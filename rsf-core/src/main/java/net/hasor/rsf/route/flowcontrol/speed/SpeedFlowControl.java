/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.route.flowcontrol.speed;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.hasor.core.Settings;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.route.rule.AbstractRule;
import org.more.logger.LoggerHelper;
/**
 * 基于QoS的速率控制规则。
 * <pre>
 * 配置实例：
 * &lt;flowControl enable="true|false" type="Speed"&gt;
 *   &lt;action&gt;service|method|address&lt;/action&gt;
 *   &lt;rate&gt;20&lt;/rate&gt;             &lt;!-- 稳态速率 --&gt
 *   &lt;peak&gt;100&lt;/peak&gt;            &lt;!-- 峰值速率 --&gt
 *   &lt;timeWindow&gt;10&lt;/timeWindow&gt; &lt;!-- 时间窗口 --&gt
 * &lt;/flowControl&gt;
 * </pre>
 * 解释：根据action的配置决定RPC调用速率。
 */
public class SpeedFlowControl extends AbstractRule {
    private QoSActionEnum                    action;
    private int                              rate       = 20;
    private int                              peak       = 200;
    private int                              timeWindow = 10;
    private QoSBucket                        defaultQoSBucket;
    private ConcurrentMap<String, QoSBucket> qosBucketMap;
    //
    public void paserControl(Settings settings) {
        this.enable(settings.getBoolean("flowControl.enable"));
        this.action = settings.getEnum("flowControl.action", QoSActionEnum.class);
        this.rate = settings.getInteger("flowControl.rate");
        this.peak = settings.getInteger("flowControl.peak");
        this.timeWindow = settings.getInteger("flowControl.timeWindow");
        this.qosBucketMap = new ConcurrentHashMap<String, QoSBucket>();
        //
        if (this.action == null) {
            this.enable(false);
            LoggerHelper.logConfig("action fail. config is null.");
        }
        if (!this.enable()) {
            return;
        }
        QoSBucket qosBucket = this.createQoSBucket();
        if (!qosBucket.validate()) {
            this.enable(false);
            LoggerHelper.logConfig("QoS config validate fail. -> %s", this.defaultQoSBucket);
            return;
        }
        defaultQoSBucket = qosBucket;
    }
    //
    public boolean callCheck(RsfBindInfo<?> info, Method m, InterAddress doCallAddress) {
        if (!this.enable()) {
            return true;
        }
        //
        String key = null;
        switch (this.action) {
        case Address:
            key = doCallAddress.toString();
            break;
        case Method:
            key = m.toString();
            break;
        case Service:
            key = info.getBindID();
            break;
        }
        //
        QoSBucket qos = this.qosBucketMap.get(key);
        if (qos == null) {
            qos = this.qosBucketMap.putIfAbsent(key, this.createQoSBucket());
        }
        return qos.check();
    }
    //
    protected QoSBucket createQoSBucket() {
        QoSBucket defaultQoSBucket = new QoSBucket(this.rate, this.peak, this.timeWindow);
        if (!this.defaultQoSBucket.validate()) {
            LoggerHelper.logConfig("QoS config validate fail. -> %s", this.defaultQoSBucket);
        }
        return defaultQoSBucket;
    }
}