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
package net.hasor.plugins.templates;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.web.startup.RuntimeListener;
/**
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class TemplateContext {
    private AtomicBoolean  inited         = new AtomicBoolean(false);
    private String         controlPath    = null;                    // 区块模版位置
    private String         layoutPath     = null;                    // 布局模版位置
    private String         templatePath   = null;                    // 页面模版位置
    private TemplateEngine templateEngine = null;
    //
    public void init(ServletContext servletContext) throws ServletException {
        if (this.inited.compareAndSet(false, true) == false) {
            return;
        }
        try {
            AppContext appContext = RuntimeListener.getAppContext(servletContext);
            this.templateEngine = appContext.getInstance(TemplateEngine.class);
            this.templateEngine.initEngine(appContext);
            //
            Settings settings = appContext.getEnvironment().getSettings();
            this.controlPath = settings.getString("hasor.template.controlPath", "/control");
            this.layoutPath = settings.getString("hasor.template.layoutPath", "/layout");
            this.templatePath = settings.getString("hasor.template.templatePath", "/templates");
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
    protected String findLayout(String tempFile) throws IOException {
        if (this.templateEngine == null) {
            return null;
        }
        File layoutFile = new File(this.layoutPath, tempFile);
        if (this.templateEngine.exist(layoutFile.getPath()) == true) {
            return layoutFile.getPath();
        } else {
            layoutFile = new File(layoutFile.getParent(), "default.htm");
            if (this.templateEngine.exist(layoutFile.getPath()) == true) {
                return layoutFile.getPath();
            } else {
                while (layoutFile.getPath().startsWith(this.layoutPath)) {
                    layoutFile = new File(layoutFile.getParentFile().getParent(), "default.htm");
                    if (this.templateEngine.exist(layoutFile.getPath()) == true) {
                        return layoutFile.getPath();
                    }
                }
            }
        }
        return null;
    }
    //
    public void processTemplate(String tempFile, Writer writer, ContextMap context) throws Throwable {
        if (this.templateEngine == null) {
            return;
        }
        String layoutFile = findLayout(tempFile);
        //
        if (layoutFile != null) {
            StringWriter tmpWriter = new StringWriter();
            this.templateEngine.process(fixTempName(this.templatePath, tempFile), tmpWriter, context);
            context.put("content_placeholder", tmpWriter.toString());
            this.templateEngine.process(layoutFile, writer, context);
        } else {
            this.templateEngine.process(fixTempName(this.templatePath, tempFile), writer, context);
        }
        //
    }
    private static String fixTempName(String templatePath, String tempName) {
        if (tempName.charAt(0) != '/') {
            return templatePath + "/" + tempName;
        } else {
            return templatePath + tempName;
        }
    }
    public String processControl(String tempFile, ContextMap context) throws Throwable {
        if (this.templateEngine == null) {
            return null;
        }
        StringWriter tmpWriter = new StringWriter();
        this.templateEngine.process(this.controlPath + "/" + tempFile, tmpWriter, context);
        return tmpWriter.toString();
    }
}