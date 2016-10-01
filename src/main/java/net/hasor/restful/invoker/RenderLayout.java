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
import net.hasor.core.BindInfo;
import net.hasor.core.Settings;
import net.hasor.restful.Render;
import net.hasor.restful.RenderData;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
class RenderLayout {
    protected Logger                    logger       = LoggerFactory.getLogger(getClass());
    private   AtomicBoolean             inited       = new AtomicBoolean(false);
    private   Map<String, RenderEngine> engineMap    = new HashMap<String, RenderEngine>();
    private   String                    layoutPath   = null;                    // 布局模版位置
    private   boolean                   useLayout    = true;
    private   String                    templatePath = null;                    // 页面模版位置
    //
    public void initEngine(WebAppContext appContext) throws Throwable {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        //
        List<BindInfo<RenderEngine>> engineInfoList = appContext.findBindingRegister(RenderEngine.class);
        for (BindInfo<RenderEngine> info : engineInfoList) {
            RenderEngine engine = appContext.getInstance(info);
            if (engine == null) {
                continue;
            }
            if (info.getMetaData("FORM-XML") != null) {
                //来自XML
                this.engineMap.put(info.getBindName(), engine);
            } else {
                Render renderInfo = engine.getClass().getAnnotation(Render.class);
                if (renderInfo != null && renderInfo.value().length > 0) {
                    String[] renderTypeArray = renderInfo.value();
                    for (String renderType : renderTypeArray) {
                        logger.info("restful -> renderType {} mappingTo {}.", renderType, engine.getClass());
                        this.engineMap.put(renderType.toUpperCase(), engine);
                    }
                }
            }
        }
        //
        Settings settings = appContext.getEnvironment().getSettings();
        this.layoutPath = settings.getString("hasor.restful.layout.layoutPath", "/layout");
        this.templatePath = settings.getString("hasor.restful.layout.templatePath", "/templates");
        this.useLayout = settings.getBoolean("hasor.restful.layout.enable", true);
        //
        for (RenderEngine engine : this.engineMap.values()) {
            engine.initEngine(appContext);
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
    public boolean process(RenderData renderData) throws Throwable {
        if (renderData == null) {
            return false;
        }
        String type = renderData.viewType();
        RenderEngine engine = this.engineMap.get(type);
        if (engine == null) {
            return false;
        }
        if (renderData.getHttpResponse().isCommitted()) {
            return false;
        }
        //
        //
        String oriViewName = renderData.renderTo();
        renderData.renderTo(fixTempName(this.templatePath, oriViewName));
        //
        String layoutFile = null;
        if (this.useLayout && renderData.layout()) {
            layoutFile = findLayout(engine, oriViewName);
        }
        //
        if (layoutFile != null) {
            //先执行目标页面,然后在渲染layout
            StringWriter tmpWriter = new StringWriter();
            if (engine.exist(renderData.renderTo())) {
                engine.process(renderData, tmpWriter);
            } else {
                tmpWriter.write("");
            }
            //渲染layout
            renderData.put("content_placeholder", tmpWriter.toString());
            renderData.renderTo(layoutFile);
            if (engine.exist(renderData.renderTo())) {
                engine.process(renderData, renderData.getHttpResponse().getWriter());
                return true;
            } else {
                throw new IOException("layout '" + layoutFile + "' file is missing.");//不可能发生这个错误。
            }
        } else {
            if (engine.exist(renderData.renderTo())) {
                engine.process(renderData, renderData.getHttpResponse().getWriter());
                return true;
            } else {
                return false;//没有执行模版
            }
        }
        //
    }
}