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
package net.hasor.web.render;
import net.hasor.core.*;
import net.hasor.web.*;
import net.hasor.web.annotation.Produces;
import net.hasor.web.annotation.Render;
import org.more.util.ExceptionUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * action功能的入口。
 * @version : 2013-5-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderWebPlugin extends WebModule implements WebPlugin, InvokerFilter {
    private static final String                    FORM_XML     = "FORM-XML";
    protected            Logger                    logger       = LoggerFactory.getLogger(getClass());
    private              AtomicBoolean             inited       = new AtomicBoolean(false);
    private              Map<String, RenderEngine> engineMap    = new HashMap<String, RenderEngine>();
    private              String                    layoutPath   = null;                    // 布局模版位置
    private              boolean                   useLayout    = true;
    private              String                    templatePath = null;                    // 页面模版位置
    //
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        // .Render
        Environment environment = apiBinder.getEnvironment();
        Settings settings = environment.getSettings();
        XmlNode[] xmlPropArray = settings.getXmlNodeArray("hasor.renderSet");
        Map<String, String> renderMap = new HashMap<String, String>();
        for (XmlNode xmlProp : xmlPropArray) {
            for (XmlNode envItem : xmlProp.getChildren()) {
                if (StringUtils.equalsIgnoreCase("render", envItem.getName())) {
                    String renderTypeStr = envItem.getAttribute("renderType");
                    String renderClass = envItem.getText();
                    if (StringUtils.isNotBlank(renderTypeStr)) {
                        String[] renderTypeArray = renderTypeStr.split(";");
                        for (String renderType : renderTypeArray) {
                            if (StringUtils.isNotBlank(renderType)) {
                                logger.info("restful -> renderType {} mappingTo {}.", renderType, renderClass);
                                renderMap.put(renderType.toUpperCase(), renderClass);
                            }
                        }
                    }
                }
            }
        }
        for (String key : renderMap.keySet()) {
            String type = renderMap.get(key);
            try {
                Class<?> renderType = environment.getClassLoader().loadClass(type);
                apiBinder.bindType(RenderEngine.class)//
                        .nameWith(key)//
                        .to((Class<? extends RenderEngine>) renderType)//
                        .metaData(FORM_XML, true);
            } catch (Exception e) {
                logger.error("restful -> renderType {} load failed {}.", type, e.getMessage(), e);
            }
        }
        //
        for (String key : this.engineMap.keySet()) {
            //apiBinder.serve("*." + key).with();
        }
        //
        apiBinder.addPlugin(this);
        apiBinder.filter("/*").through(Integer.MAX_VALUE, this);
    }
    //
    @Override
    public void init(InvokerConfig config) {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        //
        AppContext appContext = config.getAppContext();
        List<BindInfo<RenderEngine>> engineInfoList = appContext.findBindingRegister(RenderEngine.class);
        for (BindInfo<RenderEngine> info : engineInfoList) {
            RenderEngine engine = appContext.getInstance(info);
            if (engine == null) {
                continue;
            }
            if (info.getMetaData(FORM_XML) != null) {
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
        this.useLayout = settings.getBoolean("hasor.layout.enable", true);
        this.layoutPath = settings.getString("hasor.layout.layoutPath", "/layout");
        this.templatePath = settings.getString("hasor.layout.templatePath", "/templates");
        //
        try {
            for (RenderEngine engine : this.engineMap.values()) {
                engine.initEngine(appContext);
            }
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        //
    }
    //
    //
    @Override
    public void beforeFilter(Invoker invoker, InvokerData info) {
        if (!(invoker instanceof RenderInvoker)) {
            return;
        }
        //
        RenderInvoker render = (RenderInvoker) invoker;
        HttpServletResponse htttResponse = render.getHttpResponse();
        Method targetMethod = info.targetMethod();
        if (targetMethod.isAnnotationPresent(Produces.class)) {
            Produces pro = targetMethod.getAnnotation(Produces.class);
            String proValue = pro.value();
            if (!StringUtils.isBlank(proValue)) {
                String mimeType = invoker.getMimeType(proValue);
                if (StringUtils.isBlank(mimeType)) {
                    htttResponse.setContentType(proValue);
                    render.viewType(proValue);
                } else {
                    htttResponse.setContentType(mimeType);
                    render.viewType(mimeType);
                }
            }
        }
    }
    @Override
    public void afterFilter(Invoker invoker, InvokerData info) {
    }
    //
    @Override
    public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        // .执行过滤器
        chain.doNext(invoker);
        //
        // .处理渲染
        if (invoker instanceof RenderInvoker) {
            this.process((RenderInvoker) invoker);
        }
    }
    //
    public boolean process(RenderInvoker render) throws Throwable {
        if (render == null) {
            return false;
        }
        String type = render.viewType();
        RenderEngine engine = this.engineMap.get(type);
        if (engine == null) {
            return false;
        }
        if (render.getHttpResponse().isCommitted()) {
            return false;
        }
        //
        //
        String oriViewName = render.renderTo();
        render.renderTo(fixTempName(this.templatePath, oriViewName));
        //
        String layoutFile = null;
        if (this.useLayout && render.layout()) {
            layoutFile = findLayout(engine, oriViewName);
        }
        //
        if (layoutFile != null) {
            //先执行目标页面,然后在渲染layout
            StringWriter tmpWriter = new StringWriter();
            if (engine.exist(render.renderTo())) {
                engine.process(render, tmpWriter);
            } else {
                return false;
            }
            //渲染layout
            render.put("content_placeholder", tmpWriter.toString());
            render.renderTo(layoutFile);
            if (engine.exist(render.renderTo())) {
                engine.process(render, render.getHttpResponse().getWriter());
                return true;
            } else {
                throw new IOException("layout '" + layoutFile + "' file is missing.");//不可能发生这个错误。
            }
        } else {
            if (engine.exist(render.renderTo())) {
                engine.process(render, render.getHttpResponse().getWriter());
                return true;
            } else {
                return false;//没有执行模版
            }
        }
        //
    }
    //
    @Override
    public void destroy() {
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
    private String fixTempName(String templatePath, String tempName) {
        if (tempName.charAt(0) != '/') {
            return templatePath + "/" + tempName;
        } else {
            return templatePath + tempName;
        }
    }
}