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
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.quick.plugin.Plugin;
import net.hasor.web.WebApiBinder;
import net.hasor.web.plugin.AbstractWebHasorPlugin;
/**
 * 提供请求相应编码设置。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Plugin
public class EncodingFilterPlugin extends AbstractWebHasorPlugin {
    public static final String RequestEncoding  = "hasor-web.requestEncoding";
    public static final String ResponseEncoding = "hasor-web.responseEncoding";
    //
    public void loadPlugin(WebApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        String requestEncoding = settings.getString("hasor-web.requestEncoding");
        String responseEncoding = settings.getString("hasor-web.responseEncoding");
        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put(RequestEncoding, requestEncoding);
        initParams.put(ResponseEncoding, responseEncoding);
        //
        apiBinder.filter("/*").through(Integer.MIN_VALUE, EncodingFilter.class, initParams);
    }
}
class EncodingFilter implements Filter {
    private String requestEncoding  = null;
    private String responseEncoding = null;
    public void init(FilterConfig filterConfig) throws ServletException {
        /*获取请求响应编码*/
        this.requestEncoding = filterConfig.getInitParameter(EncodingFilterPlugin.RequestEncoding);
        this.responseEncoding = filterConfig.getInitParameter(EncodingFilterPlugin.ResponseEncoding);
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final HttpServletResponse httpRes = (HttpServletResponse) response;
        if (this.requestEncoding != null)
            httpReq.setCharacterEncoding(this.requestEncoding);
        if (this.requestEncoding != null)
            httpRes.setCharacterEncoding(this.responseEncoding);
        //
        Hasor.logDebug("at http(%s/%s) request : %s", this.requestEncoding, this.responseEncoding, httpReq.getRequestURI());
        chain.doFilter(request, response);
    }
    public void destroy() {}
}