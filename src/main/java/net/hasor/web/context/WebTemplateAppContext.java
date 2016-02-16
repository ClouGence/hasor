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
package net.hasor.web.context;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.ServletContext;
import org.more.util.ResourcesUtils;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.container.BeanBuilder;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.DataContextCreater;
import net.hasor.core.context.StatusAppContext;
import net.hasor.web.WebAppContext;
import net.hasor.web.WebEnvironment;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.ListenerPipeline;
import net.hasor.web.binder.support.AbstractWebApiBinder;
import net.hasor.web.binder.support.ManagedFilterPipeline;
import net.hasor.web.binder.support.ManagedListenerPipeline;
import net.hasor.web.binder.support.ManagedServletPipeline;
import net.hasor.web.env.WebStandardEnvironment;
/**
 *
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebTemplateAppContext<C extends BeanContainer> extends StatusAppContext<C> implements WebAppContext {
    public static WebTemplateAppContext<? extends BeanContainer> create(String settingURI, ServletContext servletContext) throws IOException, URISyntaxException {
        URL resURL = ResourcesUtils.getResource(settingURI);
        WebEnvironment webEnv = null;
        if (resURL != null) {
            webEnv = new WebStandardEnvironment(resURL.toURI(), servletContext);
        } else {
            webEnv = new WebStandardEnvironment(null, servletContext);
        }
        BeanContainer container = new BeanContainer();
        WebTemplateAppContext<?> appContext = new WebTemplateAppContext<BeanContainer>(webEnv, container);
        return appContext;
    }
    //
    //
    private ServletContext servletContext = null;
    protected WebTemplateAppContext(WebEnvironment environment, C container) {
        super(environment, container);
        this.servletContext = environment.getServletContext();
    }
    protected WebTemplateAppContext(WebEnvironment environment, DataContextCreater<C> creater) throws Throwable {
        super(environment, creater);
        this.servletContext = environment.getServletContext();
    }
    //
    /**获取{@link ServletContext}*/
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    /**为模块创建ApiBinder*/
    protected AbstractWebApiBinder newApiBinder(final Module forModule) {
        return new AbstractWebApiBinder(this.getEnvironment()) {
            protected BeanBuilder getBeanBuilder() {
                return getContainer();
            }
        };
    }
    /**当完成所有初始化过程之后调用，负责向 Context 绑定一些预先定义的类型。*/
    protected void doBind(final ApiBinder apiBinder) {
        super.doBind(apiBinder);
        //
        ManagedServletPipeline sPipline = new ManagedServletPipeline();
        ManagedFilterPipeline fPipline = new ManagedFilterPipeline(sPipline);
        ManagedListenerPipeline lPipline = new ManagedListenerPipeline();
        //
        apiBinder.bindType(ManagedServletPipeline.class).toInstance(sPipline);
        apiBinder.bindType(FilterPipeline.class).toInstance(fPipline);
        apiBinder.bindType(ListenerPipeline.class).toInstance(lPipline);
        //
        /*绑定ServletContext对象的Provider*/
        apiBinder.bindType(ServletContext.class).toProvider(new Provider<ServletContext>() {
            public ServletContext get() {
                return getServletContext();
            }
        });
    }
}