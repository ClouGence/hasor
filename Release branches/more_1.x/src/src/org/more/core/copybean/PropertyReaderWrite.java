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
import java.io.Serializable;
/**
 * 对象属性读写器。该类可以将某个属性保存到另外的一个对象上或者将某个属性从某个对象上读取出来。
 * 该类是一个抽象类，其子类决定具体应该如何读写属性。如果子类不支持读或者写该属性则应当重写父类
 * canReader和canWrite方法。canReader和canWrite方法在父类中始终返回true。
 * Date : 2009-5-15
 * @author 赵永春
 */
public abstract class PropertyReaderWrite implements Serializable {
    /** 属性名 */
    private String name   = null;
    /** 存放属性的对象 */
    private Object object = null;
    /**
     * 读取目标属性值，该方法应当由子类实现。
     * @return 返回目标属性值，该方法应当由子类实现。
     */
    public abstract Object get();
    /**
     * 将指定的值写入该属性对象，该方法应当由子类实现。
     * @param value 要写入的值内容。
     */
    public abstract void set(Object value);
    /**
     * 测试该属性读取器是否支持读操作。如果支持返回true否则返回false。
     * @return 返回测试该属性读写器是否支持读操作。如果支持返回true否则返回false。
     */
    public boolean canReader() {
        return true;
    }
    /**
     * 测试该属性读取器是否支持写操作。如果支持返回true否则返回false。
     * @return 返回测试该属性读写器是否支持读操作。如果支持返回true否则返回false。
     */
    public boolean canWrite() {
        return true;
    }
    /**
     * 获得该属性读写器表示的属性名称。
     * @return 返回该属性读写器表示的属性名称。
     */
    public String getName() {
        return this.name;
    }
    /**
     * 设置属性名
     * @param name 要设置的属性名
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * 获得该属性读写器准备操作的属性名。
     * @return 返回该属性读写器准备操作的属性名。
     */
    public Object getObject() {
        return object;
    }
    /**
     * 设置存放属性的对象
     * @param object 要设置的存放属性的对象
     */
    public void setObject(Object object) {
        this.object = object;
    }
    /**
     * 获得当前属性的属性类型。
     * @return 返回当前属性的属性类型。
     * @throws Exception 
     */
    public abstract Class<?> getPropertyClass();
}
