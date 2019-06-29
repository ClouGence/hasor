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
package net.hasor.core.container;
import net.hasor.core.BindInfo;
import net.hasor.core.Scope;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.scope.SingletonScope;
import net.hasor.core.spi.ScopeProvisionListener;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
/**
 * 作用域管理器
 * @version : 2019年06月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ScopContainer implements Closeable {
    private        SingletonScope                             singletonScope = new SingletonScope();
    private static Scope                                      PROTOTYPE_SCOPE = new Scope() {
        @Override
        public <T> Supplier<T> scope(Object key, Supplier<T> provider) {
            return provider;
        }
    };
    private        SpiCallerContainer                         spiContainer    = null;
    private        ConcurrentHashMap<String, Supplier<Scope>> scopeMapping    = new ConcurrentHashMap<>();
    //
    public ScopContainer(SpiCallerContainer spiContainer) {
        this.spiContainer = Objects.requireNonNull(spiContainer, "SpiCallerContainer si null.");
    }
    //
    /**
     * 为一个已经注册的作用域起一个别名。
     * @param scopeName 已经存在的作用域名称
     * @param aliasName 新的别名
     * @return 成功注册之后返回它自身, 如果存在同名的scope那么会返回第一次注册那个 scope。
     */
    public <T extends Scope> Supplier<T> registerAlias(String scopeName, String aliasName) {
        if (this.scopeMapping.containsKey(aliasName)) {
            throw new IllegalStateException("the scope name already exists.");
        }
        //
        Supplier<Scope> oldScope = this.scopeMapping.get(scopeName);
        if (oldScope == null) {
            throw new IllegalStateException("reference Scope does not exist.");
        }
        this.scopeMapping.put(aliasName, oldScope);
        return (Supplier<T>) oldScope;
    }
    /**
     * 注册作用域。
     * @param scopeName 作用域名称
     * @param scopeProvider 作用域
     * @return 成功注册之后返回scopeProvider自身, 如果存在同名的scope那么会引发异常。
     */
    public <T extends Scope> Supplier<T> registerScope(String scopeName, Supplier<T> scopeProvider) {
        // .重复检测
        if (this.scopeMapping.containsKey(scopeName)) {
            throw new IllegalStateException("the scope name already exists.");
        }
        // .添加到 Scope中
        Supplier<? extends Scope> oldScope = this.scopeMapping.putIfAbsent(scopeName, (Supplier<Scope>) scopeProvider);
        if (oldScope == null) {
            oldScope = scopeProvider;
        }
        // .触发 SPI
        this.spiContainer.callSpi(ScopeProvisionListener.class, listener -> {
            listener.newScope(scopeName, scopeProvider);
        });
        return (Supplier<T>) oldScope;
    }
    //
    /**
     * 查找某个作用域。
     * @param scopeName 作用域名称
     */
    public Supplier<Scope> findScope(String scopeName) {
        return this.scopeMapping.get(scopeName);
    }
    //
    /**
     * 根据 BindInfo 查找作用域。
     * @param bindInfo 参考的 BindInfo
     */
    public Supplier<Scope> findScope(BindInfo<?> bindInfo) {
        DefaultBindInfoProviderAdapter<?> adapter = (DefaultBindInfoProviderAdapter<?>) bindInfo;
        Supplier<Scope> scopeProvider = adapter.getCustomerScopeProvider();
        if (scopeProvider != null) {
            return scopeProvider;
        }
        //
        Class<?> sourceType = adapter.getSourceType();
        if (sourceType == null) {
            sourceType = adapter.getBindType();
        }
        //
        return findScope(ContainerUtils.findImplClass(sourceType));
    }
    //
    /** 根据类型上的注解查找对应的作用域。 */
    public Supplier<Scope> findScope(Class<?> targetType) {
        if (targetType == null) {
            return null;
        }
        Annotation[] annos = targetType.getAnnotations();
        for (Annotation anno : annos) {
            if (anno.annotationType().getAnnotation(javax.inject.Scope.class) == null) {
                continue;
            }
            String scopeName = anno.annotationType().getName();
            Supplier<Scope> supplier = findScope(anno.annotationType().getName());
            if (supplier != null) {
                return supplier;
            } else {
                throw new IllegalStateException(scopeName + " scope undefined.");
            }
        }
        return null;
    }
    //
    /**
     * 判断是否是单例，单列对象需要标记了 Singleton 注解。或者通过别名的方式映射到 Singleton 上。
     * @see net.hasor.core.Singleton
     * @see javax.inject.Singleton
     */
    public boolean isSingleton(BindInfo<?> bindInfo) {
        Supplier<Scope> scope = findScope(bindInfo);
        if (scope != null) {
            return scope.get() == singletonScope;
        }
        return false;
    }
    //
    //
    //
    public void doInitialize() {
        this.singletonScope = new SingletonScope();
        this.scopeMapping.put(net.hasor.core.Prototype.class.getName(), InstanceProvider.of(PROTOTYPE_SCOPE));
        this.scopeMapping.put(net.hasor.core.Singleton.class.getName(), InstanceProvider.of(singletonScope));
        this.scopeMapping.put(javax.inject.Singleton.class.getName(), InstanceProvider.of(singletonScope));
    }
    @Override
    public void close() {
        this.scopeMapping.clear();
    }
}