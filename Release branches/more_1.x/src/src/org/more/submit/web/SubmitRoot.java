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
package org.more.submit.web;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.hypha.ApplicationContext;
import org.more.submit.ActionObject;
import org.more.submit.SubmitService;
import org.more.util.Config;
/**
 * submit4.0组建对Web部分的支持，该类已经实现了Filter接口并且继承自HttpServlet类。
 * 该web支持的配置只有一个参数buildClass，表示生成器的具体类型。action参数表示请求的协议名
 * 或者action表达试参数名。默认是action。<br/>
 * SubmitRoot会反射的形式创建生成器。过滤器递交方式action://test.tesy?aaa=aaa，上述例子中“:”可以使用“!”代替
 * @version 2009-6-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class SubmitRoot extends HttpServlet implements Filter {
    /*-----------------------------------------------------------------*/
    private static final long serialVersionUID = -9157250446565992949L;
    private SubmitService     submitService    = null;
    private ServletContext    servletContext   = null;
    private String            prefix           = null;
    private String            contextPath      = null;
    /*-----------------------------------------------------------------*/
    private void init(Config<ServletContext> config) throws ServletException {
        try {
            this.servletContext = config.getContext();
            this.contextPath = config.getContext().getContextPath();
            ApplicationContext context = (ApplicationContext) this.servletContext.getAttribute("org.more.hypha.ROOT");
            this.submitService = context.getService(SubmitService.class);
            {
                //设置参数
                Enumeration<String> enums = config.getInitParameterNames();
                while (enums.hasMoreElements()) {
                    String name = enums.nextElement();
                    this.submitService.setAttribute(name, config.getInitParameter(name));
                }
                Object dns = config.getInitParameter("defaultNS");
                if (dns != null)
                    this.prefix = dns.toString();
            }
            this.servletContext.setAttribute("org.more.submit.ROOT", this.submitService);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ServletException)
                throw (ServletException) e;
            else
                throw new ServletException(e.getLocalizedMessage(), e);
        }
    };
    /*-----------------------------------------------------------------*/
    /** 过滤器初始化方法，该方法调用init(InitParameter param) */
    public void init(final FilterConfig config) throws ServletException {
        this.init(new FilterSubmitConfig(config));
    };
    /** Servlet初始化方法，该方法调用init(InitParameter param) */
    public void init() throws ServletException {
        final ServletConfig config = this.getServletConfig();
        this.init(new ServletSubmitConfig(config));
    };
    /*-----------------------------------------------------------------*/
    public String getActionURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String userInfo = request.getQueryString();
        StringBuffer actionURI = new StringBuffer();
        if (this.prefix != null)
            actionURI.append(this.prefix + "://");
        else
            actionURI.append(this.submitService.getDefaultNameSpaceString() + "://");
        actionURI.append(requestURI.substring(this.contextPath.length()));
        if (actionURI.charAt(0) == '/')
            actionURI = actionURI.deleteCharAt(0);
        if (userInfo == null || userInfo.equals("") == true)
            return actionURI.toString();
        else
            return actionURI.toString() + "?" + userInfo;
    }
    /** 执行调用 */
    private Object doAction(ActionObject ao, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String uri = this.getActionURI(httpRequest);
            return this.submitService.getActionObject(uri).doAction(httpRequest, httpResponse);//执行调用
        } catch (Throwable e) {
            if (e instanceof ServletException)
                throw (ServletException) e;
            else if (e instanceof IOException)
                throw (IOException) e;
            else
                throw new ServletException(e);
        }
    };
    /** 中央调度过滤器 */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //
        try {
            String uri = this.getActionURI(req);
            ActionObject ao = this.submitService.getActionObject(uri);
            if (ao != null) {
                this.doAction(ao, req, res);
                return;
            }
        } catch (Exception e) {}
        chain.doFilter(req, res);
    };
    /** 中央调度Servlet */
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //
        String uri = this.getActionURI(req);
        try {
            ActionObject ao = this.submitService.getActionObject(uri);
            if (ao != null)
                this.doAction(ao, req, res);
        } catch (Exception e) {
            res.sendError(404, "不存在的请求动作:<br/>" + uri);
        }
    };
};
class FilterSubmitConfig implements Config<ServletContext> {
    private FilterConfig config = null;
    public FilterSubmitConfig(FilterConfig config) {
        this.config = config;
    };
    public ServletContext getContext() {
        return this.config.getServletContext();
    };
    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    };
    public Enumeration<String> getInitParameterNames() {
        return this.config.getInitParameterNames();
    };
}
class ServletSubmitConfig implements Config<ServletContext> {
    private ServletConfig config = null;
    public ServletSubmitConfig(ServletConfig config) {
        this.config = config;
    };
    public ServletContext getContext() {
        return this.config.getServletContext();
    };
    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    };
    public Enumeration<String> getInitParameterNames() {
        return this.config.getInitParameterNames();
    };
};