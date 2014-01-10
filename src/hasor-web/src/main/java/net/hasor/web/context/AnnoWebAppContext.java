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
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.hasor.core.binder.AbstractApiBinder;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.core.module.ModulePropxy;
import net.hasor.web.WebAppContext;
import net.hasor.web.WebEnvironment;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.SessionListenerPipeline;
import net.hasor.web.binder.support.ManagedFilterPipeline;
import net.hasor.web.binder.support.ManagedServletPipeline;
import net.hasor.web.binder.support.ManagedSessionListenerPipeline;
import net.hasor.web.binder.support.WebApiBinderModule;
import net.hasor.web.env.WebStandardEnvironment;
import net.hasor.web.startup.RuntimeFilter;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AnnoWebAppContext extends AnnoStandardAppContext implements WebAppContext {
    public AnnoWebAppContext(ServletContext servletContext) throws IOException, URISyntaxException {
        this((String) null, servletContext);
    }
    /***/
    public AnnoWebAppContext(String mainSettings, ServletContext servletContext) throws IOException, URISyntaxException {
        super(mainSettings, servletContext);
    }
    /***/
    public AnnoWebAppContext(File mainSettings, ServletContext servletContext) {
        super(mainSettings, servletContext);
    }
    /***/
    public AnnoWebAppContext(URI mainSettings, ServletContext servletContext) {
        super(mainSettings, servletContext);
    }
    //
    /**获取{@link ServletContext}*/
    public ServletContext getServletContext() {
        return ((WebEnvironment) this.getEnvironment()).getServletContext();
    }
    protected Injector createInjector(Module[] guiceModules) {
        Module webModule = new Module() {
            public void configure(Binder guiceBinder) {
                /*Bind*/
                guiceBinder.bind(ManagedServletPipeline.class).asEagerSingleton();
                guiceBinder.bind(FilterPipeline.class).to(ManagedFilterPipeline.class).asEagerSingleton();
                guiceBinder.bind(SessionListenerPipeline.class).to(ManagedSessionListenerPipeline.class).asEagerSingleton();
            }
        };
        //2.追加Guice Module
        ArrayList<Module> guiceModuleSet = new ArrayList<Module>();
        if (guiceModules != null)
            guiceModuleSet.addAll(Arrays.asList(guiceModules));
        guiceModuleSet.add(webModule);
        //3.创建Guice
        return super.createInjector(guiceModuleSet.toArray(new Module[guiceModuleSet.size()]));
    }
    protected WebEnvironment createEnvironment() {
        return new WebStandardEnvironment(this.getMainSettings(), (ServletContext) this.getContext());
    }
    protected AbstractApiBinder newApiBinder(final ModulePropxy forModule, final Binder guiceBinder) {
        return new WebApiBinderModule((WebEnvironment) this.getEnvironment()) {
            public ModuleSettings configModule() {
                return forModule;
            }
            public Binder getGuiceBinder() {
                return guiceBinder;
            }
        };
    }
    protected void doBind(Binder guiceBinder) {
        super.doBind(guiceBinder);
        /*绑定ServletRequest对象的Provider*/
        guiceBinder.bind(ServletRequest.class).toProvider(new Provider<ServletRequest>() {
            public ServletRequest get() {
                return RuntimeFilter.getLocalRequest();
            }
        });
        /*绑定HttpServletRequest对象的Provider*/
        guiceBinder.bind(HttpServletRequest.class).toProvider(new Provider<HttpServletRequest>() {
            public HttpServletRequest get() {
                return RuntimeFilter.getLocalRequest();
            }
        });
        /*绑定ServletResponse对象的Provider*/
        guiceBinder.bind(ServletResponse.class).toProvider(new Provider<ServletResponse>() {
            public ServletResponse get() {
                return RuntimeFilter.getLocalResponse();
            }
        });
        /*绑定HttpServletResponse对象的Provider*/
        guiceBinder.bind(HttpServletResponse.class).toProvider(new Provider<HttpServletResponse>() {
            public HttpServletResponse get() {
                return RuntimeFilter.getLocalResponse();
            }
        });
        /*绑定HttpSession对象的Provider*/
        guiceBinder.bind(HttpSession.class).toProvider(new Provider<HttpSession>() {
            public HttpSession get() {
                HttpServletRequest req = RuntimeFilter.getLocalRequest();
                return (req != null) ? req.getSession(true) : null;
            }
        });
        /*绑定ServletContext对象的Provider*/
        guiceBinder.bind(ServletContext.class).toProvider(new Provider<ServletContext>() {
            public ServletContext get() {
                return getServletContext();
            }
        });
    }
}