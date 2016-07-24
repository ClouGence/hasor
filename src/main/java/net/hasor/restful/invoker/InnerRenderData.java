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
import net.hasor.restful.RenderData;
import net.hasor.restful.MimeType;
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
        this.contextMap.put("rootData", this);
        this.mimeType = mimeType;
    }
    //
    /**获取MimeType类型*/
    public String getMimeType(String suffix) {
        if (this.mimeType == null) {
            return null;//TODO return mimeType
        } else {
            return this.mimeType.getMimeType(suffix);
        }
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
        if (StringUtils.isBlank(key) || StringUtils.equalsIgnoreCase("rootData", key)) {
            throw new UnsupportedOperationException("the key must not as 'rootData' or empty");
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
}