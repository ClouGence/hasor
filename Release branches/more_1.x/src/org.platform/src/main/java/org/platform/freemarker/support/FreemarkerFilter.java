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
package org.platform.freemarker.support;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.freemarker.FreemarkerManager;
import org.platform.general.WebFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * Freemarker模板功能支持。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
@WebFilter(value = "*", sort = Integer.MIN_VALUE + 2)
public class FreemarkerFilter implements Filter {
    @Inject
    private FreemarkerManager  freemarkerManager = null;
    @Inject
    private FreemarkerSettings settings          = null;
    //
    /***/
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.settings.isEnable() == false) {
            chain.doFilter(request, response);
            return;
        }
        //
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String[] suffix = this.settings.getSuffix();
        boolean doTemplate = false;
        if (suffix != null) {
            for (String sort : suffix)
                if (StringUtils.matchWild(sort, requestURI) == true) {
                    doTemplate = true;
                    break;
                }
        }
        if (doTemplate) {
            try {
                if (httpResponse.isCommitted() == true)
                    return;
                //HttpRequestHashModel requestHashModel = new HttpRequestHashModel(httpRequest, httpResponse);
                //TemplateRootMap rootMap = new TemplateRootMap(httpRequest, appContext, freemarkerManager);
                Object rootMap = null;
                this.freemarkerManager.processTemplate(requestURI, rootMap, httpResponse.getWriter());
                return;
            } catch (Exception e) {
                switch (this.settings.getOnError()) {
                /**抛出异常*/
                case ThrowError:
                    throw new ServletException(e);
                    /**打印到控制台或日志*/
                case PrintOnConsole:
                    Platform.error("%s", e);
                    break;
                /**忽略，仅仅产生一条警告消息*/
                case Warning:
                    Platform.warning("process Template error -> requestURI is %s ,message is %s", requestURI, e.getMessage());
                    break;
                /**打印到页面*/
                case PrintOnPage:
                    e.printStackTrace(httpResponse.getWriter());
                    break;
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }
    //
    /**初始化*/
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    /**销毁*/
    @Override
    public void destroy() {}
}