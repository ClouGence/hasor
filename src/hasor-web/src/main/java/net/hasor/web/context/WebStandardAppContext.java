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
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.hasor.core.ApiBinder;
import net.hasor.core.Plugin;
import net.hasor.core.Provider;
import net.hasor.core.binder.TypeRegister;
import net.hasor.core.context.StandardAppContext;
import net.hasor.web.WebAppContext;
import net.hasor.web.WebEnvironment;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.ListenerPipeline;
import net.hasor.web.binder.support.AbstractWebApiBinder;
import net.hasor.web.binder.support.ManagedFilterPipeline;
import net.hasor.web.binder.support.ManagedListenerPipeline;
import net.hasor.web.binder.support.ManagedServletPipeline;
import net.hasor.web.env.WebStandardEnvironment;
import net.hasor.web.startup.RuntimeFilter;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebStandardAppContext extends StandardAppContext implements WebAppContext {
    public WebStandardAppContext(ServletContext servletContext) throws IOException, URISyntaxException {
        super();
        this.setContext(servletContext);
    }
    /***/
    public WebStandardAppContext(String mainSettings, ServletContext servletContext) throws IOException, URISyntaxException {
        super(mainSettings);
        this.setContext(servletContext);
    }
    /***/
    public WebStandardAppContext(File mainSettings, ServletContext servletContext) {
        super(mainSettings);
        this.setContext(servletContext);
    }
    /***/
    public WebStandardAppContext(URI mainSettings, ServletContext servletContext) {
        super(mainSettings);
        this.setContext(servletContext);
    }
    //
    /**获取{@link ServletContext}*/
    public ServletContext getServletContext() {
        return ((WebEnvironment) this.getEnvironment()).getServletContext();
    }
    protected WebEnvironment createEnvironment() {
        return new WebStandardEnvironment(this.getMainSettings(), (ServletContext) this.getContext());
    }
    /**为模块创建ApiBinder*/
    protected AbstractWebApiBinder newApiBinder(final Plugin forModule) {
        return new AbstractWebApiBinder((WebEnvironment) this.getEnvironment()) {
            protected <T> TypeRegister<T> registerType(Class<T> type) {
                return WebStandardAppContext.this.registerType(type);
            }
        };
    }
    protected void doBind(ApiBinder apiBinder) {
        super.doBind(apiBinder);
        //
        ManagedServletPipeline sPipline = new ManagedServletPipeline();
        ManagedFilterPipeline fPipline = new ManagedFilterPipeline(sPipline);
        ManagedListenerPipeline lPipline = new ManagedListenerPipeline();
        //
        apiBinder.bindingType(ManagedServletPipeline.class).toInstance(sPipline);
        apiBinder.bindingType(FilterPipeline.class).toInstance(fPipline);
        apiBinder.bindingType(ListenerPipeline.class).toInstance(lPipline);
        //
        /*绑定ServletRequest对象的Provider*/
        apiBinder.bindingType(ServletRequest.class).toProvider(new Provider<ServletRequest>() {
            public ServletRequest get() {
                return RuntimeFilter.getLocalRequest();
            }
        });
        /*绑定HttpServletRequest对象的Provider*/
        apiBinder.bindingType(HttpServletRequest.class).toProvider(new Provider<HttpServletRequest>() {
            public HttpServletRequest get() {
                return RuntimeFilter.getLocalRequest();
            }
        });
        /*绑定ServletResponse对象的Provider*/
        apiBinder.bindingType(ServletResponse.class).toProvider(new Provider<ServletResponse>() {
            public ServletResponse get() {
                return RuntimeFilter.getLocalResponse();
            }
        });
        /*绑定HttpServletResponse对象的Provider*/
        apiBinder.bindingType(HttpServletResponse.class).toProvider(new Provider<HttpServletResponse>() {
            public HttpServletResponse get() {
                return RuntimeFilter.getLocalResponse();
            }
        });
        /*绑定HttpSession对象的Provider*/
        apiBinder.bindingType(HttpSession.class).toProvider(new Provider<HttpSession>() {
            public HttpSession get() {
                HttpServletRequest req = RuntimeFilter.getLocalRequest();
                return (req != null) ? req.getSession(true) : null;
            }
        });
        /*绑定ServletContext对象的Provider*/
        apiBinder.bindingType(ServletContext.class).toProvider(new Provider<ServletContext>() {
            public ServletContext get() {
                return getServletContext();
            }
        });
    }
}