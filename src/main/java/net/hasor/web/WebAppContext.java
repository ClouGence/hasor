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
import net.hasor.core.Environment;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public interface WebAppContext extends AppContext {
    /** 当处理 request 之前引发。
     * @see net.hasor.web.startup.RuntimeFilter#beforeRequest(WebAppContext, HttpServletRequest, HttpServletResponse)
     */
    public static final String HTTP_BEFORE_REQUEST = "HTTP_BEFORE_REQUEST";
    /** 当处理完 request 响应 Response 之后引发。
     * @see net.hasor.web.startup.RuntimeFilter#afterResponse(WebAppContext, HttpServletRequest, HttpServletResponse)
     */
    public static final String HTTP_AFTER_RESPONSE = "HTTP_AFTER_RESPONSE";

    /**获取ServletContext对象。*/
    public ServletContext getServletContext();

    /**获取容器目前支持的 Servet Api 版本。*/
    public ServletVersion getServletVersion();

    /** @return 获取 {@link Environment} */
    public WebEnvironment getEnvironment();
}