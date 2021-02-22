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
package net.hasor.web;
import net.hasor.core.AppContext;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 请求调用
 * @version : 2016-12-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Invoker extends MimeType {
    /**数据池中的key，该数据是表示请求方法的执行返回值。*/
    public static final String RETURN_DATA_KEY = "resultData";  //
    /**数据池中的key，数据池中的自关联，相当于 this的含义。*/
    public static final String ROOT_DATA_KEY   = "rootData";    //
    /**数据池中的key，request对象。*/
    public static final String REQUEST_KEY     = "request";     //
    /**数据池中的key，response对象。*/
    public static final String RESPONSE_KEY    = "response";    //

    /** 获取当前{@link AppContext} 对象。*/
    public AppContext getAppContext();

    /** 获取 {@link HttpServletRequest} 对象。*/
    public HttpServletRequest getHttpRequest();

    /** 获取 {@link HttpServletResponse} 对象。*/
    public HttpServletResponse getHttpResponse();

    /** 安排一个异步任务来执行接下来的任务。*/
    public <T> Future<T> asyncExecute(EFunction<Invoker, T, Throwable> consumer, Executor executor);

    /** 安排一个异步任务来执行接下来的任务。*/
    public default <T> Future<T> asyncExecute(EFunction<Invoker, T, Throwable> consumer) {
        Executor executor = this.getAppContext().getEnvironment().getEventContext().getExecutor();
        return this.asyncExecute(consumer, executor);
    }

    /** 设置内容类型类型，如果没有配置那么会通过 renderType 配置进行自动推断，若 @RenderType 也未配置，那么不会进行任何操作。*/
    public String contentType();

    /** 设置内容类型类型，如果没有配置那么会通过 renderType 配置进行自动推断，若 @RenderType 也未配置，那么不会进行任何操作。*/
    public void contentType(String contentType);

    /** 本次请求的 Action，如果没有命中任何 Mapping 那么会返回空。例如在 InvokerFilter 拦截器中经常会看到空的 ownerMapping */
    public Mapping ownerMapping();

    /** 如果请求是 application/json 类型的，那么可以通过这个方法获取 Json 数据 */
    public String getJsonBodyString();

    /** 获取数据容器中已经保存的数据 keys 。*/
    public default Set<String> keySet() {
        Enumeration<String> names = this.getHttpRequest().getAttributeNames();
        HashSet<String> nameSet = new HashSet<>();
        while (names.hasMoreElements()) {
            nameSet.add(names.nextElement());
        }
        return nameSet;
    }

    /** 将Request中的参数填充到 formType 类型对象上，formType 的创建将会使用 {@link AppContext#justInject(Object)}  方法。 */
    public default <T> T fillForm(Class<? extends T> formType) {
        try {
            return this.fillForm(formType, this.getAppContext().justInject(formType.newInstance()));
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 将Request中的参数填充到 formType 类型对象上，类型实例由参数指定 */
    public <T> T fillForm(Class<? extends T> formType, T bean);

    /**
     * 从数据池中获取数据
     * @param key 数据key
     */
    public default Object get(String key) {
        return this.getHttpRequest().getAttribute(key);
    }

    /**
     * 从数据池中删除数据，如果尝试删除已经被锁定的key，会引发 {@link UnsupportedOperationException} 类型异常。
     * @see #lockKey(String)
     * @param key 数据key
     */
    public default void remove(String key) {
        if (StringUtils.isBlank(key) || this.isLockKey(key)) {
            throw new UnsupportedOperationException("the key '" + key + "' is lock key.");
        }
        this.getHttpRequest().removeAttribute(key);
    }

    /**
     /**
     * 将新的值设置到数据池中，如果尝试覆盖已经被锁定的key，会引发 {@link UnsupportedOperationException} 类型异常。
     * @see #lockKey(String)
     * @param key 数据key
     * @param value 数据 value
     */
    public default void put(String key, Object value) {
        if (StringUtils.isBlank(key) || this.isLockKey(key)) {
            throw new UnsupportedOperationException("the key '" + key + "' is lock key.");
        }
        this.getHttpRequest().setAttribute(key, value);
    }

    /**
     * 判断一个 key 是否被 lock 了。
     */
    public boolean isLockKey(String key);

    /**
     * 将一个 key 进行锁定。
     * tips：当对锁定的 key 进行 put 或者 remove 操作时会引发 {@link UnsupportedOperationException} 类型异常。
     * @param key 要被锁定的key，大小写敏感。
     */
    public void lockKey(String key);

    /**获取当前请求路径。相当于下面这样的代码：
     * <pre>
     String contextPath = httpRequest.getContextPath();
     String requestPath = httpRequest.getRequestURI();
     if (requestPath.startsWith(contextPath)) {
     requestPath = requestPath.substring(contextPath.length());
     }
     return requestPath;
     </pre>*/
    public default String getRequestPath() {
        String contextPath = this.getHttpRequest().getContextPath();
        String requestPath = this.getHttpRequest().getRequestURI();
        if (requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        return requestPath.replaceAll("[/]{2,}", "/");
    }

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception.   Unless
     * otherwise specified by the implementing class, actions are performed in
     * the order of entry set iteration (if an iteration order is specified.)
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException if the specified action is null
     * @since 1.8
     */
    public default void forEach(BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        for (String key : keySet()) {
            Object optionValue = get(key);
            action.accept(key, optionValue);
        }
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns
     * {@code null}, else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws UnsupportedOperationException if the {@code put} operation is not supported by this map
     * @since 1.8
     */
    public default void putIfAbsent(String key, Object value) {
        if (get(key) == null) {
            put(key, value);
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     * @since 1.8
     */
    public default Object getOrDefault(String key, Object defaultValue) {
        Object v = null;
        return ((v = get(key)) != null) ? v : defaultValue;
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     * @since 1.8
     */
    public default <V> V getOrMap(String key, Function<Object, V> defaultValue) {
        return defaultValue.apply(get(key));
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}), attempts to compute its value using the given mapping
     * function and enters it into this map unless {@code null}.
     *
     * <p>If the function returns {@code null} no mapping is recorded. If
     * the function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded.  The most
     * common usage is to construct a new object serving as an initial
     * mapped value or memoized result.
     *
     * @param key key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @throws UnsupportedOperationException if the {@code put} operation is not supported by this map
     * @since 1.8
     */
    public default void computeIfAbsent(String key, Function<String, Object> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        if (get(key) == null) {
            Object newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
            }
        }
    }
}
