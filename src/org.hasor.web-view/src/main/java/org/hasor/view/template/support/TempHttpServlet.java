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
package org.hasor.view.template.support;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.Hasor;
import org.hasor.view.template.TemplateException;
import org.hasor.view.template.TemplateService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 模板功能支持。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class TempHttpServlet extends HttpServlet {
    private static final long serialVersionUID = -8512332739621932581L;
    @Inject
    private TemplateService   templateService  = null;
    @Inject
    private TempSettings      settings         = null;
    //
    /***/
    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        try {
            if (httpResponse.isCommitted() == true)
                return;
            httpResponse.setContentType(this.settings.getContentType());
            this.templateService.processTemplate(requestURI, httpRequest, httpResponse);
            return;
        } catch (Exception ee) {
            Throwable e = ee;
            if (e instanceof TemplateException)
                e = e.getCause();
            //
            switch (this.settings.getOnError()) {
            /**抛出异常*/
            case ThrowError:
                throw new ServletException(e);
                /**打印到控制台或日志*/
            case PrintOnConsole:
                Hasor.error("%s", e);
                break;
            /**忽略，仅仅产生一条警告消息*/
            case Warning:
                Hasor.warning("process Template error -> requestURI is %s ,message is %s", requestURI, e.getMessage());
                break;
            /**打印到页面*/
            case PrintOnPage:
                e.printStackTrace(httpResponse.getWriter());
                break;
            }
        }
    }
}