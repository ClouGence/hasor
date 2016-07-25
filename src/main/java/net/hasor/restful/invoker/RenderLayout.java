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
import org.more.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
class RenderLayout {
    private AtomicBoolean             inited       = new AtomicBoolean(false);
    private Map<String, RenderEngine> engineMap    = new HashMap<String, RenderEngine>();
    private String                    layoutPath   = null;                    // 布局模版位置
    private String                    templatePath = null;                    // 页面模版位置
    //
    //
    //
    public void initEngine(WebAppContext appContext) throws Throwable {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        //
        //
        RenderEngine engine = appContext.getInstance(RenderEngine.class);
        engine.initEngine(appContext);
        //
        Settings settings = appContext.getEnvironment().getSettings();
        this.layoutPath = settings.getString("hasor.restful.layoutPath", "/layout");
        this.templatePath = settings.getString("hasor.restful.templatePath", "/templates");
        String renderPatterns = settings.getString("hasor.restful.renderPatterns", "htm;html;");
        if (StringUtils.isNotBlank(renderPatterns)) {
            String[] renderArrays = StringUtils.split(renderPatterns, ";");
            for (String renderType : renderArrays) {
                this.engineMap.put(renderType, engine);
            }
        }
    }
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
    public boolean process(RenderData renderData, Writer writer) throws Throwable {
        if (renderData == null) {
            return false;
        }
        String type = renderData.getViewType();
        RenderEngine engine = this.engineMap.get(type);
        if (engine == null) {
            return false;
        }
        if (renderData.getHttpResponse().isCommitted()) {
            return false;
        }
        //
        //
        String tempName = renderData.getViewName();
        String layoutFile = findLayout(engine, tempName);
        tempName = fixTempName(this.templatePath, tempName);
        renderData.setViewName(tempName);
        //
        //
        if (layoutFile != null) {
            StringWriter tmpWriter = new StringWriter();
            if (engine.exist(renderData.getViewName())) {
                engine.process(renderData, tmpWriter);
            } else {
                tmpWriter.write("");
            }
            renderData.put("content_placeholder", tmpWriter.toString());
            renderData.setViewName(layoutFile);
            if (engine.exist(renderData.getViewName())) {
                engine.process(renderData, writer);
            }
        } else {
            if (engine.exist(renderData.getViewName())) {
                engine.process(renderData, writer);
            }
        }
        //
        return true;
    }
}