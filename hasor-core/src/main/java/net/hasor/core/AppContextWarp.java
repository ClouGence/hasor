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
package net.hasor.core;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Hasor的核心接口，它为应用程序提供了一个统一的配置界面和运行环境。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@hasor.net)
 */
public class AppContextWarp implements AppContext {
    private Supplier<AppContext> appContextProvider;
    private AppContext           appContext;

    public AppContextWarp(AppContext appContext) {
        this.appContext = appContext;
    }

    public AppContextWarp(Supplier<AppContext> appContextProvider) {
        this.appContextProvider = appContextProvider;
    }

    public AppContext getAppContext() {
        if (this.appContext != null) {
            return this.appContext;
        }
        if (this.appContextProvider != null) {
            return this.appContextProvider.get();
        }
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return this.getAppContext().getEnvironment();
    }

    @Override
    public void start(Module... modules) throws Throwable {
        this.getAppContext().start(modules);
    }

    @Override
    public boolean isStart() {
        return this.getAppContext().isStart();
    }

    @Override
    public void shutdown() {
        this.getAppContext().shutdown();
    }

    @Override
    public void join(long timeout, TimeUnit unit) {
        this.getAppContext().join(timeout, unit);
    }

    @Override
    public void waitSignal(Object signal, long timeout, TimeUnit unit) throws InterruptedException {
        this.getAppContext().waitSignal(signal, timeout, unit);
    }

    @Override
    public Class<?> getBeanType(String bindID) {
        return this.getAppContext().getBeanType(bindID);
    }

    @Override
    public String[] getBindIDs() {
        return this.getAppContext().getBindIDs();
    }

    @Override
    public boolean containsBindID(String bindID) {
        return this.getAppContext().containsBindID(bindID);
    }

    @Override
    public boolean isSingleton(BindInfo<?> bindInfo) {
        return this.getAppContext().isSingleton(bindInfo);
    }

    @Override
    public boolean isSingleton(Class<?> targetType) {
        return this.getAppContext().isSingleton(targetType);
    }

    @Override
    public <T> BindInfo<T> getBindInfo(String bindID) {
        return this.getAppContext().getBindInfo(bindID);
    }

    @Override
    public <T> T justInject(T object, Class<?> beanType) {
        return this.getAppContext().justInject(object, beanType);
    }

    @Override
    public <T> T justInject(T object, BindInfo<?> bindInfo) {
        return this.getAppContext().justInject(object, bindInfo);
    }

    @Override
    public <T> Supplier<? extends T> getProvider(String bindID) {
        return this.getAppContext().getProvider(bindID);
    }

    @Override
    public <T> Supplier<? extends T> getProvider(Class<T> targetClass, Object... params) {
        return this.getAppContext().getProvider(targetClass, params);
    }

    @Override
    public <T> Supplier<? extends T> getProvider(Constructor<T> bindType, Object... params) {
        return this.getAppContext().getProvider(bindType, params);
    }

    @Override
    public <T> Supplier<? extends T> getProvider(BindInfo<T> info) {
        return this.getAppContext().getProvider(info);
    }

    @Override
    public <T> List<BindInfo<T>> findBindingRegister(Class<T> bindType) {
        return this.getAppContext().findBindingRegister(bindType);
    }

    @Override
    public Supplier<Scope> findScope(String scopeName) {
        return this.getAppContext().findScope(scopeName);
    }

    @Override
    public Object getMetaData(String key) {
        return this.getAppContext().getMetaData(key);
    }

    @Override
    public void setMetaData(String key, Object value) {
        this.getAppContext().setMetaData(key, value);
    }

    @Override
    public void removeMetaData(String key) {
        this.getAppContext().removeMetaData(key);
    }
}