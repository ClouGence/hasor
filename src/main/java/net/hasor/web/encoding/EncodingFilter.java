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
package net.hasor.web.encoding;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.WebApiBinder;
import net.hasor.web.startup.RuntimeListener;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 提供请求相应编码设置。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class EncodingFilter implements Filter {
    private String httpRequestEncoding  = null;
    private String httpResponseEncoding = null;
    //
    public void init(FilterConfig filterConfig) throws ServletException {
        //
        AppContext appContext = RuntimeListener.getAppContext(filterConfig.getServletContext());
        BindInfo<EncodingFilter> bindInfo = appContext.getBindInfo(EncodingFilter.class);
        if (bindInfo != null) {
            httpRequestEncoding = (String) bindInfo.getMetaData(WebApiBinder.HTTP_REQUEST_ENCODING_KEY);
            httpResponseEncoding = (String) bindInfo.getMetaData(WebApiBinder.HTTP_RESPONSE_ENCODING_KEY);
        }
        //
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (httpRequestEncoding != null) {
            HttpServletRequest httpReq = (HttpServletRequest) request;
            httpReq.setCharacterEncoding(httpRequestEncoding);
        }
        if (httpResponseEncoding != null) {
            HttpServletResponse httpRes = (HttpServletResponse) response;
            httpRes.setCharacterEncoding(httpResponseEncoding);
        }
        chain.doFilter(request, response);
    }
    public void destroy() {
        /**/
    }
}