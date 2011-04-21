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
package org.more.hypha;
/**
 * 扩展点，用于开放框架内部的流程。使外面程序可以参与或控制执行。{@link ExpandPoint}类型是所有其他扩展点的基类。
 * @version 2010-12-28
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ExpandPoint {
    /**该方法是执行扩展点的综合入口方法，各子类在重写该方法时来再次确认调用的扩展点本体方法。*/
    public Object doIt(Object returnObj, Object[] params);
};