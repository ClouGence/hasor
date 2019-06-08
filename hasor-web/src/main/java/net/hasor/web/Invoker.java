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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
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

    /** 本次请求的 Action，如果没有命中任何 Mapping 那么会返回空。例如在 InvokerFilter 拦截器中经常会看到空的 ownerMapping */
    public Mapping ownerMapping();

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
        return requestPath;
    }
}