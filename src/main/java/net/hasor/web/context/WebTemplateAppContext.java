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
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.container.BeanBuilder;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.DataContextCreater;
import net.hasor.core.context.StatusAppContext;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebAppContext;
import net.hasor.web.WebEnvironment;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.ListenerPipeline;
import net.hasor.web.binder.support.AbstractWebApiBinder;
import net.hasor.web.binder.support.ManagedFilterPipeline;
import net.hasor.web.binder.support.ManagedListenerPipeline;
import net.hasor.web.binder.support.ManagedServletPipeline;
import net.hasor.web.env.WebStandardEnvironment;
import org.more.util.ResourcesUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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
    @Override
    public WebEnvironment getEnvironment() {
        return (WebEnvironment) super.getEnvironment();
    }
    /**获取{@link ServletContext}*/
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    /**获取容器目前支持的 Servet Api 版本。*/
    @Override
    public ServletVersion getServletVersion() {
        return this.getEnvironment().getServletVersion();
    }
    /**为模块创建ApiBinder*/
    @Override
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
        final WebAppContext appContet = this;
        //
        /*绑定Environment对象的Provider*/
        apiBinder.bindType(WebEnvironment.class).toProvider(new Provider<WebEnvironment>() {
            public WebEnvironment get() {
                return appContet.getEnvironment();
            }
        });
        /*绑定AppContext对象的Provider*/
        apiBinder.bindType(WebAppContext.class).toProvider(new Provider<WebAppContext>() {
            public WebAppContext get() {
                return appContet;
            }
        });
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
        /*绑定AppContext对象的Provider*/
        apiBinder.bindType(WebAppContext.class).toProvider(new Provider<WebAppContext>() {
            public WebAppContext get() {
                return appContet;
            }
        });
        /*绑定AppContext对象的Provider*/
        apiBinder.bindType(WebEnvironment.class).toProvider(new Provider<WebEnvironment>() {
            public WebEnvironment get() {
                return appContet.getEnvironment();
            }
        });
        /*绑定当前Servlet支持的版本*/
        apiBinder.bindType(ServletVersion.class).toProvider(new Provider<ServletVersion>() {
            public ServletVersion get() {
                return appContet.getEnvironment().getServletVersion();
            }
        });
    }
}