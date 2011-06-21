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
package org.more.hypha.commons.logic;
/**
 * 用来封装{@link AbstractBeanBuilderEx}执行点的代码。
 * @version : 2011-6-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassData {
    /**字节码表示类，类名。*/
    public String className = null;
    /**类的字节码数据。*/
    public byte[] bytes     = null;
};