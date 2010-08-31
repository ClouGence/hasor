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
package org.more.submit.web.support;
import java.io.IOException;
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
import org.more.submit.ActionContextBuild;
import org.more.submit.SubmitBuild;
import org.more.submit.web.WebSubmitContext;
import org.more.util.Config;
/**
 * submit3.0组建对Web部分的支持，该类已经实现了Filter接口并且继承自HttpServlet类。
 * 该web支持的配置只有一个参数buildClass，表示生成器的具体类型。action参数表示请求的协议名
 * 或者action表达试参数名。默认是action。<br/>
 * SubmitRoot会反射的形式创建生成器。过滤器递交方式action://test.tesy?aaa=aaa，上述例子中“:”可以使用“!”代替
 * 参数systemListener表示可以监听的启动和停止事件。
 * @version 2009-6-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class SubmitRoot extends HttpServlet implements Filter {
    //========================================================================================Field
    private static final long serialVersionUID = -9157250446565992949L;
    private WebSubmitContext  submitContext    = null;                 //action管理器。
    private SystemListener    listener         = null;
    private ServletContext    servletContext   = null;
    //==========================================================================================Job
    private void init(Config config) throws ServletException {
        try {
            Object listener = config.getInitParameter("listener");
            if (listener != null)
                this.listener = (SystemListener) Class.forName(listener.toString()).newInstance();
            //
            Object buildClassString = config.getInitParameter("buildClass");
            if (buildClassString == null)
                buildClassString = "org.more.submit.casing.more.MoreBuilder";
            ActionContextBuild build = (ActionContextBuild) Class.forName(buildClassString.toString()).newInstance();
            SubmitBuild sb = new SubmitBuild();
            sb.setConfig(config);
            this.submitContext = sb.buildWeb(build, (ServletContext) config.getContext());
            this.servletContext = this.submitContext.getServletContext();
            this.servletContext.setAttribute("org.more.web.submit.ROOT", this.submitContext);
            this.servletContext.setAttribute("org.more.web.submit.ROOT.Action", this.submitContext.getProtocol());
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ServletException)
                throw (ServletException) e;
            else
                throw new ServletException(e.getLocalizedMessage(), e);
        }
        //-----------------------------------------------
        if (this.listener != null)
            this.listener.start(this.servletContext, this.submitContext);
    };
    public void destroy() {
        //-----------------------------------------------
        if (this.listener != null)
            this.listener.stop(this.servletContext, this.submitContext);
        super.destroy();
    }
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
    /** 执行调用 */
    private Object doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            return this.submitContext.doAction(httpRequest, httpResponse);//执行调用
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
        if (this.submitContext.isActionRequest(req) == false)
            chain.doFilter(req, res);
        else
            this.doAction(req, res);
    };
    /** 中央调度Servlet */
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (this.submitContext.isActionRequest(req) == true)
            this.doAction(req, res);
    };
};