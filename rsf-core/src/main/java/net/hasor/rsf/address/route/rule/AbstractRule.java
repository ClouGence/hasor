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
package net.hasor.rsf.address.route.rule;
import net.hasor.core.Settings;
/**
 * 路由规则，配置模版实例：
 * <pre>
 * &lt;flowControl enable="true|false" type="Room"&gt;
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
    //
    /**设置规则是否启用*/
    protected void enable(boolean enable) {
        this.enable = enable;
    }
    /**设置规则ID*/
    protected void setRouteID(String routeID) {
        this.routeID = routeID;
    }
    /**设置规则内容*/
    protected void setRoutebody(String routebody) {
        this.routebody = routebody;
    }
    //
    //
    /**应用配置初始化规则器*/
    public abstract void paserControl(Settings settings);
}