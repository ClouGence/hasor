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
package org.platform.action.support;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.platform.general.WebFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
@WebFilter(value = "*", sort = Integer.MIN_VALUE + 1)
public class ControllerFilter implements Filter {
    @Inject
    private ActionSettings actionSettings = null;
    //    @Inject
    private ActionManager  actionManager  = null;
    // 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        //
        chain.doFilter(request, response);
        // TODO Auto-generated method stub
        //        actionManager.findNameSpace(httpRequest).getActionByName(httpRequest.getMethod(), "aa").invoke(request, response, params);
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}