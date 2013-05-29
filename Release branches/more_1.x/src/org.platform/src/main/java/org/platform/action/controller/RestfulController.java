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
package org.platform.action.controller;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.action.ActionInvoke;
import org.platform.action.ActionManager;
import org.platform.action.ActionNameSpace;
import org.platform.general.WebFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
@WebFilter(value = "*", sort = Integer.MIN_VALUE + 2)
public class RestfulController implements Filter {
    @Inject
    private ActionManager actionManager = null;
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        //1.定位所属ActionNameSpace
        ActionNameSpace[] spaceList = actionManager.getNameSpaceList();
        ActionNameSpace findSpace = null;
        for (ActionNameSpace space : spaceList) {
            String ns = space.getNameSpace();
            if (requestPath.startsWith(ns) == true) {
                findSpace = space;
                break;
            }
        }s
        //2.获取 ActionInvoke
        String actionInvoke = null;
        ActionInvoke invoke = null;
        try {
            actionInvoke = requestPath.substring(findSpace.getNameSpace().length());
            String actionMethod = actionInvoke.split("\\.")[0];
            invoke = findSpace.getActionByName(request.getMethod(), actionMethod);
        } catch (NullPointerException e) {
            chain.doFilter(request, response);
        }
    }
    @Override
    public void destroy() {}
}