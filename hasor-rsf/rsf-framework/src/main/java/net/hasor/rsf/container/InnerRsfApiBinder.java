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
import net.hasor.core.aop.PropertyDelegate;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.spi.SpiJudge;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.utils.supplier.TypeSupplier;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 服务注册器
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年11月12日
 */
public class InnerRsfApiBinder extends AbstractRsfBindBuilder implements RsfApiBinder {
    private final ApiBinder      apiBinder;
    private final RsfEnvironment rsfEnvironment;

    protected InnerRsfApiBinder(ApiBinder apiBinder, RsfEnvironment rsfEnvironment) {
        super();
        this.apiBinder = new ApiBinderWrap(Objects.requireNonNull(apiBinder));
        this.rsfEnvironment = Objects.requireNonNull(rsfEnvironment);
    }

    @Override
    protected <T> Supplier<? extends T> toProvider(BindInfo<T> bindInfo) {
        return this.apiBinder.getProvider(bindInfo);
    }

    @Override
    protected <T> Supplier<? extends T> toProvider(Class<T> bindInfo) {
        return this.apiBinder.getProvider(bindInfo);
    }

    @Override
    protected <T> RsfBindInfo<T> addService(ServiceDefine<T> serviceDefine) {
        this.bindType(ServiceDefine.class).uniqueName().toInstance(serviceDefine);
        return serviceDefine;
    }

    @Override
    protected void addShareFilter(FilterDefine filterDefine) {
        this.bindType(FilterDefine.class).uniqueName().toInstance(filterDefine);
    }

    @Override
    public RsfEnvironment getEnvironment() {
        return this.rsfEnvironment;
    }

    @Override
    public <T> ConfigurationBuilder<T> rsfService(BindInfo<T> bindInfo) {
        return this.rsfService(bindInfo.getBindType()).toInfo(bindInfo);
    }

    @Override
    public Set<Class<?>> findClass(Class<?> featureType) {
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

    @Override
    public ApiBinder installModule(Module... module) throws Throwable {
        return this.apiBinder.installModule(module);
    }

    @Override
    public boolean isSingleton(BindInfo<?> bindInfo) {
        return this.apiBinder.isSingleton(bindInfo);
    }

    @Override
    public boolean isSingleton(Class<?> targetType) {
        return this.apiBinder.isSingleton(targetType);
    }

    @Override
    public ApiBinder loadModule(Class<?> moduleType, TypeSupplier typeSupplier) {
        return this.apiBinder.loadModule(moduleType, typeSupplier);
    }

    @Override
    public void bindInterceptor(String matcherExpression, MethodInterceptor interceptor) {
        this.apiBinder.bindInterceptor(matcherExpression, interceptor);
    }

    @Override
    public void bindInterceptor(Predicate<Class<?>> matcherClass, Predicate<Method> matcherMethod, MethodInterceptor interceptor) {
        this.apiBinder.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }

    @Override
    public LinkedBindingBuilder<PropertyDelegate> dynamicProperty(Predicate<Class<?>> matcherClass, String propertyName, Class<?> propertyType) {
        return this.apiBinder.dynamicProperty(matcherClass, propertyName, propertyType);
    }

    @Override
    public LinkedBindingBuilder<PropertyDelegate> dynamicReadOnlyProperty(Predicate<Class<?>> matcherClass, String propertyName, Class<?> propertyType) {
        return this.apiBinder.dynamicReadOnlyProperty(matcherClass, propertyName, propertyType);
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
    public <T extends EventListener> void bindSpiListener(Class<T> spiType, Supplier<? extends T> listener) {
        this.apiBinder.bindSpiListener(spiType, listener);
    }

    @Override
    public <T extends EventListener> void bindSpiJudge(Class<T> spiType, Supplier<SpiJudge> spiJudgeSupplier) {
        this.apiBinder.bindSpiJudge(spiType, spiJudgeSupplier);
    }

    @Override
    public <T extends Scope> Supplier<T> bindScope(String scopeName, Supplier<T> scopeSupplier) {
        return this.apiBinder.bindScope(scopeName, scopeSupplier);
    }

    @Override
    public Supplier<Scope> findScope(String scopeName) {
        return this.apiBinder.findScope(scopeName);
    }
}
