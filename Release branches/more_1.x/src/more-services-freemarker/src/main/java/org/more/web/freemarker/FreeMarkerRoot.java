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
package org.more.web.freemarker;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.hypha.ApplicationContext;
import org.more.services.freemarker.FreemarkerService;
import org.more.services.freemarker.loader.DirTemplateLoader;
import org.more.util.StringUtil;
import org.more.util.config.Config;
import org.more.web.AbstractServletFilter;
import org.more.web.hypha.ContextLoaderListener;
import freemarker.template.TemplateException;
/**
 * freemarker 组建对Web部分的支持，该类已经实现了Filter接口并且继承自HttpServlet类。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class FreeMarkerRoot extends AbstractServletFilter {
    /*-----------------------------------------------------------------*/
    private static final long serialVersionUID  = -9157250446565992949L;
    private FreemarkerService freemarkerService = null;
    private String[]          files             = { "*.html", "*.htm", "*.flt" };
    /*-----------------------------------------------------------------*/
    protected void init(Config<ServletContext> config) throws ServletException {
        try {
            ApplicationContext context = (ApplicationContext) this.getServletContext().getAttribute(ContextLoaderListener.ContextName);
            this.freemarkerService = context.getService(FreemarkerService.class);
            File webPath = new File(this.getAbsoluteContextPath());
            this.freemarkerService.addLoader(0, new DirTemplateLoader(webPath));//Web目录
            {
                //设置参数
                Enumeration<String> enums = config.getInitParameterNames();
                while (enums.hasMoreElements()) {
                    String name = enums.nextElement();
                    this.freemarkerService.setAttribute(name, config.getInitParameter(name));
                }
            }
            {
                String fs = (String) config.getInitParameter("freemarker-files");
                if (fs != null)
                    this.files = fs.split(",");
            }
            this.getServletContext().setAttribute("org.more.freemarker.ROOT", this.freemarkerService);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ServletException)
                throw (ServletException) e;
            else
                throw new ServletException(e.getLocalizedMessage(), e);
        }
    };
    /*-----------------------------------------------------------------*/
    protected void processTemplate(String templateName, HttpServletResponse res, Map<String, Object> params) throws IOException, ServletException {
        try {
            this.freemarkerService.process(templateName, params, res.getWriter());
        } catch (TemplateException e) {
            throw new ServletException(e);
        }
    }
    /**获取一个RootMap，这个Map用于存放一些对象，当模板被执行时该Map中的内容在解析模板时可以被模板访问到。*/
    protected Map<String, Object> getRootMap(HttpServletRequest req, HttpServletResponse res) {
        Map<String, String[]> paramMap = req.getParameterMap();
        HashMap<String, Object> reqParams = new HashMap<String, Object>();
        for (String key : paramMap.keySet()) {
            String[] param = paramMap.get(key);
            if (param == null || param.length == 0)
                reqParams.put(key, null);
            else if (param.length == 1)
                reqParams.put(key, param[0]);
            else
                reqParams.put(key, param);
        }
        //输出root中的对象
        HashMap<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put("req", req);
        rootMap.put("request", req);
        rootMap.put("res", res);
        rootMap.put("response", res);
        rootMap.put("reqMap", reqParams);
        rootMap.put("session", req.getSession(true));
        rootMap.put("context", req.getSession(true).getServletContext());
        return rootMap;
    }
    /** 中央调度过滤器 */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String templateName = this.getRequestPath(req);
        //确定可以解析的扩展名
        for (String pattern : this.files)
            /**确定是否是模板*/
            if (StringUtil.matchWild(pattern, templateName) == true) {
                Map<String, Object> params = this.getRootMap(req, res);
                if (this.freemarkerService.containsTemplate(templateName) == true)
                    this.processTemplate(templateName, res, params);
                else
                    this.processTemplate("404", res, params);
                return;
            }
        /**资源访问*/
        if (new File(templateName).isDirectory() == true) {
            chain.doFilter(req, res);
            return;
        }
        InputStream in = this.freemarkerService.getResourceAsStream(templateName);
        if (in == null)
            chain.doFilter(req, res);
        else {
            OutputStream out = res.getOutputStream();
            byte[] arrayData = new byte[4096];
            String mimeType = request.getServletContext().getMimeType(templateName);
            res.setContentType(mimeType);
            int length = 0;
            while ((length = in.read(arrayData)) > 0)
                out.write(arrayData, 0, length);
            out.flush();
        }
    };
    /** 中央调度Servlet，该services只会处理模板类型文件。 */
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //
        String templateName = this.getRequestPath(req);
        Map<String, Object> params = this.getRootMap(req, res);
        //确定可以解析的扩展名
        for (String pattern : this.files)
            /**确定是否是模板*/
            if (StringUtil.matchWild(pattern, templateName) == true) {
                if (this.freemarkerService.containsTemplate(templateName) == true)
                    this.processTemplate(templateName, res, params);
                else
                    this.processTemplate("404", res, params);
                return;
            }
        params.put("message", "未配置该文件为模板类型资源，或该资源文件不存在。");
        this.processTemplate("500", res, params);
    };
    /**获取{@link FreemarkerService}对象。*/
    protected FreemarkerService getFreemarkerService() {
        return this.freemarkerService;
    };
};