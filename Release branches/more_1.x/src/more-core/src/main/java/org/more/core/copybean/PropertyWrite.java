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
package org.more.core.copybean;
import java.lang.reflect.InvocationTargetException;
/**
 * 对象属性写入器。通过该接口可以将一个值写入到bean中。
 * {@link #getTargetClass()}方法返回该读取器所支持的类型。
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PropertyWrite<T> {
    /**
     * 读取目标属性值，该方法应当由子类实现。
     * @param propertyName 要写入的名称
     * @param target 要写入的目标bean
     * @param newValue 写入的新值
     * @return 返回目标属性值，该方法应当由子类实现。
     */
    public boolean writeProperty(String propertyName, T target, Object newValue) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
    /**
     * 测试该属性读取器是否支持写操作。如果支持返回true否则返回false。
     * @param propertyName 要写入的名称
     * @param target 要写入的目标bean
     * @param newValue 写入的新值
     * @return 返回测试该属性读写器是否支持读操作。如果支持返回true否则返回false。
     */
    public boolean canWrite(String propertyName, T target, Object newValue);
    /** 获得属性读取器所支持的bean类型。*/
    public Class<?> getTargetClass();
}