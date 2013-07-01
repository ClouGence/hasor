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
package org.moreframework.view.freemarker.support;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.moreframework.MoreFramework;
import org.moreframework.view.freemarker.FreemarkerManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * Freemarker模板功能支持。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class FreemarkerHttpServlet extends HttpServlet {
    private static final long  serialVersionUID  = -8512332739621932581L;
    @Inject
    private FreemarkerManager  freemarkerManager = null;
    @Inject
    private FreemarkerSettings settings          = null;
    //
    /***/
    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        try {
            if (httpResponse.isCommitted() == true)
                return;
            HashMap<String, Object> rootMap = null;
            {
                rootMap = new HashMap<String, Object>();
                rootMap.put("request", httpRequest);
                rootMap.put("response", httpResponse);
                rootMap.put("session", httpRequest.getSession(true));
                for (Entry<String, String[]> ent : httpRequest.getParameterMap().entrySet()) {
                    String[] values = ent.getValue();
                    rootMap.put("req_" + ent.getKey(), (values == null || values.length == 0) ? null : values[0]);
                    rootMap.put("req_" + ent.getKey() + "s", values);
                }
                Enumeration<String> reqAtts = httpRequest.getAttributeNames();
                while (reqAtts.hasMoreElements()) {
                    String name = reqAtts.nextElement();
                    rootMap.put(name, httpRequest.getAttribute(name));
                }
                HttpSession httpSession = httpRequest.getSession(true);
                Enumeration<String> sesAtts = httpSession.getAttributeNames();
                while (sesAtts.hasMoreElements()) {
                    String name = sesAtts.nextElement();
                    rootMap.put(name, httpSession.getAttribute(name));
                }
            }
            httpResponse.setContentType(this.settings.getContentType());
            this.freemarkerManager.processTemplate(requestURI, rootMap, response.getWriter());
            return;
        } catch (Exception e) {
            switch (this.settings.getOnError()) {
            /**抛出异常*/
            case ThrowError:
                throw new ServletException(e);
                /**打印到控制台或日志*/
            case PrintOnConsole:
                MoreFramework.error("%s", e);
                break;
            /**忽略，仅仅产生一条警告消息*/
            case Warning:
                MoreFramework.warning("process Template error -> requestURI is %s ,message is %s", requestURI, e.getMessage());
                break;
            /**打印到页面*/
            case PrintOnPage:
                e.printStackTrace(httpResponse.getWriter());
                break;
            }
        }
    }
}