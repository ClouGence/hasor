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
package org.more.services.submit.xml;
import java.lang.reflect.Method;
/**
 * 该类的目的是将一个方法的定位字符串和一个地址进行映射，这样可以通过简单的地址访问复杂的方法路径。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class B_AnnoActionInfo {
    /**Action可能存在的包地址。*/
    public String packageString = null;
    /**Action的真实地址。*/
    public Method actionPath    = null;
    /**Action映射出去的地址。*/
    public String mappingPath   = null;
}