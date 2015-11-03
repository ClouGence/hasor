/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.ServletContext;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.context.DataContext;
import net.hasor.core.context.DataContextCreater;
import net.hasor.core.context.StatusAppContext;
import net.hasor.web.WebAppContext;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.ListenerPipeline;
import net.hasor.web.binder.support.AbstractWebApiBinder;
import net.hasor.web.binder.support.ManagedFilterPipeline;
import net.hasor.web.binder.support.ManagedListenerPipeline;
import net.hasor.web.binder.support.ManagedServletPipeline;
import net.hasor.web.env.WebStandardEnvironment;
import org.more.util.ResourcesUtils;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebTemplateAppContext extends StatusAppContext<WebDataContext> implements WebAppContext {
    public WebTemplateAppContext(final String settingURI, final ServletContext servletContext) throws Throwable {
        super(new DataContextCreater<WebDataContext>() {
            public WebDataContext create(Environment env) throws IOException, URISyntaxException {
                URL resURL = ResourcesUtils.getResource(settingURI);
                if (resURL != null) {
                    return new WebDataContext(resURL.toURI(), servletContext);
                } else {
                    return new WebDataContext(servletContext);
                }
            }
        }, null);
    }
    //
    /**获取{@link ServletContext}*/
    public ServletContext getServletContext() {
        return this.getDataContext().getServletContext();
    }
    /**为模块创建ApiBinder*/
    protected AbstractWebApiBinder newApiBinder(final Module forModule) {
        return new AbstractWebApiBinder() {
            protected WebDataContext dataContext() {
                return getDataContext();
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
class WebDataContext extends DataContext {
    private WebStandardEnvironment environment;
    //
    public WebDataContext(ServletContext servletContext) {
        this.environment = new WebStandardEnvironment(servletContext);
    }
    public WebDataContext(URI settingURI, ServletContext servletContext) throws IOException {
        this.environment = new WebStandardEnvironment(settingURI, servletContext);
    }
    //
    public WebStandardEnvironment getEnvironment() {
        return this.environment;
    }
    /**获取{@link ServletContext}*/
    public ServletContext getServletContext() {
        return getEnvironment().getServletContext();
    }
}