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
/**
 * 
 * @version : 2014年7月2日
 * @author 赵永春(zyc@hasor.net)
 */
public interface BindInfoBuilder<T> extends BindInfo<T> {
    //    /**为绑定设置ID*/
    //    public void setID(String newID);
    /**为类型绑定一个名称。*/
    public void setBindName(String bindName);
    /**获取注册的类型*/
    public Class<T> getBindType();
    //
    /**为类型绑定一个实现，当获取类型实例时其实获取的是实现对象。*/
    public void setSourceType(Class<? extends T> sourceType);
    /**设置元信息*/
    public void setMetaData(String key, Object value);
    /**标记为单例*/
    public void setSingleton(boolean singleton);
    /**为类型绑定一个Provider。*/
    public void setCustomerProvider(Provider<T> customerProvider);
    /**将类型发布到一个固定的命名空间内。*/
    public void setScopeProvider(Provider<Scope> scopeProvider);
    //
    public void setInitParam(int index, Class<?> paramType, Provider<?> valueProvider);
    public void setInitParam(int index, Class<?> paramType, BindInfo<?> valueInfo);
    //
    public void addInject(String property, Provider<?> valueProvider);
    public void addInject(String property, BindInfo<?> valueInfo);
    //
    public BindInfo<T> toInfo();
}