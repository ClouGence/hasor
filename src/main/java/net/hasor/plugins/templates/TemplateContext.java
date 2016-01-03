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
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.web.startup.RuntimeListener;
/**
 * 
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class TemplateContext {
    private AppContext     appContext     = null;
    private String         controlPath    = null; //区块模版位置
    private String         layoutPath     = null; //布局模版位置
    private String         templatePath   = null; //页面模版位置
    private TemplateEngine templateEngine = null;
    //
    public void init(ServletContext servletContext) throws ServletException {
        this.appContext = RuntimeListener.getAppContext(servletContext);
        this.templateEngine = this.appContext.getInstance(TemplateEngine.class);
        //
        Settings settings = this.appContext.getEnvironment().getSettings();
        this.controlPath = settings.getDirectoryPath("hasor.template.controlPath", "/control");
        this.layoutPath = settings.getDirectoryPath("hasor.template.layoutPath", "/layout");
        this.templatePath = settings.getDirectoryPath("hasor.template.templatePath", "/templates");
        //
    }
    protected String findLayout(String tempFile) {
        if (this.templateEngine == null) {
            return null;
        }
        TemplateLoader loader = this.templateEngine.getRootLoader();
        File layoutFile = new File(this.layoutPath, tempFile);
        if (loader.exist(layoutFile.getPath()) == true) {
            return layoutFile.getPath();
        } else {
            layoutFile = new File(layoutFile.getParent(), "default.htm");
            if (loader.exist(layoutFile.getPath()) == true) {
                return layoutFile.getPath();
            } else {
                while (layoutFile.getPath().startsWith(this.layoutPath)) {
                    layoutFile = new File(layoutFile.getParentFile().getParent(), "default.htm");
                    if (loader.exist(layoutFile.getPath()) == true) {
                        return layoutFile.getPath();
                    }
                }
            }
        }
        return null;
    }
    //
    public void processTemplate(String tempFile, Writer writer, ContextMap context) throws ServletException, IOException {
        if (this.templateEngine == null) {
            return;
        }
        String layoutFile = findLayout(tempFile);
        String encoding = context.getCharacterEncoding();
        //
        if (layoutFile != null) {
            StringWriter tmpWriter = new StringWriter();
            this.templateEngine.process(this.templatePath + "/" + tempFile, tmpWriter, context, encoding);
            context.put("content_placeholder", tmpWriter.toString());
            this.templateEngine.process(layoutFile, writer, context, encoding);
        } else {
            this.templateEngine.process(this.templatePath + "/" + tempFile, writer, context, encoding);
        }
        //
    }
    public String processControl(String tempFile, ContextMap context) throws ServletException, IOException {
        if (this.templateEngine == null) {
            return null;
        }
        String encoding = context.getCharacterEncoding();
        StringWriter tmpWriter = new StringWriter();
        this.templateEngine.process(this.controlPath + "/" + tempFile, tmpWriter, context, encoding);
        return tmpWriter.toString();
    }
}