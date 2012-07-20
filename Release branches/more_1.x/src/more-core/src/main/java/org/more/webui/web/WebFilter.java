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
package org.more.webui.web;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContext;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Lifecycle;
/**
 * Web入口
 * @version : 2012-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebFilter implements Filter {
    private Lifecycle    lifecycle = null;
    private FacesConfig  config    = null;
    private FacesContext uiContext = null;
    /*-----------------------------------------------------------------------------------*/
    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse res = (HttpServletResponse) arg1;
        if (req.getRequestURI().endsWith(this.config.getFacesSuffix()) == true) {
            /**处理Faces*/
            ViewContext viewContext = new ViewContext(req, res, this.uiContext);
            ViewContext.setCurrentViewContext(viewContext);
            this.lifecycle.execute(viewContext);
            ViewContext.setCurrentViewContext(null);
        } else {
            /**处理资源*/
            arg2.doFilter(arg0, arg1);
            return;
        }
    }
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        try {
            this.config = new FacesConfig(arg0);
            String factoryClass = this.config.getWebUIFactoryClass();
            Class<?> factory = Thread.currentThread().getContextClassLoader().loadClass(factoryClass);
            WebUIFactory webuiFactory = (WebUIFactory) factory.newInstance();
            FactoryBuild build = new FactoryBuild(webuiFactory);
            this.uiContext = build.buildFacesContext(this.config);
            this.lifecycle = build.buildLifestyle(this.config, this.uiContext);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    @Override
    public void destroy() {}
}