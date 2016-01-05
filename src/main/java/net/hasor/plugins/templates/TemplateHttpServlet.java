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
package net.hasor.plugins.templates;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.ExceptionUtils;
import net.hasor.core.AppContext;
import net.hasor.plugins.mimetype.MimeType;
import net.hasor.web.startup.RuntimeListener;
/**
 * 
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
class TemplateHttpServlet extends HttpServlet {
    private static final long serialVersionUID = -4405894246041827036L;
    private MimeType          mimeType         = null;
    private TemplateContext   templateContext;
    //
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.templateContext = new TemplateContext();
        this.templateContext.init(config.getServletContext());
        AppContext appContext = RuntimeListener.getAppContext(config.getServletContext());
        this.mimeType = appContext.getInstance(MimeType.class);
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String fileExt = requestURI.substring(requestURI.lastIndexOf("."));
        String typeMimeType = null;
        if (this.mimeType != null) {
            typeMimeType = this.mimeType.getMimeType(fileExt);
        } else {
            typeMimeType = req.getSession(true).getServletContext().getMimeType(fileExt);
        }
        resp.setContentType(typeMimeType);
        try {
            ContextMap contextMap = ContextMap.genContextMap(req, resp);
            requestURI = req.getRequestURI().substring(req.getContextPath().length());
            this.templateContext.processTemplate(requestURI, resp.getWriter(), contextMap);
        } catch (Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof ServletException) {
                throw (ServletException) e;
            } else {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
    }
}