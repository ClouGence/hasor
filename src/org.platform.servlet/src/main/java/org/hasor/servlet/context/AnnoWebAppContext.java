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
package org.hasor.servlet.context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletContext;
import org.hasor.annotation.context.AnnoAppContext;
import org.hasor.context.Environment;
import org.hasor.context.HasorModule;
import org.hasor.context.WorkSpace;
import org.hasor.context.environment.StandardEnvironment;
import org.hasor.servlet.binder.FilterPipeline;
import org.hasor.servlet.binder.SessionListenerPipeline;
import org.hasor.servlet.binder.support.ManagedErrorPipeline;
import org.hasor.servlet.binder.support.ManagedFilterPipeline;
import org.hasor.servlet.binder.support.ManagedServletPipeline;
import org.hasor.servlet.binder.support.ManagedSessionListenerPipeline;
import org.hasor.servlet.binder.support.WebApiBinderModule;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoWebAppContext extends AnnoAppContext {
    public AnnoWebAppContext(ServletContext servletContext) throws IOException {
        super("hasor-config.xml", servletContext);
    }
    public AnnoWebAppContext(String mainConfig, ServletContext servletContext) throws IOException {
        super(mainConfig, servletContext);
    }
    public ServletContext getServletContext() {
        if (this.getContext() instanceof ServletContext)
            return (ServletContext) this.getContext();
        else
            return null;
    }
    @Override
    protected Environment createEnvironment() {
        return new WebStandardEnvironment(this.getWorkSpace(), this.getServletContext());
    }
    @Override
    protected Injector createInjector(Module[] guiceModules) {
        Module webModule = new Module() {
            @Override
            public void configure(Binder binder) {
                /*Bind*/
                binder.bind(ManagedErrorPipeline.class);
                binder.bind(ManagedServletPipeline.class);
                binder.bind(FilterPipeline.class).to(ManagedFilterPipeline.class);
                binder.bind(SessionListenerPipeline.class).to(ManagedSessionListenerPipeline.class);
                /*绑定ServletContext对象的Provider*/
                binder.bind(ServletContext.class).toProvider(new Provider<ServletContext>() {
                    @Override
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
    @Override
    protected WebApiBinderModule newApiBinder(final HasorModule forModule, final Binder binder) {
        return new WebApiBinderModule(this) {
            @Override
            public Binder getGuiceBinder() {
                return binder;
            }
        };
    }
}
/**
 * 负责注册MORE_WEB_ROOT环境变量以及Web环境变量的维护。
 * @version : 2013-7-17
 * @author 赵永春 (zyc@byshell.org)
 */
class WebStandardEnvironment extends StandardEnvironment {
    private ServletContext servletContext = null;
    public WebStandardEnvironment(WorkSpace workSpace, ServletContext servletContext) {
        super(workSpace);
        this.servletContext = servletContext;
    }
    @Override
    protected Map<String, String> getHasorEnvironment() {
        Map<String, String> hasorEnv = super.getHasorEnvironment();
        hasorEnv.put("HASOR_WEBROOT", servletContext.getRealPath("/"));
        return hasorEnv;
    }
}