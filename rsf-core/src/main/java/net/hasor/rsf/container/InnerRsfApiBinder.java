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
package net.hasor.rsf.container;
import net.hasor.core.*;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.RsfEvent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class InnerRsfApiBinder extends AbstractRsfBindBuilder implements RsfApiBinder {
    private ApiBinder      apiBinder;
    private RsfEnvironment rsfEnvironment;
    public InnerRsfApiBinder(ApiBinder apiBinder, RsfEnvironment rsfEnvironment) {
        super();
        this.apiBinder = new ApiBinderWrap(Hasor.assertIsNotNull(apiBinder));
        this.rsfEnvironment = Hasor.assertIsNotNull(rsfEnvironment);
    }
    @Override
    protected <T> RegisterReference<T> addService(ServiceDefine<T> serviceDefine) {
        EventContext ec = getEnvironment().getEventContext();
        ec.pushListener(RsfEvent.Rsf_Initialized, new EventListener<Object>() {
        });
        return null;
        s
    }
    @Override
    protected void addShareFilter(FilterDefine filterDefine) {
    }
    //
    //
    @Override
    public RsfEnvironment getEnvironment() {
        return this.rsfEnvironment;
    }
    @Override
    public Set<Class<?>> findClass(Class<?> featureType) {
        return this.apiBinder.findClass(featureType);
    }
    @Override
    public void installModule(Module module) throws Throwable {
        this.apiBinder.installModule(module);
    }
    @Override
    public void bindInterceptor(String matcherExpression, MethodInterceptor interceptor) {
        this.apiBinder.bindInterceptor(matcherExpression, interceptor);
    }
    @Override
    public void bindInterceptor(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor) {
        this.apiBinder.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }
    @Override
    public <T> BindInfo<T> getBindInfo(String bindID) {
        return this.apiBinder.getBindInfo(bindID);
    }
    @Override
    public <T> BindInfo<T> getBindInfo(Class<T> bindType) {
        return this.apiBinder.getBindInfo(bindType);
    }
    @Override
    public <T> List<BindInfo<T>> findBindingRegister(Class<T> bindType) {
        return this.apiBinder.findBindingRegister(bindType);
    }
    @Override
    public <T> BindInfo<T> findBindingRegister(String withName, Class<T> bindType) {
        return this.apiBinder.findBindingRegister(withName, bindType);
    }
    @Override
    public <T> NamedBindingBuilder<T> bindType(Class<T> type) {
        return this.apiBinder.bindType(type);
    }
    @Override
    public <T> MetaDataBindingBuilder<T> bindType(Class<T> type, T instance) {
        return this.apiBinder.bindType(type, instance);
    }
    @Override
    public <T> InjectPropertyBindingBuilder<T> bindType(Class<T> type, Class<? extends T> implementation) {
        return this.apiBinder.bindType(type, implementation);
    }
    @Override
    public <T> ScopedBindingBuilder<T> bindType(Class<T> type, Provider<T> provider) {
        return this.apiBinder.bindType(type, provider);
    }
    @Override
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type) {
        return this.apiBinder.bindType(withName, type);
    }
    @Override
    public <T> MetaDataBindingBuilder<T> bindType(String withName, Class<T> type, T instance) {
        return this.apiBinder.bindType(withName, type, instance);
    }
    @Override
    public <T> InjectPropertyBindingBuilder<T> bindType(String withName, Class<T> type, Class<? extends T> implementation) {
        return this.apiBinder.bindType(withName, type, implementation);
    }
    @Override
    public <T> LifeBindingBuilder<T> bindType(String withName, Class<T> type, Provider<T> provider) {
        return this.apiBinder.bindType(withName, type, provider);
    }
    @Override
    public Provider<Scope> registerScope(String scopeName, Scope scope) {
        return this.apiBinder.registerScope(scopeName, scope);
    }
    @Override
    public Provider<Scope> registerScope(String scopeName, Provider<Scope> scopeProvider) {
        return this.apiBinder.registerScope(scopeName, scopeProvider);
    }
}