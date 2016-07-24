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
package net.hasor.plugins.encoding;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.web.startup.RuntimeListener;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    protected Logger      logger           = LoggerFactory.getLogger(getClass());
    private   String      requestEncoding  = null;
    private   String      responseEncoding = null;
    private   Environment environment      = null;
    public void init(FilterConfig filterConfig) throws ServletException {
        /*获取请求响应编码*/
        this.requestEncoding = filterConfig.getInitParameter(EncodingModule.REQUEST_ENCODING);
        this.responseEncoding = filterConfig.getInitParameter(EncodingModule.RESPONSE_ENCODING);
        //
        if (StringUtils.isBlank(this.requestEncoding)) {
            this.requestEncoding = null;
        }
        if (StringUtils.isBlank(this.responseEncoding)) {
            this.responseEncoding = null;
        }
        //
        AppContext app = RuntimeListener.getAppContext(filterConfig.getServletContext());
        this.environment = app.getEnvironment();
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        if (this.requestEncoding != null) {
            httpReq.setCharacterEncoding(this.requestEncoding);
        }
        if (this.requestEncoding != null) {
            httpRes.setCharacterEncoding(this.responseEncoding);
        }
        //
        if (logger.isDebugEnabled()) {
            logger.debug("encodingFilter -> at http({}/{}) request : {}", this.requestEncoding, this.responseEncoding, httpReq.getRequestURI());
        }
        chain.doFilter(request, response);
    }
    public void destroy() {
    }
}