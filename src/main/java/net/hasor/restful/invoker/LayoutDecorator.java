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
package net.hasor.restful.invoker;
import net.hasor.core.Settings;
import net.hasor.restful.InvokerContext;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
class LayoutDecorator implements RenderEngine {
    private AtomicBoolean inited       = new AtomicBoolean(false);
    private RenderEngine  engine       = null;
    private String        layoutPath   = null;                    // 布局模版位置
    private String        templatePath = null;                    // 页面模版位置
    //
    public LayoutDecorator(RenderEngine engine) {
        this.engine = engine;
    }
    //
    protected String findLayout(String tempFile) throws IOException {
        if (engine == null) {
            return null;
        }
        File layoutFile = new File(this.layoutPath, tempFile);
        if (engine.exist(layoutFile.getPath())) {
            return layoutFile.getPath();
        } else {
            layoutFile = new File(layoutFile.getParent(), "default.htm");
            if (engine.exist(layoutFile.getPath())) {
                return layoutFile.getPath();
            } else {
                while (layoutFile.getPath().startsWith(this.layoutPath)) {
                    layoutFile = new File(layoutFile.getParentFile().getParent(), "default.htm");
                    if (engine.exist(layoutFile.getPath())) {
                        return layoutFile.getPath();
                    }
                }
            }
        }
        return null;
    }
    //
    private static String fixTempName(String templatePath, String tempName) {
        if (tempName.charAt(0) != '/') {
            return templatePath + "/" + tempName;
        } else {
            return templatePath + tempName;
        }
    }
    //
    @Override
    public void initEngine(WebAppContext appContext) throws IOException {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        //
        Settings settings = appContext.getEnvironment().getSettings();
        this.layoutPath = settings.getString("hasor.restful.layoutPath", "/layout");
        this.templatePath = settings.getString("hasor.restful.templatePath", "/templates");
    }
    //
    public void process(InvokerContext invokerContext, Writer writer, Map<String, Object> dataModel) throws Throwable {
        if (engine == null) {
            return;
        }
        String tempName = invokerContext.getViewName();
        String layoutFile = findLayout(tempName);
        tempName = fixTempName(this.templatePath, tempName);
        invokerContext.setViewName(tempName);
        //
        if (layoutFile != null) {
            StringWriter tmpWriter = new StringWriter();
            engine.process(invokerContext, tmpWriter, dataModel);
            dataModel.put("content_placeholder", tmpWriter.toString());
            invokerContext.setViewName(layoutFile);
            engine.process(invokerContext, writer, dataModel);
        } else {
            engine.process(invokerContext, writer, dataModel);
        }
        //
    }
    @Override
    public boolean exist(String template) throws IOException {
        return this.exist(template);
    }
}