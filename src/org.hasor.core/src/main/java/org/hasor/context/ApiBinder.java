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
package org.hasor.context;
import java.util.Set;
import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;
/**
 * ApiBinder
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ApiBinder {
    /**获取初始化环境*/
    public InitContext getInitContext();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> getClassSet(Class<?> featureType);
    /**获取用于初始化Guice的Binder。*/
    public Binder getGuiceBinder();
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