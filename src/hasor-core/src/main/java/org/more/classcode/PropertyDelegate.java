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
package org.more.classcode;
/**
 * 委托属性，classcode组建提供一种新型的属性形态，传统的bean的get/set方法是针对一个私有字段。
 * 而委托属性则是导出这个get/set方法到一个接口中。从而被添加的属性可以通过接口的相应方法来负责处理。
 * @version 2010-9-3
 * @author 赵永春 (zyc@hasor.net)
 */
public interface PropertyDelegate<T> {
    /**获取这个委托属性的属性类型。*/
    public Class<? extends T> getType();
    /**该委托属性的get方法，参数是属性所处的对象。*/
    public T get() throws Throwable;
    /**该委托属性的set方法，第一个参数是属性所处的对象，第二个参数代表设置的新值。*/
    public void set(T newValue) throws Throwable;
}