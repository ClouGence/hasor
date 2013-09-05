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
package net.hasor.context;
import java.util.List;
/**
 * 用于表示某个模块的依赖关系。
 * @version : 2013-7-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Dependency {
    /**获取模块信息*/
    public ModuleInfo getModuleInfo();
    /**表明该依赖是否为可选的依赖（true表示可选的，false表示强制的）*/
    public boolean isOption();
    /**获取模块的依赖项目*/
    public List<Dependency> getDependency();
}