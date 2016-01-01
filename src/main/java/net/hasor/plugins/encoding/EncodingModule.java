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
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.startup.RuntimeListener;
/**
 * 提供请求相应编码设置。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class EncodingModule extends WebModule {
    public static final String REQUEST_ENCODING      = "hasor.encoding.requestEncoding";
    public static final String RESPONSE_ENCODING     = "hasor.encoding.responseEncoding";
    public static final String URL_PATTERNS_ENCODING = "hasor.encoding.urlPatterns";
    //
    public void loadModule(WebApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        String requestEncoding = settings.getString(REQUEST_ENCODING);
        String responseEncoding = settings.getString(RESPONSE_ENCODING);
        logger.info("EncodingFilterPlugin -> requestEncoding = " + requestEncoding);
        logger.info("EncodingFilterPlugin -> responseEncoding = " + responseEncoding);
        //
        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put(REQUEST_ENCODING, requestEncoding);
        initParams.put(RESPONSE_ENCODING, responseEncoding);
        //
        String urlPatternsConfig = settings.getString(URL_PATTERNS_ENCODING);
        String[] patterns = StringUtils.isBlank(urlPatternsConfig) ? new String[0] : urlPatternsConfig.split(";");
        logger.info("EncodingFilterModule -> urlPatterns = {}.", new Object[] { patterns });
        //
        apiBinder.filter(patterns).through(Integer.MIN_VALUE, new EncodingFilter(), initParams);
    }
}
class EncodingFilter implements Filter {
    protected Logger    logger           = LoggerFactory.getLogger(getClass());
    private String      requestEncoding  = null;
    private String      responseEncoding = null;
    private Environment environment      = null;
    public void init(FilterConfig filterConfig) throws ServletException {
        /*获取请求响应编码*/
        this.requestEncoding = filterConfig.getInitParameter(EncodingModule.REQUEST_ENCODING);
        this.responseEncoding = filterConfig.getInitParameter(EncodingModule.RESPONSE_ENCODING);
        AppContext app = RuntimeListener.getAppContext(filterConfig.getServletContext());
        this.environment = app.getEnvironment();
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        if (this.requestEncoding != null)
            httpReq.setCharacterEncoding(this.requestEncoding);
        if (this.requestEncoding != null)
            httpRes.setCharacterEncoding(this.responseEncoding);
        //
        if (this.environment.isDebug()) {
            logger.info("at http({}/{}) request : {}", this.requestEncoding, this.responseEncoding, httpReq.getRequestURI());
        }
        chain.doFilter(request, response);
    }
    public void destroy() {}
}