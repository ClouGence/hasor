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
package net.hasor.servlet.context;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletContext;
import net.hasor.core.Environment;
import net.hasor.core.binder.ApiBinderModule;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.module.AbstractModulePropxy;
import net.hasor.servlet.binder.FilterPipeline;
import net.hasor.servlet.binder.SessionListenerPipeline;
import net.hasor.servlet.binder.support.ManagedFilterPipeline;
import net.hasor.servlet.binder.support.ManagedServletPipeline;
import net.hasor.servlet.binder.support.ManagedSessionListenerPipeline;
import net.hasor.servlet.binder.support.WebApiBinderModule;
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
    /***/
    public AnnoWebAppContext() throws IOException {
        super();
    }
    public AnnoWebAppContext(ServletContext servletContext) throws IOException {
        this((String) null, servletContext);
    }
    /***/
    public AnnoWebAppContext(String mainSettings) throws IOException {
        super(mainSettings);
    }
    /***/
    public AnnoWebAppContext(File mainSettings) {
        super(mainSettings);
    }
    /***/
    public AnnoWebAppContext(URI mainSettings) {
        super(mainSettings);
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
        if (this.getContext() instanceof ServletContext)
            return (ServletContext) this.getContext();
        else
            return null;
    }
    protected Environment createEnvironment() {
        return new WebStandardEnvironment(this.getMainSettings(), this.getServletContext());
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
    protected ApiBinderModule newApiBinder(final AbstractModulePropxy forModule, final Binder binder) {
        return new WebApiBinderModule(this.getEnvironment(), forModule) {
            public Binder getGuiceBinder() {
                return binder;
            }
            public DependencySettings dependency() {
                return forModule;
            }
        };
    }
}
/**
 * 负责注册MORE_WEB_ROOT环境变量以及Web环境变量的维护。
 * @version : 2013-7-17
 * @author 赵永春 (zyc@hasor.net)
 */
class WebStandardEnvironment extends StandardEnvironment {
    private ServletContext servletContext;
    public WebStandardEnvironment(ServletContext servletContext) {
        super();
        this.servletContext = servletContext;
        this.initEnvironment();
    }
    public WebStandardEnvironment(URI settingURI, ServletContext servletContext) {
        super();
        this.settingURI = settingURI;
        this.servletContext = servletContext;
        this.initEnvironment();
    }
    protected EnvVars createEnvVars() {
        final WebStandardEnvironment $this = this;
        return new EnvVars(this) {
            protected Map<String, String> configEnvironment() {
                Map<String, String> hasorEnv = super.configEnvironment();
                String webContextDir = servletContext.getRealPath("/");
                hasorEnv.put("HASOR_WEBROOT", webContextDir);
                //
                /*单独处理work_home*/
                String workDir = $this.getSettings().getString("environmentVar.HASOR_WORK_HOME", "./");
                workDir = workDir.replace("/", File.separator);
                if (workDir.startsWith("." + File.separatorChar))
                    hasorEnv.put("HASOR_WORK_HOME", new File(webContextDir, workDir.substring(2)).getAbsolutePath());
                return hasorEnv;
            }
        };
    }
}