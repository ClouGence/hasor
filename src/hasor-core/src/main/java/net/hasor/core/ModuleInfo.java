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
package net.hasor.core;
import java.util.List;
/**
 * 该接口只能获取模块信息（注意：该接口不要尝试去实现它）
 * @version : 2013-7-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ModuleInfo {
    /**获取绑定的配置文件命名空间*/
    public String getSettingsNamespace();
    /**获取显示名称*/
    public String getDisplayName();
    /**获取描述信息*/
    public String getDescription();
    /**设置显示名称*/
    public void setDisplayName(String displayName);
    /**设置描述信息*/
    public void setDescription(String description);
    /**获取模块信息所表述的模块对象*/
    public Module getTarget();
    /**获取模块的依赖模块*/
    public List<Dependency> getDependency();
    //
    /**当模块是否准备好，当模块经过了init过程视为准备好.*/
    public boolean isReady();
    /**当模块是否准备好，当模块经过了init过程视为准备好.*/
    public boolean isDependencyReady();
    /**模块是否启动.*/
    public boolean isStart();
}