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
import net.hasor.core.BindInfo;
import net.hasor.core.Settings;
import net.hasor.utils.StringUtils;
import net.hasor.web.*;
import net.hasor.web.binder.RenderDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * 渲染器插件。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderWebPlugin implements WebModule, InvokerFilter {
    private static Logger                    logger        = LoggerFactory.getLogger(RenderWebPlugin.class);
    private        String                    layoutPath    = null;                    // 布局模版位置
    private        boolean                   useLayout     = true;
    private        String                    templatePath  = null;                    // 页面模版位置
    private        Map<String, RenderEngine> engineMap     = new HashMap<>();
    private        String                    placeholder   = null;
    private        String                    defaultLayout = null;

    @Override
    public void loadModule(WebApiBinder apiBinder) {
        BindInfo<InvokerFilter> filterInfo = apiBinder.bindType(InvokerFilter.class)  //
                .idWith(RenderWebPlugin.class.getName()).toInstance(this).toInfo();
        apiBinder.filter("/*").through(Integer.MIN_VALUE, filterInfo);
    }

    public void onStart(AppContext appContext) throws Throwable {
        List<RenderDef> renderInfoList = appContext.findBindingBean(RenderDef.class);
        for (RenderDef renderInfo : renderInfoList) {
            String renderName = renderInfo.getRenderName();
            logger.info("web -> renderName {}.", renderName);
            this.engineMap.put(renderName.toUpperCase(), renderInfo.newEngine(appContext));
        }
        //
        Settings settings = appContext.getEnvironment().getSettings();
        this.useLayout = settings.getBoolean("hasor.layout.enable", true);
        this.layoutPath = settings.getString("hasor.layout.layoutPath", "/layout");
        this.templatePath = settings.getString("hasor.layout.templatePath", "/templates");
        this.placeholder = settings.getString("hasor.layout.placeholder", "content_placeholder");
        this.defaultLayout = settings.getString("hasor.layout.defaultLayout", "default.htm");
        logger.info("RenderPlugin init -> useLayout={}, layoutPath={}, templatePath={}, placeholder={}, defaultLayout={}",//
                this.useLayout, this.layoutPath, this.templatePath, this.placeholder, this.defaultLayout);
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        if (invoker instanceof RenderInvoker) {
            return doRenderInvoker((RenderInvoker) invoker, chain);
        } else {
            return chain.doNext(invoker);
        }
    }

    private static RenderType findRenderType(Annotation[] annotations) {
        return Arrays.stream(annotations).map(annotation -> {
            if (annotation instanceof RenderType) {
                return (RenderType) annotation;
            }
            return annotation.annotationType().getAnnotation(RenderType.class);
        }).filter((Predicate<Annotation>) Objects::nonNull).findFirst().orElse(null);
    }

    public Object doRenderInvoker(RenderInvoker invoker, InvokerChain chain) throws Throwable {
        // Layout 预设
        if (this.useLayout) {
            invoker.layoutEnable();
        } else {
            invoker.layoutDisable();
        }
        //
        // 处理 RenderType
        RenderEngine specialEngine = null;
        if (invoker.ownerMapping() != null) {
            Method method = invoker.ownerMapping().findMethod(invoker.getHttpRequest());
            RenderType renderType = findRenderType(method.getAnnotations());
            if (renderType == null) {
                renderType = findRenderType(method.getDeclaringClass().getAnnotations());
            }
            if (renderType != null && StringUtils.isNotBlank(renderType.value())) {
                invoker.renderType(renderType.value());
                String mimeType = invoker.getMimeType(renderType.value());
                if (StringUtils.isNotBlank(mimeType)) {
                    invoker.contentType(mimeType);
                }
            }
            if (renderType != null && renderType.engineType() != RenderType.DEFAULT.class) {
                specialEngine = invoker.getAppContext().getInstance(renderType.engineType());
            }
        }
        //
        // .执行过滤器
        Object returnData = chain.doNext(invoker);
        if (invoker.getHttpResponse().isCommitted()) {
            return returnData;
        }
        //
        // .处理渲染
        if (this.process(invoker, specialEngine)) {
            return returnData;
        }
        // .如果处理渲染失败，但是isCommitted = false，那么做服务端转发 renderTo
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        if (!httpResponse.isCommitted()) {
            RequestDispatcher requestDispatcher = httpRequest.getRequestDispatcher(invoker.renderTo());
            if (requestDispatcher != null) {
                requestDispatcher.forward(httpRequest, httpResponse);
            }
        }
        return returnData;
    }

    public boolean process(RenderInvoker render, RenderEngine engine) throws Throwable {
        if (engine == null) {
            String renderType = render.renderType();
            engine = this.engineMap.get(renderType);
            if (engine == null) {
                return false;
            }
        }
        //
        String oriViewName = render.renderTo();
        String newViewName = render.renderTo();
        if (render.layout()) {
            newViewName = this.templatePath + ((oriViewName.charAt(0) != '/') ? "/" : "") + oriViewName;
        }
        //
        String layoutFile = null;
        if (render.layout()) {
            layoutFile = findLayout(engine, oriViewName);
        }
        //
        if (layoutFile != null) {
            //先执行目标页面,然后在渲染layout
            StringWriter tmpWriter = new StringWriter();
            if (engine.exist(newViewName)) {
                render.renderTo(newViewName);
                engine.process(render, tmpWriter);
            } else {
                return false;
            }
            //渲染layout
            render.put(this.placeholder, tmpWriter.toString());
            if (engine.exist(layoutFile)) {
                render.renderTo(layoutFile);
                engine.process(render, render.getHttpResponse().getWriter());
                return true;
            } else {
                throw new IOException("layout '" + layoutFile + "' file is missing.");//不可能发生这个错误。
            }
        } else {
            if (engine.exist(newViewName)) {
                render.renderTo(newViewName);
                engine.process(render, render.getHttpResponse().getWriter());
                return true;
            } else {
                return false;//没有执行模版
            }
        }
    }

    protected String findLayout(RenderEngine engine, String tempFile) throws IOException {
        File layoutFile = new File(this.layoutPath, tempFile);
        if (engine.exist(layoutFile.getPath())) {
            return layoutFile.getPath();
        } else {
            layoutFile = new File(layoutFile.getParent(), this.defaultLayout);
            if (engine.exist(layoutFile.getPath())) {
                return layoutFile.getPath();
            } else {
                while (layoutFile.getPath().startsWith(this.layoutPath)) {
                    layoutFile = new File(layoutFile.getParentFile().getParent(), this.defaultLayout);
                    if (engine.exist(layoutFile.getPath())) {
                        return layoutFile.getPath();
                    }
                }
            }
        }
        return null;
    }
}
