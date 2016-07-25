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
package net.hasor.restful.invoker;
import net.hasor.restful.MimeType;
import net.hasor.restful.RenderData;
import org.more.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerRenderData implements RenderData {
    private String              viewName     = null;
    private String              viewType     = null;
    private boolean             useLayout    = true;
    private Map<String, Object> contextMap   = null;
    private HttpServletRequest  httpRequest  = null;
    private HttpServletResponse httpResponse = null;
    private MimeType            mimeType     = null;
    //
    public InnerRenderData(MimeType mimeType, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String contextPath = httpRequest.getContextPath();
        String requestPath = httpRequest.getRequestURI();
        if (requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        //
        int lastIndex = requestPath.lastIndexOf(".");
        if (lastIndex > 0) {
            this.setViewType(requestPath.substring(lastIndex + 1));
        } else {
            this.setViewType("default");
        }
        this.viewName = requestPath;
        this.contextMap = new HashMap<String, Object>();
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        //
        Enumeration<?> paramEnum = httpRequest.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            Object paramKey = paramEnum.nextElement();
            String key = paramKey.toString();
            String val = httpRequest.getParameter(key);
            this.contextMap.put("req_" + key, val);
        }
        this.contextMap.put(ROOT_DATA_KEY, this);
        this.mimeType = mimeType;
    }
    //
    /**获取MimeType类型*/
    public String getMimeType(String suffix) {
        if (this.mimeType == null) {
            return httpRequest.getSession(true).getServletContext().getMimeType(suffix);
        } else {
            return this.mimeType.getMimeType(suffix);
        }
    }
    //
    /**设置返回值*/
    public void setReturnData(Object value) {
        this.contextMap.put(RETURN_DATA_KEY, value);
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
        return this.contextMap.keySet();
    }
    @Override
    public Object get(String key) {
        return this.contextMap.get(key);
    }
    @Override
    public void put(String key, Object value) {
        if (StringUtils.isBlank(key) ||//
                StringUtils.equalsIgnoreCase(ROOT_DATA_KEY, key) ||//
                StringUtils.equalsIgnoreCase(RETURN_DATA_KEY, key)) {
            throw new UnsupportedOperationException("the key must not as '" + ROOT_DATA_KEY + "' or '" + RETURN_DATA_KEY + "' or empty");
        }
        this.contextMap.put(key, value);
    }
    @Override
    public String getViewName() {
        return this.viewName;
    }
    @Override
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    @Override
    public String getViewType() {
        return this.viewType;
    }
    @Override
    public void setViewType(String viewType) {
        if (StringUtils.isNotBlank(viewType)) {
            this.viewType = viewType.trim().toUpperCase();
        } else {
            this.viewType = "";
        }
    }
    @Override
    public boolean useLayout() {
        return this.useLayout;
    }
    @Override
    public void enableLayout() {
        this.useLayout = true;
    }
    @Override
    public void disableLayout() {
        this.useLayout = false;
    }
}