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
package net.hasor.rsf.route.rule;
import net.hasor.core.Settings;
/**
 * 路由规则，配置模版实例：
 * <pre>
 * &lt;flowControl id="xxx" enable="true|false"&gt;
 *   ...
 * &lt;/flowControl&gt;
 * </pre>
 * @version : 2015年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRule implements Rule {
    private String  routeID;
    private String  routebody;
    private boolean enable;
    //
    /**路由规则ID*/
    public String routeID() {
        return this.routeID;
    }
    /**路由规则原文*/
    public String rawRoute() {
        return this.routebody;
    }
    /**规则是否启用*/
    public boolean enable() {
        return this.enable;
    }
    /**设置规则是否启用*/
    protected void enable(boolean enable) {
        this.enable = enable;
    }
    public abstract void paserControl(Settings settings);
    //    /**解析规则文本为{@link Settings}*/
    //    protected Settings ruleSettings() {
    //        if (!this.enable || StringUtils.isBlank(rawRoute())) {
    //            return null;
    //        }
    //        //
    //        if (!StringUtils.startsWithIgnoreCase(rawRoute(), "<flowControl") || !StringUtils.endsWithIgnoreCase(rawRoute(), "</flowControl>")) {
    //            this.enable = false;
    //            return null;
    //        }
    //        //
    //        synchronized (this) {
    //            try {
    //                ReaderInputStream ris = new ReaderInputStream(new StringReader(this.rawRoute()));
    //                this.ruleSettings = new InputStreamSettings(ris);
    //            } catch (Exception e) {
    //                this.enable = false;
    //                LoggerHelper.logConfig("rule raw format error.", e);
    //            }
    //        }
    //        return this.ruleSettings;
    //    }
}