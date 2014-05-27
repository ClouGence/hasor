/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.project.common.plugins.freemarker.install;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.hasor.core.Hasor;
import net.hasor.web.startup.RuntimeFilter;
import net.test.project.common.plugins.freemarker.FreemarkerService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-7-31
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class FmServlet extends HttpServlet {
    private static final long serialVersionUID = -8512332739621932581L;
    @Inject
    private FreemarkerService fmService        = null;
    //
    /***/
    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        try {
            if (httpResponse.isCommitted() == true)
                return;
            httpResponse.setContentType("text/html");
            this.processTpl(requestURI, httpRequest, httpResponse, null, httpResponse.getWriter());
            return;
        } catch (Exception ee) {
            Throwable e = ee;
            if (e instanceof TemplateException)
                e = e.getCause();
            else if (e instanceof ServletException)
                e = e.getCause();
            Hasor.logError("%s", e);
        }
    }
    public String processTpl(String requestURI, Map<String, Object> rootMap) throws ServletException {
        HttpServletRequest httpRequest = RuntimeFilter.getLocalRequest();
        HttpServletResponse httpResponse = RuntimeFilter.getLocalResponse();
        StringWriter sw = new StringWriter();
        processTpl(requestURI, httpRequest, httpResponse, rootMap, sw);
        return sw.toString();
    }
    private void processTpl(String requestURI, HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String, Object> root, Writer sw) throws ServletException {
        Map<String, Object> rootMap = root;
        {
            rootMap = (root == null) ? new HashMap<String, Object>() : new HashMap<String, Object>(root);
            rootMap.put("request", httpRequest);
            rootMap.put("response", httpResponse);
            rootMap.put("session", httpRequest.getSession(true));
            Map<String, String[]> paramMap = httpRequest.getParameterMap();
            for (Entry<String, String[]> ent : paramMap.entrySet()) {
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
        try {
            this.fmService.processTemplate(requestURI, rootMap, sw);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}