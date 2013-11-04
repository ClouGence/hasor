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
import java.util.ArrayList;
import javax.servlet.ServletContext;
import net.hasor.core.binder.ApiBinderModule;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.core.module.ModulePropxy;
import net.hasor.web.WebEnvironment;
import net.hasor.web.binder.FilterPipeline;
import net.hasor.web.binder.SessionListenerPipeline;
import net.hasor.web.binder.support.ManagedFilterPipeline;
import net.hasor.web.binder.support.ManagedServletPipeline;
import net.hasor.web.binder.support.ManagedSessionListenerPipeline;
import net.hasor.web.binder.support.WebApiBinderModule;
import net.hasor.web.env.WebStandardEnvironment;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AnnoWebAppContext extends AnnoStandardAppContext {
    public AnnoWebAppContext(ServletContext servletContext) throws IOException {
        this((String) null, servletContext);
    }
    /***/
    public AnnoWebAppContext(String mainSettings, ServletContext servletContext) throws IOException {
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
            public void configure(Binder binder) {
                /*Bind*/
                binder.bind(ManagedServletPipeline.class);
                binder.bind(FilterPipeline.class).to(ManagedFilterPipeline.class);
                binder.bind(SessionListenerPipeline.class).to(ManagedSessionListenerPipeline.class);
                /*绑定ServletContext对象的Provider*/
                binder.bind(ServletContext.class).toProvider(new Provider<ServletContext>() {
                    public ServletContext get() {
                        return getServletContext();
                    }
                });
            }
        };
        //2.
        ArrayList<Module> guiceModuleSet = new ArrayList<Module>();
        guiceModuleSet.add(webModule);
        if (guiceModules != null)
            for (Module mod : guiceModules)
                guiceModuleSet.add(mod);
        return super.createInjector(guiceModuleSet.toArray(new Module[guiceModuleSet.size()]));
    }
    protected WebEnvironment createEnvironment() {
        return new WebStandardEnvironment(this.getMainSettings(), (ServletContext) this.getContext());
    }
    protected ApiBinderModule newApiBinder(final ModulePropxy forModule, final Binder binder) {
        return new WebApiBinderModule((WebEnvironment) this.getEnvironment(), forModule) {
            public Binder getGuiceBinder() {
                return binder;
            }
            public DependencySettings dependency() {
                return forModule;
            }
        };
    }
}