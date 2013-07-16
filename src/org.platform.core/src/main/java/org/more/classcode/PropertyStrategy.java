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
package org.more.classcode;
/**
 * 该接口是基本属性和委托属性的策略接口，这些附加的属性在输出到class之前都会通过该接口来确定是否忽略它以及属性的读写策略。
 * @version 2010-9-3
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PropertyStrategy extends BaseStrategy {
    /**
     * 该方法是用于决定是否忽略附加的属性。如果返回true则表示忽略这个属性，被忽略的属性不会再次调用其isReadOnly和isWriteOnly方法。
     * @param name 附加的属性名。
     * @param type 附加的属性类型。
     * @param isDelegate 该属性是否是一个代理属性。
     * @return 如果忽略这个属性则返回true，否则返回false。
     */
    public boolean isIgnore(String name, Class<?> type, boolean isDelegate);
    /**
     * 该方法用于确定即将输出的属性是否为一个只读属性。
     * @param name 附加的属性名。
     * @param type 附加的属性类型。
     * @param isDelegate 该属性是否是一个代理属性。
     * @return 如果该属性是一个只读属性则返回true，否则返回false。
     */
    public boolean isReadOnly(String name, Class<?> type, boolean isDelegate);
    /**
     * 该方法用于确定即将输出的属性是否为一个只写属性。
     * @param name 附加的属性名。
     * @param type 附加的属性类型。
     * @param isDelegate 该属性是否是一个代理属性。
     * @return 如果该属性是一个只写属性则返回true，否则返回false。
    */
    public boolean isWriteOnly(String name, Class<?> type, boolean isDelegate);
}