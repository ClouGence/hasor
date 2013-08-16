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
package org.hasor.mvc.controller.support;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 
 * @version : 2013-8-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class AbstractController {
    private HttpServletRequest  request  = null;
    private HttpServletResponse response = null;
    //
    public void initController(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
    /**获取{@link HttpServletRequest}*/
    protected HttpServletRequest getRequest() {
        return request;
    }
    /**获取{@link HttpServletResponse}*/
    protected HttpServletResponse getResponse() {
        return response;
    }
    /**设置{@link HttpServletRequest}属性*/
    protected void putAtt(String attKey, Object attValue) {
        this.getRequest().setAttribute(attKey, attValue);
    }
    /**设置{@link HttpServletResponse}Header属性*/
    protected void setHeader(String key, String value) {
        this.getResponse().setHeader(key, value);
    }
    /**设置{@link HttpServletResponse}Header属性*/
    protected void addHeader(String key, String value) {
        this.getResponse().addHeader(key, value);
    }
}