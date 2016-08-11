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
import net.hasor.web.WebAppContext;
import org.more.bizcommon.Message;
import org.more.json.JSON;
import org.more.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerRenderData implements RenderData {
    private static final String[]               LOCK_KEYS    = //
            {ROOT_DATA_KEY, RETURN_DATA_KEY, VALID_DATA_KEY, REQUEST_KEY, RESPONSE_KEY};
    private              String                 viewName     = null;//模版名称
    private              String                 viewType     = null;//渲染引擎
    private              boolean                useLayout    = true;//是否渲染布局
    private              Map<String, Object>    contextMap   = null;//
    private              HttpServletRequest     httpRequest  = null;
    private              HttpServletResponse    httpResponse = null;
    private              Map<String, ValidData> validData    = null;//原始验证数据
    private              WebAppContext          appContext   = null;
    private              MimeType               mimeType     = null;
    //
    public InnerRenderData(WebAppContext appContext, MimeType mimeType, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String contextPath = httpRequest.getContextPath();
        String requestPath = httpRequest.getRequestURI();
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
        this.contextMap = new HashMap<String, Object>();
        this.validData = new HashMap<String, ValidData>();
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
        this.contextMap.put(RETURN_DATA_KEY, null);
        this.contextMap.put(VALID_DATA_KEY, this.validData);
        this.contextMap.put(REQUEST_KEY, httpRequest);
        this.contextMap.put(RESPONSE_KEY, httpResponse);
        this.appContext = appContext;
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
    public WebAppContext getAppContext() {
        return appContext;
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
    public void remove(String key) {
        this.contextMap.remove(key);
    }
    @Override
    public void put(String key, Object value) {
        for (String keyItem : LOCK_KEYS) {
            if (StringUtils.isBlank(key) || StringUtils.equalsIgnoreCase(ROOT_DATA_KEY, key)) {
                throw new UnsupportedOperationException("the key['" + key + "'] must not in " + JSON.toString(LOCK_KEYS) + " or empty");
            }
        }
        this.contextMap.put(key, value);
    }
    //
    // --------------------------------------------------
    @Override
    public String viewName() {
        return this.viewName;
    }
    @Override
    public void viewName(String viewName) {
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
    //
    // --------------------------------------------------
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
    //
    // --------------------------------------------------
    public void updateValidResult(Map<String, ValidData> validDataMap) {
        this.validData.clear();
        this.validData.putAll(validDataMap);
    }
    @Override
    public List<String> validKeys() {
        return new ArrayList<String>(this.validData.keySet());
    }
    @Override
    public List<Message> validErrors(String messageKey) {
        ValidData data = this.validData.get(messageKey);
        return data == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(data);
    }
    @Override
    public boolean isValid() {
        for (ValidData data : this.validData.values()) {
            if (data != null && !data.isValid()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean isValid(String messageKey) {
        ValidData data = this.validData.get(messageKey);
        return data == null ? true : data.isValid();
    }
    @Override
    public void clearValidErrors() {
        this.validData.clear();
    }
    @Override
    public void clearValidErrors(String messageKey) {
        this.validData.remove(messageKey);
    }
}