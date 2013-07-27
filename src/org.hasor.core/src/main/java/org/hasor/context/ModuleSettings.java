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
package org.hasor.context;
/**
 * 该接口可以配置模块信息
 * @version : 2013-7-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ModuleSettings extends ModuleInfo {
    /***/
    public void afterMe(Class<? extends HasorModule> targetModule);
    /**要求目标模块的启动在当前模块之前进行启动。<br/>
     * 注意：该方法仅仅要求在目标模块之后启动。但目标模块是否启动并无强制要求。*/
    public void beforeMe(Class<? extends HasorModule> targetModule);
    /**跟随目标模块启动而启动。<br/> 
     * 注意：该方法要求在目标模块启动之后在启动。*/
    public void followTarget(Class<? extends HasorModule> targetModule);
    /**绑定配置文件命名空间*/
    public void bindingSettingsNamespace(String settingsNamespace);
    /**设置显示名称*/
    public void setDisplayName(String displayName);
    /**设置描述信息*/
    public void setDescription(String description);
}