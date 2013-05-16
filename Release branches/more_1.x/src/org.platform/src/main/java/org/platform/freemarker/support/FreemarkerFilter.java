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
import org.more.util.StringUtil;
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
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String[] suffix = this.settings.getSuffix();
        if (suffix != null) {
            for (String sort : suffix)
                if (StringUtil.matchWild(sort, requestURI) == true) {
                    if (httpResponse.isCommitted() == true)
                        return;
                    //this.freemarkerManager.getTemplate(templateName).getTemplate(requestURI).process(rootMap, out);
                    return;
                }
        }
        chain.doFilter(request, response);
    }
    //
    /**初始化*/
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    /**销毁*/
    @Override
    public void destroy() {}
}