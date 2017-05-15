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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
/**
 * 请求调用
 * @version : 2016-12-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Invoker extends MimeType {
    /**数据池中的key，该数据是表示请求方法的执行返回值。*/
    public static final String RETURN_DATA_KEY = "resultData";//
    /**数据池中的key，数据池中的自关联，相当于 this的含义。*/
    public static final String ROOT_DATA_KEY   = "rootData";//
    /**数据池中的key，request对象。*/
    public static final String REQUEST_KEY     = "request";//
    /**数据池中的key，response对象。*/
    public static final String RESPONSE_KEY    = "response";//

    /** 获取当前{@link AppContext} 对象。*/
    public AppContext getAppContext();

    /** 获取 {@link HttpServletRequest} 对象。*/
    public HttpServletRequest getHttpRequest();

    /** 获取 {@link HttpServletResponse} 对象。*/
    public HttpServletResponse getHttpResponse();

    /** 获取数据容器中已经保存的数据 keys 。*/
    public Set<String> keySet();

    /**
     * 从数据池中获取数据
     * @param key 数据key
     */
    public Object get(String key);

    /**
     * 从数据池中删除数据，如果尝试删除已经被锁定的key，会引发 {@link UnsupportedOperationException} 类型异常。
     * @see #lockKey(String)
     * @param key 数据key
     */
    public void remove(String key);

    /**
     /**
     * 将新的值设置到数据池中，如果尝试覆盖已经被锁定的key，会引发 {@link UnsupportedOperationException} 类型异常。
     * @see #lockKey(String)
     * @param key 数据key
     * @param value 数据 value
     */
    public void put(String key, Object value);

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
    public String getRequestPath();
}