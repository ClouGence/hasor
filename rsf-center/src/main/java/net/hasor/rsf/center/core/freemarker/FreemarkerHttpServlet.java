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
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class FreemarkerHttpServlet extends HttpServlet implements InjectMembers {
    private static final long serialVersionUID = -2529079828653246786L;
    private AppContext        appContext       = null;
    private Configuration     configuration    = null;
    public void doInject(AppContext appContext) {
        this.appContext = appContext;
        this.configuration = appContext.getInstance(Configuration.class);
    }
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //
        String layoutBasePath = req.getServletContext().getRealPath("/layout");
        String tempBasePath = ("/templates/" + req.getRequestURI()).replace("//", "/");
        //
        String layoutFileStr = null;
        File layoutFile = new File(layoutBasePath, req.getRequestURI());
        if (layoutFile.exists() == false) {
            while (layoutFile.getPath().startsWith(layoutBasePath)) {
                layoutFile = new File(layoutFile.getParentFile().getParent(), "default.htm");
                if (layoutFile.exists()) {
                    layoutFileStr = "/layout/" + layoutFile.getPath().substring(layoutBasePath.length());
                    layoutFileStr = layoutFileStr.replace("\\", "/").replace("//", "/");
                    break;
                }
            }
        }
        //
        try {
            Map<String, Object> dataModel = this.appContext.getInstance(ContextMap.class);
            if (dataModel == null) {
                dataModel = new HashMap<String, Object>();
            }
            Template bodyTemp = this.configuration.getTemplate(tempBasePath, req.getCharacterEncoding());
            Template layoutTemp = this.configuration.getTemplate(layoutFileStr, req.getCharacterEncoding());
            //body
            StringWriter cacheWriter = new StringWriter();
            bodyTemp.process(dataModel, cacheWriter);
            //layout
            if (layoutTemp != null) {
                dataModel.put("content_placeholder", cacheWriter.toString());
            }
            //
            Writer respWriter = resp.getWriter();
            bodyTemp.process(dataModel, respWriter);
            //
        } catch (TemplateException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}