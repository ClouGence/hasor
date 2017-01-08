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
import net.hasor.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
/**
 * 标准的 {@link ApiBinder} 接口包装类。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ApiBinderWrap implements ApiBinder {
    protected Logger    logger    = LoggerFactory.getLogger(getClass());
    private   ApiBinder apiBinder = null;
    //
    public ApiBinderWrap(ApiBinder apiBinder) {
        this.apiBinder = Hasor.assertIsNotNull(apiBinder);
    }
    public Environment getEnvironment() {
        return this.apiBinder.getEnvironment();
    }
    public Set<Class<?>> findClass(final Class<?> featureType) {
        return this.apiBinder.findClass(featureType);
    }
    @Override
    public Set<Class<?>> findClass(Class<?> featureType, String... scanPackages) {
        return this.apiBinder.findClass(featureType, scanPackages);
    }
    @Override
    public <T extends ApiBinder> T tryCast(Class<T> castApiBinder) {
        return this.apiBinder.tryCast(castApiBinder);
    }
    public void installModule(final Module module) throws Throwable {
        this.apiBinder.installModule(module);
    }
    public void bindInterceptor(String matcherExpression, MethodInterceptor interceptor) {
        this.apiBinder.bindInterceptor(matcherExpression, interceptor);
    }
    public void bindInterceptor(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor) {
        this.apiBinder.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }
    public <T> BindInfo<T> getBindInfo(String bindID) {
        return this.apiBinder.getBindInfo(bindID);
    }
    public <T> BindInfo<T> getBindInfo(Class<T> bindType) {
        return this.apiBinder.getBindInfo(bindType);
    }
    public <T> List<BindInfo<T>> findBindingRegister(Class<T> bindType) {
        return this.apiBinder.findBindingRegister(bindType);
    }
    public <T> BindInfo<T> findBindingRegister(String withName, Class<T> bindType) {
        return this.apiBinder.findBindingRegister(withName, bindType);
    }
    public <T> NamedBindingBuilder<T> bindType(Class<T> type) {
        return this.apiBinder.bindType(type);
    }
    public <T> MetaDataBindingBuilder<T> bindType(Class<T> type, T instance) {
        return this.apiBinder.bindType(type, instance);
    }
    public <T> InjectPropertyBindingBuilder<T> bindType(Class<T> type, Class<? extends T> implementation) {
        return this.apiBinder.bindType(type, implementation);
    }
    public <T> ScopedBindingBuilder<T> bindType(Class<T> type, Provider<T> provider) {
        return this.apiBinder.bindType(type, provider);
    }
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type) {
        return this.apiBinder.bindType(withName, type);
    }
    public <T> MetaDataBindingBuilder<T> bindType(String withName, Class<T> type, T instance) {
        return this.apiBinder.bindType(withName, type, instance);
    }
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type, Class<? extends T> implementation) {
        return this.apiBinder.bindType(withName, type, implementation);
    }
    public <T> LifeBindingBuilder<T> bindType(String withName, Class<T> type, Provider<T> provider) {
        return this.apiBinder.bindType(withName, type, provider);
    }
    //
    public Provider<Scope> registerScope(String scopeName, Scope scope) {
        return this.apiBinder.registerScope(scopeName, scope);
    }
    public Provider<Scope> registerScope(String scopeName, Provider<Scope> scope) {
        return this.apiBinder.registerScope(scopeName, scope);
    }
}