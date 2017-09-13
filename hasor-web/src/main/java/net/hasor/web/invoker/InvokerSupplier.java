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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.MimeType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
/**
 * {@link Invoker} 接口实现类。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerSupplier implements Invoker {
    private Set<String>         lockKeys     = new HashSet<String>();
    private HttpServletRequest  httpRequest  = null;
    private HttpServletResponse httpResponse = null;
    private AppContext          appContext   = null;
    private MimeType            mimeType     = null;
    private String requestPath;
    //
    protected InvokerSupplier(AppContext appContext, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        //
        this.appContext = appContext;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.mimeType = appContext.getInstance(MimeType.class);
        //
        String contextPath = this.getHttpRequest().getContextPath();
        String requestPath = this.getHttpRequest().getRequestURI();
        if (requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        this.requestPath = requestPath;
        //
        this.put(ROOT_DATA_KEY, this);
        this.put(REQUEST_KEY, this.httpRequest);
        this.put(RESPONSE_KEY, this.httpResponse);
        //
        this.lockKey(ROOT_DATA_KEY);// rootData
        this.lockKey(REQUEST_KEY);  // response
        this.lockKey(RESPONSE_KEY); // request
    }
    @Override
    public AppContext getAppContext() {
        return appContext;
    }
    //
    @Override
    public HttpServletRequest getHttpRequest() {
        return this.httpRequest;
    }
    @Override
    public HttpServletResponse getHttpResponse() {
        return this.httpResponse;
    }
    @Override
    public Set<String> keySet() {
        Enumeration<String> names = this.httpRequest.getAttributeNames();
        HashSet<String> nameSet = new HashSet<String>();
        while (names.hasMoreElements()) {
            nameSet.add(names.nextElement());
        }
        return nameSet;
    }
    @Override
    public Object get(String key) {
        return this.httpRequest.getAttribute(key);
    }
    @Override
    public void put(String key, Object value) {
        if (StringUtils.isBlank(key) || this.lockKeys.contains(key)) {
            throw new UnsupportedOperationException("the key '" + key + "' is lock key.");
        }
        this.httpRequest.setAttribute(key, value);
    }
    @Override
    public void remove(String key) {
        if (StringUtils.isBlank(key) || this.lockKeys.contains(key)) {
            throw new UnsupportedOperationException("the key '" + key + "' is lock key.");
        }
        this.httpRequest.removeAttribute(key);
    }
    @Override
    public void lockKey(String key) {
        if (StringUtils.isBlank(key))
            return;
        this.lockKeys.add(key);
    }
    @Override
    public String getRequestPath() {
        return this.requestPath;
    }
    @Override
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }
}