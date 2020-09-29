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
import java.util.function.Supplier;

/**
 *
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
@FunctionalInterface
public interface Scope {
    /**
     * 加入作用域
     * @param key 加入作用域的key。
     * @param provider 对象 Provider。
     * @return 返回作用域中的对象 Provider。
     */
    public <T> Supplier<T> scope(Object key, Supplier<T> provider);

    /**
     * 构造一个Scope链，使一个对象的创建可以同时贯穿两个Scope。<p>
     * 如果对象的创建满足这两个 Scope 的要求，那么对象会被这两个 Scope 同时缓存。
     * @param key 加入作用域的key。
     * @param secondScope 第二作用域
     * @param provider 对象 Provider。
     * @see #chainScope(Object, Scope[], Supplier)
     * @see #chainScope(Object, Scope[], int, int, Supplier)
     */
    public default <T> Supplier<T> chainScope(Object key, Scope secondScope, Supplier<T> provider) {
        Scope[] scopeArray = new Scope[] { secondScope };
        return chainScope(key, scopeArray, 0, scopeArray.length, provider);
    }

    /**
     * 构造一个Scope链，使一个对象的创建可以同时贯穿多个Scope。<p>
     * scopeChain 中的 Scope 如果被创建的对象满足其缓存的要求那么会被缓存。
     * @param key 加入作用域的key。
     * @param scopeChain 作用域链
     * @param provider 对象 Provider。
     * @see #chainScope(Object, Scope, Supplier)
     * @see #chainScope(Object, Scope[], int, int, Supplier)
     */
    public default <T> Supplier<T> chainScope(Object key, Scope[] scopeChain, Supplier<T> provider) {
        return chainScope(key, scopeChain, 0, scopeChain.length, provider);
    }

    /**
     * 构造一个Scope链，使一个对象的创建可以同时贯穿多个Scope。<p>
     * scopeChain 中的 Scope 如果被创建的对象满足其缓存的要求那么会被缓存。
     * @param key 加入作用域的key。
     * @param scopeChain 作用域链
     * @param start scopeChain 开始位置。
     * @param end scopeChain 结束位置。
     * @param provider 对象 Provider。
     * @see #chainScope(Object, Scope, Supplier)
     * @see #chainScope(Object, Scope[], Supplier)
     */
    public default <T> Supplier<T> chainScope(final Object key, final Scope[] scopeChain, int start, int end, Supplier<T> provider) {
        Supplier<T> nextSupplier = provider;
        if (start < end) {
            for (int i = start; i < end; i++) {
                Scope currentScope = scopeChain[end - i - 1];
                Supplier<T> finalNextSupplier = nextSupplier;
                nextSupplier = () -> {
                    return currentScope.scope(key, finalNextSupplier).get();
                };
            }
        }
        //
        Supplier<T> finalSupplier = nextSupplier;
        return () -> {
            Supplier<T> scope = scope(key, finalSupplier);
            if (scope != null) {
                T target = scope.get();
                if (target != null) {
                    return target;
                }
            }
            return finalSupplier.get();
        };
    }
}