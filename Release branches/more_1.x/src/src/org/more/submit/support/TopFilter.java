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
package org.more.submit.support;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

import org.more.core.copybean.CopyBeanUtil;
import org.more.submit.ActionManager;
import org.more.submit.CasingBuild;
import org.more.submit.CasingDirector;
import org.more.submit.Config;
/**
 * submit2.0组建对Web部分的支持，该类已经实现了Filter接口并且继承自HttpServlet类。
 * 该web支持的配置只有一个参数buildClass，表示生成器的具体类型。action参数表示请求的协议名
 * 或者action表达试参数名。默认是action。<br/>
 * TopFilter会反射的形式创建生成器。过滤器递交方式action://test.tesy?aaa=aaa
 * Date : 2009-6-29
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class TopFilter extends HttpServlet implements Filter {
    /**  */
    private static final long serialVersionUID = -9157250446565992949L;
    private ActionManager     actionManager    = null;                 //action管理器。
    private String            actionName       = "action";             //servlet存放表达试的参数名。filter用于解析action的协议前缀
    //==================================================================
    /** 执行调用 */
    private Object doAction(String exp, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        try {
            //添加线程环境参数
            this.actionManager.addThreadContextParams("request", request);
            this.actionManager.addThreadContextParams("response", response);
            //执行调用
            Object obj = this.actionManager.doAction(exp, this.getParams(request));
            this.actionManager.clearThreadContextParams();//清空线程环境参数
            return obj;
        } catch (Exception e) {
            this.actionManager.clearThreadContextParams();//清空线程环境参数
            if (e instanceof ServletException)
                throw (ServletException) e;
            else if (e instanceof IOException)
                throw (IOException) e;
            else
                throw new ServletException(e);
        }
    }
    private Map getParams(ServletRequest request) {
        Map params = new HashMap();
        CopyBeanUtil.newInstance().copy(request, params);
        return params;
    }
    private void init(Config config) throws ServletException {
        try {
            // 1.获得请求协议名
            String tem_actionName = config.getInitParameter("action");
            if (tem_actionName != null && tem_actionName.equals("") == false)
                this.actionName = tem_actionName;
            // 2.初始化ActionManager
            ServletContext sc = (ServletContext) config.getContext();
            ActionManager am = (ActionManager) sc.getAttribute("org.more.web.submit.ROOT");
            if (am == null) {
                CasingDirector director = new CasingDirector(config);//创建生成器
                CasingBuild build = (CasingBuild) Class.forName(config.getInitParameter("buildClass")).newInstance();
                director.buildManager(build);//通过CasingDirector生成manager
                this.actionManager = director.getResult();
                sc.setAttribute("org.more.web.submit.ROOT", this.actionManager);
                sc.setAttribute("org.more.web.submit.ROOT.Action", this.actionName);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    // ============================================================================
    /** 过滤器初始化方法，该方法调用init(InitParameter param) */
    @Override
    public void init(final FilterConfig config) throws ServletException {
        this.init(new FilterSubmitConfig(config));
    }
    /** Servlet初始化方法，该方法调用init(InitParameter param) */
    @Override
    public void init() throws ServletException {
        final ServletConfig config = this.getServletConfig();
        this.init(new ServletSubmitConfig(config));
    }
    // ============================================================================
    /** 中央调度过滤器 */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String headInfo = ((HttpServletRequest) request).getRequestURL().toString();
        Pattern p = Pattern.compile("/" + this.actionName + ":(//){0,1}(.*)(\\?.*){0,}");// /post:(//){0,1}(.*)(\?.*){0,}
        Matcher m = p.matcher(headInfo);
        //
        if (m.find()) {
            String exp = m.group(2);//获取请求参数中的调用表达试
            this.doAction(exp, request, response); //执行调用
        } else
            //如果找不到请求表达试或者根本不是post://协议的请求则继续处理。
            chain.doFilter(request, response);
    }
    /** 中央调度Servlet */
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        String exp = request.getParameter(this.actionName);//获取请求参数中的调用表达试
        this.doAction(exp, request, response); //执行调用
    }
}
