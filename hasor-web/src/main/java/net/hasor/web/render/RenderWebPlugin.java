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
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.utils.StringUtils;
import net.hasor.web.*;
import net.hasor.web.annotation.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
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
 * 渲染器插件。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderWebPlugin extends WebModule implements WebPlugin, InvokerFilter {
    protected Logger        logger       = LoggerFactory.getLogger(getClass());
    private   AtomicBoolean inited       = new AtomicBoolean(false);
    private   String        layoutPath   = null;                    // 布局模版位置
    private   boolean       useLayout    = true;
    private   String        templatePath = null;                    // 页面模版位置
    private Map<String, RenderEngine> engineMap;
    //
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        apiBinder.addPlugin(this);
        apiBinder.filter("/*").through(Integer.MAX_VALUE, this);
    }
    //
    @Override
    public void init(InvokerConfig config) throws Throwable {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        //
        AppContext appContext = config.getAppContext();
        Map<String, RenderEngine> engineMap = new HashMap<String, RenderEngine>();
        Map<String, String> renderMapping = new HashMap<String, String>();
        List<RenderDefinition> renderInfoList = appContext.findBindingBean(RenderDefinition.class);
        for (RenderDefinition renderInfo : renderInfoList) {
            if (renderInfo == null) {
                continue;
            }
            logger.info("web -> renderType {} mappingTo {}.", StringUtils.join(renderInfo.getRenderSet().toArray(), ","), renderInfo.toString());
            String renderInfoID = renderInfo.getID();
            engineMap.put(renderInfoID, renderInfo.newEngine(appContext));
            //
            List<String> renderSet = renderInfo.getRenderSet();
            for (String renderName : renderSet) {
                renderMapping.put(renderName.toUpperCase(), renderInfoID);
            }
        }
        //
        this.engineMap = new HashMap<String, RenderEngine>();
        for (String key : renderMapping.keySet()) {
            //
            String keyMapping = renderMapping.get(key);
            RenderEngine engine = engineMap.get(keyMapping);
            this.engineMap.put(key, engine);
        }
        //
        Settings settings = appContext.getEnvironment().getSettings();
        this.useLayout = settings.getBoolean("hasor.layout.enable", true);
        this.layoutPath = settings.getString("hasor.layout.layoutPath", "/layout");
        this.templatePath = settings.getString("hasor.layout.templatePath", "/templates");
    }
    //
    @Override
    public void destroy() {
    }
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
                render.viewType(proValue);
                String mimeType = invoker.getMimeType(proValue);
                if (StringUtils.isBlank(mimeType)) {
                    htttResponse.setContentType(proValue);//用原始配置
                } else {
                    htttResponse.setContentType(mimeType);//用定义的配置
                }
            }
        }
    }
    @Override
    public void doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        // .执行过滤器
        chain.doNext(invoker);
        //
        // .处理渲染
        if (invoker instanceof RenderInvoker) {
            boolean process = this.process((RenderInvoker) invoker);
            if (process) {
                return;
            }
            RenderInvoker renderInvoker = (RenderInvoker) invoker;
            HttpServletRequest httpRequest = renderInvoker.getHttpRequest();
            HttpServletResponse httpResponse = renderInvoker.getHttpResponse();
            httpRequest.getRequestDispatcher(renderInvoker.renderTo()).forward(httpRequest, httpResponse);
        }
    }
    @Override
    public void afterFilter(Invoker invoker, InvokerData info) {
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
        String oriViewName = render.renderTo();
        if (this.useLayout) {
            render.renderTo(fixTempName(this.templatePath, oriViewName));
        }
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