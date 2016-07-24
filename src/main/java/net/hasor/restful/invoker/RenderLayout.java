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
import net.hasor.restful.RenderData;
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
class RenderLayout implements RenderEngine {
    private AtomicBoolean             inited       = new AtomicBoolean(false);
    private Map<String, RenderEngine> engineMap    = null;
    private String                    layoutPath   = null;                    // 布局模版位置
    private String                    templatePath = null;                    // 页面模版位置
    //
    //
    protected String findLayout(RenderEngine engine, String tempFile) throws IOException {
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
    public void initEngine(WebAppContext appContext) throws Throwable {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        //
        this.engine.initEngine(appContext);
        Settings settings = appContext.getEnvironment().getSettings();
        this.layoutPath = settings.getString("hasor.restful.layoutPath", "/layout");
        this.templatePath = settings.getString("hasor.restful.templatePath", "/templates");
    }
    //
    public void process(RenderData renderData, Writer writer) throws Throwable {
        if (renderData == null) {
            return;
        }
        //
        String type = renderData.getViewType();
        RenderEngine engine = this.engineMap.get(type);
        if (engine == null) {
            return;
        }
        //
        String tempName = renderData.getViewName();
        String layoutFile = findLayout(engine, tempName);
        tempName = fixTempName(this.templatePath, tempName);
        renderData.setViewName(tempName);
        //
        if (layoutFile != null) {
            StringWriter tmpWriter = new StringWriter();
            engine.process(renderData, tmpWriter);
            renderData.put("content_placeholder", tmpWriter.toString());
            renderData.setViewName(layoutFile);
            engine.process(renderData, writer);
        } else {
            engine.process(renderData, writer);
        }
        //
    }
    @Override
    public boolean exist(String template) throws IOException {
        return this.exist(template);
    }
}