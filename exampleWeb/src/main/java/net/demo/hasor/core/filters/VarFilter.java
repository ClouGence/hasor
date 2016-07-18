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
package net.demo.hasor.core.filters;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.Environment;
import net.hasor.plugins.templates.ContextMap;
/**
 * 服务让模版页面可以访问到“ctx_path”变量
 * @version : 2016年1月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class VarFilter implements Filter {
    private String curentVersion;
    private String qq_admins;
    //
    public VarFilter(Environment environment) {
        this.curentVersion = environment.getSettings().getString("curentVersion", "2.3.1");
        this.qq_admins = environment.envVar("admins");//给QQ的登陆接入授权码
        /*--*/
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        /*--*/
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        ContextMap map = ContextMap.genContextMap(req, resp);
        map.put("ctx_path", req.getSession(true).getServletContext().getContextPath());
        map.put("curentVersion", this.curentVersion);
        map.put("qq_admins", this.qq_admins);
        chain.doFilter(request, response);
    }
    @Override
    public void destroy() {
        /*--*/
    }
}