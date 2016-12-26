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
import net.hasor.web.DataContext;
import net.hasor.web.MimeType;
import org.more.bizcommon.json.JSON;
import org.more.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class DataContextSupplier implements DataContext {
    private Set<String>         lockKeys     = new HashSet<String>();
    private HttpServletRequest  httpRequest  = null;
    private HttpServletResponse httpResponse = null;
    private AppContext          appContext   = null;
    private MimeType            mimeType     = null;
    private String              viewName     = null;//模版名称
    private String              viewType     = null;//渲染引擎
    private boolean             useLayout    = true;//是否渲染布局
    //
    public DataContextSupplier(AppContext appContext, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        //
        this.appContext = appContext;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.mimeType = appContext.getInstance(MimeType.class);
        //
        Enumeration<?> paramEnum = this.httpRequest.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            Object paramKey = paramEnum.nextElement();
            String key = paramKey.toString();
            String val = this.httpRequest.getParameter(key);
            this.httpRequest.setAttribute("req_" + key, val);
        }
        //
        String contextPath = this.getHttpRequest().getContextPath();
        String requestPath = this.getHttpRequest().getRequestURI();
        if (requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        //
        int lastIndex = requestPath.lastIndexOf(".");
        if (lastIndex > 0) {
            this.viewType(requestPath.substring(lastIndex + 1));
        } else {
            this.viewType("default");
        }
        this.viewName = requestPath;
        //
        this.putWithoutLock(ROOT_DATA_KEY, this);
        this.putWithoutLock(REQUEST_KEY, this.httpRequest);
        this.putWithoutLock(RESPONSE_KEY, this.httpResponse);
        //
        this.lockKey(REQUEST_KEY);  // response
        this.lockKey(RESPONSE_KEY); // request
        this.lockKey(ROOT_DATA_KEY);// rootData
    }
    //
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
            throw new UnsupportedOperationException("the key['" + key + "'] must not in " + JSON.toString(this.lockKeys) + " or empty");
        }
        this.httpRequest.setAttribute(key, value);
    }
    protected void putWithoutLock(String key, Object value) {
        this.httpRequest.setAttribute(key, value);
    }
    @Override
    public void remove(String key) {
        if (StringUtils.isBlank(key) || this.lockKeys.contains(key)) {
            throw new UnsupportedOperationException("the key['" + key + "'] must not in " + JSON.toString(this.lockKeys) + " or empty");
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
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }
    //
    @Override
    public String renderTo() {
        return this.viewName;
    }
    @Override
    public void renderTo(String viewName) {
        this.viewName = viewName;
    }
    @Override
    public void renderTo(String viewType, String viewName) {
        this.viewType(viewType);
        this.viewName = viewName;
    }
    @Override
    public String viewType() {
        return this.viewType;
    }
    @Override
    public void viewType(String viewType) {
        if (StringUtils.isNotBlank(viewType)) {
            this.viewType = viewType.trim().toUpperCase();
        } else {
            this.viewType = "";
        }
    }
    @Override
    public boolean layout() {
        return this.useLayout;
    }
    @Override
    public void layoutEnable() {
        this.useLayout = true;
    }
    @Override
    public void layoutDisable() {
        this.useLayout = false;
    }
}