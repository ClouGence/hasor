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
package net.hasor.core.binder;
import net.hasor.core.BindInfo;
import net.hasor.core.Scope;

import java.util.function.Supplier;

/**
 * Bean配置接口，用于对Bean信息进行全方面配置。
 * @version : 2014年7月2日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface BindInfoBuilder<T> {
    /**
     * 为绑定设置ID。
     * @param newID newID
     */
    public void setBindID(String newID);

    /**
     * 为类型绑定一个名称。
     * @param bindName 名称
     */
    public void setBindName(String bindName);

    /**
     * 为类型绑定一个实现，当获取类型实例时其实获取的是实现对象。
     * @param sourceType 实现类
     */
    public void setSourceType(Class<? extends T> sourceType);

    /**
     * 设置元信息。
     * @param key metaData key
     * @param value metaData value
     */
    public void setMetaData(String key, Object value);

    /**
     * 开发者自定义的{@link Supplier}。
     * @param customerProvider 设置自定义{@link Supplier}
     */
    public void setCustomerProvider(Supplier<? extends T> customerProvider);

    /**
     * 加入一个 Scope。
     * @param scopeProvider 命名空间
     */
    public void addScopeProvider(Supplier<Scope> scopeProvider);

    /**
     * 加入一个 Scope。
     * @param scopeProvider 命名空间
     */
    public default void addScopeProvider(Supplier<Scope>[] scopeProvider) {
        if (scopeProvider != null && scopeProvider.length > 0) {
            for (Supplier<Scope> scope : scopeProvider) {
                this.addScopeProvider(scope);
            }
        }
    }

    /**
     * 清空已经加入的所有 Scope。
     */
    public void clearScope();

    /**
     * 设置构造参数。
     * @param index 参数索引
     * @param paramType 参数类型
     * @param valueProvider 参数值
     */
    public void setConstructor(int index, Class<?> paramType, Supplier<?> valueProvider);

    /**
     * 设置构造参数。
     * @param index 参数索引
     * @param paramType 参数类型
     * @param valueInfo 参数值
     */
    public void setConstructor(int index, Class<?> paramType, BindInfo<?> valueInfo);

    /**
     * 添加依赖注入。
     * @param property 属性名
     * @param valueProvider 属性值
     */
    public void addInject(String property, Supplier<?> valueProvider);

    /**
     * 添加依赖注入。
     * @param property 属性名
     * @param valueInfo 属性值
     */
    public void addInject(String property, BindInfo<?> valueInfo);

    /**
     * 转化为{@link BindInfo}类型对象。
     * @return 返回{@link BindInfo}类型对象。
     */
    public BindInfo<T> toInfo();

    /**
     * 设置初始化方法，一个无参的方法。例如：public void init(){ ... }。
     * @param methodName 方法名。
     */
    public void initMethod(String methodName);

    /**
     * 设置初始化方法，一个无参的方法。例如：public void init(){ ... }。
     * @param methodName 方法名。
     */
    public void destroyMethod(String methodName);
}