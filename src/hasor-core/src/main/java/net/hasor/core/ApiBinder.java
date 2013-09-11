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
package net.hasor.core;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.binder.LinkedBindingBuilder;
/**
 * ApiBinder
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ApiBinder {
    /**获取初始化环境*/
    public Environment getEnvironment();
    /**获取用于初始化Guice的Binder。*/
    public Binder getGuiceBinder();
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * 使用下面的方法即可重新获取绑定的类型。
     * <pre>
     * AppContext :
     *   appContext.findBindingsByType(BeanInfo.class);
     * Guice :
     *   TypeLiteral INFO_DEFS = TypeLiteral.get(BeanInfo.class);
     *   appContext.getGuice().findBindingsByType(INFO_DEFS);
     * </pre>
     * */
    public <T> LinkedBindingBuilder<T> bindingType(Class<T> type);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class); */
    public <T> void bindingType(Class<T> type, T instance);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class); */
    public <T> void bindingType(Class<T> type, Class<? extends T> implementation);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class); */
    public <T> void bindingType(Class<T> type, Provider<? extends T> provider);
    /**将后面的对象绑定前一个类型上。可以通过AppContext使用绑定的类型重新获取绑定的对象。
     * @see ApiBinder#bindingType(Class); */
    public <T> void bindingType(Class<T> type, Key<? extends T> targetKey);
    //
    //
    /**注册一个bean。*/
    public BeanBindingBuilder newBean(String beanName);
    /**负责注册Bean*/
    public static interface BeanBindingBuilder {
        /**别名*/
        public BeanBindingBuilder aliasName(String aliasName);
        /**bean绑定的类型。*/
        public <T> LinkedBindingBuilder<T> bindType(Class<T> beanClass);
    }
}