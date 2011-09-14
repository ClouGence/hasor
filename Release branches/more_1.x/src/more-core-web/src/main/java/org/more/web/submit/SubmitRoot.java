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
package org.more.web.submit;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.more.core.json.JsonUtil;
import org.more.hypha.ApplicationContext;
import org.more.services.submit.SubmitService;
import org.more.util.config.Config;
import org.more.web.hypha.ContextLoaderListener;
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
    private String            contextPath      = null;
    private Pattern           actionURLPattern = null;
    private String            defaultNS        = null;
    /*-----------------------------------------------------------------*/
    protected void init(Config<ServletContext> config) throws ServletException {
        try {
            /*      /([^/]+)?:/([^?]*)(?:\\?(.*))?      */
            /*      /[ns]:/[actionpath][?info]          */
            actionURLPattern = Pattern.compile("/([^/]+)?:/([^?]*)(?:\\?(.*))?");
            this.servletContext = config.getContext();
            this.contextPath = this.servletContext.getContextPath();
            ApplicationContext context = (ApplicationContext) this.servletContext.getAttribute(ContextLoaderListener.ContextName);
            this.submitService = context.getService(SubmitService.class);
            {
                //设置参数
                Enumeration<String> enums = config.getInitParameterNames();
                while (enums.hasMoreElements()) {
                    String name = enums.nextElement();
                    this.submitService.setAttribute(name, config.getInitParameter(name));
                }
                Object _defaultNS = config.getInitParameter("defaultNS");
                if (_defaultNS != null)
                    defaultNS = _defaultNS.toString();
                else
                    defaultNS = this.submitService.getDefaultNameSpaceString();
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
    public final void init(final FilterConfig config) throws ServletException {
        this.init(new FilterSubmitConfig(config));
    };
    /** Servlet初始化方法，该方法调用init(InitParameter param) */
    public final void init() throws ServletException {
        final ServletConfig config = this.getServletConfig();
        this.init(new ServletSubmitConfig(config));
    };
    /*-----------------------------------------------------------------*/
    protected URI getActionURI(HttpServletRequest request) {
        String requestURI = getRequestPath(request);
        String userInfo = request.getQueryString();
        if (userInfo == null || userInfo.equals("") == true) {} else
            requestURI += ("?" + userInfo);
        /*      /([^/]+)?:/([^?]*)(?:\\?(.*))?      */
        /*      /[ns]:/[actionpath][?info]          */
        Matcher m = this.actionURLPattern.matcher(requestURI);
        if (m.find() == false)
            return null;
        //
        String ns = m.group(1);
        String path = m.group(2);
        String info = m.group(3);
        //
        StringBuffer sb = new StringBuffer();
        if (ns == null)
            sb.append(defaultNS);
        else
            sb.append(ns);
        sb.append("://");
        sb.append(path);
        if (info != null) {
            sb.append("?");
            sb.append(info);
        }
        try {
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            return null;
        }
    }
    /** 执行调用 */
    private Object doAction(URI uri, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            //初始化WebHelper
            WebHelper.reset();
            WebHelper.setHttpRequest(request);
            WebHelper.setHttpResponse(response);
            WebHelper.setHttpSession(request.getSession(true));
            WebHelper.setHttpContext(request.getSession(true).getServletContext());
            return this.submitService.getActionObject(uri).doAction();//执行调用
        } catch (Throwable e) {
            if (e instanceof ServletException)
                throw (ServletException) e;
            else if (e instanceof IOException)
                throw (IOException) e;
            else
                throw new ServletException(e);
        }
    };
    private void printTo(HttpServletResponse res, Object object) throws IOException {
        if (object == null)
            return;
        if (res.isCommitted() == false)
            if (object instanceof CharSequence == false)
                res.getWriter().write(new JsonUtil().toString(object));
            else {
                CharSequence cs = (CharSequence) object;
                StringBuffer sb = new StringBuffer(cs);
                res.getWriter().write(sb.toString());
            }
    }
    /** 中央调度过滤器 */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //
        URI uri = this.getActionURI(req);
        if (uri != null) {
            this.printTo(res, this.doAction(uri, req, res));
            return;
        }
        chain.doFilter(req, res);
    };
    /** 中央调度Servlet */
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //
        URI uri = this.getActionURI(req);
        if (uri != null) {
            this.printTo(res, this.doAction(uri, req, res));
            return;
        }
        res.sendError(404, "不存在的请求动作:<br/>" + uri);
    };
    /**获取{@link SubmitService}对象。*/
    protected SubmitService getSubmitService() {
        return this.submitService;
    };
    /**获取请求路径*/
    protected String getRequestPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(this.contextPath.length());
        return requestURI;
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