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
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.supplier.ClassLoaderSingleProvider;
import net.hasor.utils.supplier.SingleProvider;
import net.hasor.utils.supplier.ThreadSingleProvider;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 *  提供者实现这个接口就相当于同时实现了
 *      <li>java.util.function.Supplier</li>
 *      <li>javax.inject.Provider</li>
 *      <li>java.util.concurrent.Callable</li>
 *  三个接口
 * @version : 2014年5月22日
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Provider<T> extends Supplier<T>, javax.inject.Provider<T>, Callable<T> {
    /** @return 获取对象。*/
    public default T get() {
        try {
            return this.call();
        } catch (Exception e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    public T call() throws Exception;

    public default Provider<T> asSingle() {
        return of(new SingleProvider<>(this));
    }

    public default Provider<T> asThread() {
        return of(new ThreadSingleProvider<>(this));
    }

    public default Provider<T> asLoader() {
        return of(new ClassLoaderSingleProvider<>(this));
    }

    public static <T> Provider<T> of(T instance) {
        return () -> instance;
    }

    public static <T> Provider<T> of(Supplier<T> supplier) {
        return supplier::get;
    }

    public static <T> Provider<T> of(javax.inject.Provider<T> supplier) {
        return supplier::get;
    }

    public static <T> Provider<T> of(Callable<T> callable) {
        return callable::call;
    }

    public static <V, T extends V> Provider<V> ofs(Supplier<T> supplier) {
        return supplier::get;
    }

    public static <V, T extends V> Provider<V> ofp(javax.inject.Provider<T> supplier) {
        return supplier::get;
    }

    public static <V, T extends V> Provider<V> ofc(Callable<T> callable) {
        return callable::call;
    }
}