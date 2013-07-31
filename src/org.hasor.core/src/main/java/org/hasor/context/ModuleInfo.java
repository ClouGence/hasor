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
import java.util.List;
/**
 * 该接口只能获取模块信息（注意：该接口不要尝试去实现它）
 * @version : 2013-7-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ModuleInfo {
    /**获取绑定的配置文件命名空间*/
    public String getSettingsNamespace();
    /**获取显示名称*/
    public String getDisplayName();
    /**获取描述信息*/
    public String getDescription();
    /**获取模块信息所表述的模块对象*/
    public HasorModule getModuleObject();
    /**获取模块的依赖模块*/
    public List<Dependency> getDependency();
    /**当模块没有通过configuration方法时false，否则为true.*/
    public boolean isReady();
    /**当模块刚刚经过start阶段，该方法返回值为true。否则返回值为false.*/
    public boolean isRunning();
    /**当模块没有通过init阶段返回值为false，否则为true.*/
    public boolean isInit();
    /**判断依赖的模块是否已经就绪。如果依赖为一个可选依赖，则被依赖项目即使没有ready也会被判定为ready。*/
    public boolean isDependencyReady();
    /**判断依赖的模块是否已经启动。如果依赖为一个可选依赖，则被依赖项目即使没有Running也会被判定为Running。*/
    public boolean isDependencyRunning();
    /**判断依赖的模块是否都通过了isInit。如果依赖为一个可选依赖，则被依赖项目即使没有init也会被判定为init。*/
    public boolean isDependencyInit();
}