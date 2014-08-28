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
package net.hasor.web.startup;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.web.WebAppContext;
import net.hasor.web.binder.ListenerPipeline;
import net.hasor.web.context.WebStandardAppContext;
import org.more.util.ContextClassLoaderLocal;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-3-25
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeListener implements ServletContextListener, HttpSessionListener {
    public static final String                             AppContextName          = AppContext.class.getName();
    private WebAppContext                                  appContext              = null;
    private ListenerPipeline                               sessionListenerPipeline = null;
    private static ContextClassLoaderLocal<ServletContext> LocalServletContext     = new ContextClassLoaderLocal<ServletContext>();
    private static ContextClassLoaderLocal<AppContext>     LocalAppContext         = new ContextClassLoaderLocal<AppContext>();
    /*----------------------------------------------------------------------------------------------------*/
    //
    /**创建{@link WebAppContext}对象*/
    protected WebAppContext createAppContext(final ServletContext sc) throws Throwable {
        return new WebStandardAppContext("hasor-config.xml", sc);
    }
    //
    /**获取启动模块*/
    protected Module getStartModule(ServletContext sc) throws Exception {
        //
        //1.Start Module.
        Module startModule = null;
        String startModuleType = sc.getInitParameter("startModule");
        if (StringUtils.isBlank(startModuleType)) {
            Hasor.logWarn("startModule is undefinition.");
        } else {
            Class<Module> startModuleClass = (Class<Module>) Thread.currentThread().getContextClassLoader().loadClass(startModuleType);
            startModule = startModuleClass.newInstance();
            Hasor.logInfo("startModule is %s.", startModuleType);
        }
        return startModule;
    }
    //
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        try {
            //1.create AppContext
            final ServletContext sc = servletContextEvent.getServletContext();
            this.appContext = this.createAppContext(sc);
            if (this.appContext.isStart() == false) {
                Module startModule = this.getStartModule(sc);
                this.appContext.start(startModule);
            }
            //
            RuntimeListener.LocalServletContext.set(servletContextEvent.getServletContext());
            RuntimeListener.LocalAppContext.set(this.appContext);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
        //2.获取SessionListenerPipeline
        this.sessionListenerPipeline = this.appContext.getInstance(ListenerPipeline.class);
        this.sessionListenerPipeline.init(this.appContext);
        Hasor.logInfo("sessionListenerPipeline created.");
        //3.放入ServletContext环境。
        Hasor.logInfo("ServletContext Attribut : " + RuntimeListener.AppContextName + " -->> " + Hasor.logString(this.appContext));
        servletContextEvent.getServletContext().setAttribute(RuntimeListener.AppContextName, this.appContext);
        this.sessionListenerPipeline.contextInitialized(servletContextEvent);
    }
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        if (this.sessionListenerPipeline != null) {
            this.sessionListenerPipeline.contextDestroyed(servletContextEvent);
        }
    }
    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        if (this.sessionListenerPipeline != null) {
            this.sessionListenerPipeline.sessionCreated(se);
        }
    }
    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        if (this.sessionListenerPipeline != null) {
            this.sessionListenerPipeline.sessionDestroyed(se);
        }
    }
    //
    /**获取{@link ServletContext}*/
    public static ServletContext getLocalServletContext() {
        return RuntimeListener.LocalServletContext.get();
    }
    //
    /**获取{@link AppContext}*/
    public static AppContext getLocalAppContext() {
        return RuntimeListener.LocalAppContext.get();
    }
}