/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.rsf;
/**
 * 服务配置更新器。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfUpdater {
    /**更新服务地址本计算规则（服务级）*/
    public void updateDefaultServiceRoute(String scriptBody);
    /**更新本地方法级地址计算脚本。*/
    public void updateDefaultMethodRoute(String scriptBody);
    /**更新本地参数级地址计算脚本。*/
    public void updateDefaultArgsRoute(String scriptBody);
    /**更新服务路由策略*/
    public void updateDefaultFlowControl(String flowControl);
    //
    /**更新服务地址本计算规则（服务级）*/
    public void updateServiceRoute(String serviceID, String scriptBody);
    /**更新本地方法级地址计算脚本。*/
    public void updateMethodRoute(String serviceID, String scriptBody);
    /**更新本地参数级地址计算脚本。*/
    public void updateArgsRoute(String serviceID, String scriptBody);
    /**更新服务路由策略*/
    public void updateFlowControl(String serviceID, String flowControl);
}