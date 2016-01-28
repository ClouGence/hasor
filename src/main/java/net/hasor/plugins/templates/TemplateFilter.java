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
import java.util.ArrayList;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.plugins.mimetype.MimeType;
import net.hasor.web.startup.RuntimeListener;
/**
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
class TemplateFilter implements Filter {
    private MimeType        mimeType        = null;
    private TemplateContext templateContext = null;
    private String[]        interceptArrays = null;
    //
    @Override
    public void init(FilterConfig config) throws ServletException {
        AppContext appContext = RuntimeListener.getAppContext(config.getServletContext());
        this.templateContext = new TemplateContext();
        this.templateContext.init(config.getServletContext());
        this.mimeType = appContext.getInstance(MimeType.class);
        //
        Settings settings = appContext.getEnvironment().getSettings();
        String interceptNames = settings.getString("hasor.template.urlPatterns", "htm;html;");
        ArrayList<String> interceptList = new ArrayList<String>();
        for (String name : interceptNames.split(";")) {
            if (StringUtils.isBlank(name) == false) {
                interceptList.add("." + name);
            }
        }
        this.interceptArrays = interceptList.toArray(new String[interceptList.size()]);
        //
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        ContextMap contextMap = ContextMap.genContextMap(request, response);
        String requestURI = contextMap.getViewName();
        //
        if (resp.isCommitted()) {
            chain.doFilter(request, response);
            return;
        } else {
            boolean jumpOut = true;
            for (String intercept : interceptArrays) {
                if (requestURI.endsWith(intercept)) {
                    jumpOut = false;
                    break;
                }
            }
            if (jumpOut) {
                chain.doFilter(request, response);
                return;
            }
        }
        //
        try {
            String fileExt = requestURI.substring(requestURI.lastIndexOf("."));
            if (this.mimeType != null) {
                String typeMimeType = this.mimeType.getMimeType(fileExt);
                resp.setContentType(typeMimeType);
            } else {
                String typeMimeType = request.getSession(true).getServletContext().getMimeType(fileExt);
                resp.setContentType(typeMimeType);
            }
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
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}