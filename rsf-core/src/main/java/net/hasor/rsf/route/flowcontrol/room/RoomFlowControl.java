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
package net.hasor.rsf.route.flowcontrol.room;
import java.util.Arrays;
import java.util.List;
import net.hasor.core.Settings;
import net.hasor.rsf.route.rule.AbstractRule;
/**
 * 机房流量控制规则，用来控制跨机房调用。
 * <pre>
 * 配置实例：
 * &lt;flowControl enable="true|false" type="LocalPreferred"&gt;
 *   &lt;threshold&gt;0.3&lt;/threshold&gt;
 *   &lt;exclusions&gt;172.23.*,172.19.*&lt;/exclusions&gt;
 * &lt;/flowControl&gt;
 * </pre>
 * 解释： 对某一服务，开启本机房优先调用策略
 * 但当本机房内的可用机器的数量占服务地址全部数量的比例小于0.3时，本机房优先调用策略失效，启用跨机房调用。
 * 该规则对以下网段的服务消费者不生效：172.23.*,172.19.*
 */
public class RoomFlowControl extends AbstractRule {
    private float        threshold;
    private List<String> exclusions;
    //
    public void paserControl(Settings settings) {
        this.enable(settings.getBoolean("flowControl.enable"));
        this.threshold = settings.getFloat("flowControl.threshold");
        String exclusions = settings.getString("flowControl.exclusions");
        this.exclusions = Arrays.asList(exclusions.split(","));
    }
    public float getThreshold() {
        return this.threshold;
    }
    public List<String> getExclusions() {
        return this.exclusions;
    }
    //
    /**
     * 是否启用本地机房优先规则
     * @param allAmount 所有可用地址数量
     * @param localAmount 本地机房地址数量
     */
    public boolean isLocalPreferred(int allAmount, int localAmount) {
        if (localAmount == 0 || !this.enable()) {
            return false;
        }
        float value = (localAmount + 0.0F) / allAmount;
        if (value >= this.getThreshold()) {
            return true;
        }
        return false;
    }
    //
}