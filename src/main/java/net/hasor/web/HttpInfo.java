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
/**
 *
 * @version : 2016-10-06
 * @author 赵永春 (zyc@hasor.net)
 */
public class HttpInfo {
    private AppContext          appContext;
    private HttpServletRequest  request;
    private HttpServletResponse response;
    //
    public HttpInfo(AppContext appContext, HttpServletRequest request, HttpServletResponse response) {
        this.appContext = appContext;
        this.request = request;
        this.response = response;
    }
    //
    /** @return Return HttpServletRequest. Do not use HttpServletRequest Object in constructor of Controller */
    public HttpServletRequest getRequest() {
        return this.request;
    }
    /** @return Return HttpServletResponse. Do not use HttpServletResponse Object in constructor of Controller */
    public HttpServletResponse getResponse() {
        return this.response;
    }
    /** @return Return AppContext. */
    public AppContext getAppContext() {
        return this.appContext;
    }
}