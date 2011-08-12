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
package org.more.submit;
import java.lang.reflect.Method;
/**
 * submit的包，action是保存在这个包下面。
 * @version : 2011-7-18
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionPackage {
    /**获取包名称*/
    public String packageName();
    /**向action包中添加一个路由映射，路由映射的目的是将action方法或路径映射到一个外部地址上。actionPath为action的内部地址。*/
    public void addRouteMapping(String mappingPath, Method actionPath);
    /**通过外部地址获取action的内部地址。*/
    public Method getActionPath(String mappingPath);
};