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
import java.util.List;
/**
 * 对象属性读取器。通过该接口可以将bean上的某个属性读取出来。
 * {@link #getTargetClass()}方法返回该读取器所支持的类型。
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PropertyReader<T> {
    /**获取属性名。*/
    public List<String> getPropertyNames(T target);
    /**
     * 读取目标属性值，该方法应当由子类实现。
     * @return 返回目标属性值，该方法应当由子类实现。
     */
    public Object readProperty(String propertyName, T target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
    /**返回一个boolean，该值表示了该属性是否可以被读。*/
    public boolean canReader(String propertyName, T target);
    /** 获得属性读取器所支持的bean类型。*/
    public Class<?> getTargetClass();
}