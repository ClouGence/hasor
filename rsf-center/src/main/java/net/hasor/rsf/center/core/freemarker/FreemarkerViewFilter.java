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
package net.hasor.rsf.center.core.freemarker;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.mvc.support.ContextMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class FreemarkerViewFilter implements Filter, InjectMembers {
    public void init(FilterConfig filterConfig) throws ServletException {}
    public void destroy() {}
    //
    //
    private AppContext    appContext;
    private Configuration configuration = null;
    public void doInject(AppContext appContext) {
        this.appContext = appContext;
        this.configuration = appContext.getInstance(Configuration.class);
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Map<String, Object> dataModel = this.appContext.getInstance(ContextMap.class);
        if (dataModel == null) {
            dataModel = new HashMap<String, Object>();
        }
        try {
            String tempPath = ((HttpServletRequest) request).getRequestURI();
            Template temp = this.configuration.getTemplate(tempPath, request.getCharacterEncoding());
            if (temp != null) {
                temp.process(dataModel, response.getWriter());
            }
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}