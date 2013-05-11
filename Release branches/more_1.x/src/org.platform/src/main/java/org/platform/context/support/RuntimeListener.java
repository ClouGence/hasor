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
package org.platform.context.support;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.platform.Platform;
import org.platform.binder.SessionListenerPipeline;
import org.platform.context.AppContext;
/**
 * 该类实现启动过程中如下动作：<br/>
 * <pre>
 * 1.SpanClasses -> 2.add internal -> 3. Decide Listener -> 4.Create InitHook ->
 * 4.Create Event & InitContext -> 5.Create Guice -> 6.do ContextListener ->
 * </pre>
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class RuntimeListener implements ServletContextListener, HttpSessionListener {
    public static final String      AppContextName          = AppContext.class.getName();
    private AppContext              appContext              = null;
    private SessionListenerPipeline sessionListenerPipeline = null;
    /*----------------------------------------------------------------------------------------------------*/
    protected AppContext createAppContext(ServletContext sc) {
        return new PlatformAppContext(sc);
    }
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //1.创建AppContext
        this.appContext = this.createAppContext(servletContextEvent.getServletContext());
        appContext.start();
        //2.获取SessionListenerPipeline
        this.sessionListenerPipeline = this.appContext.getInstance(SessionListenerPipeline.class);
        this.sessionListenerPipeline.init(this.appContext);
        Platform.info("sessionListenerPipeline created.");
        //3.放入ServletContext环境。
        Platform.info("ServletContext Attribut : " + AppContextName + " -->> " + Platform.logString(this.appContext));
        servletContextEvent.getServletContext().setAttribute(AppContextName, this.appContext);
        Platform.info("platform started!");
    }
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        this.appContext.destroyed();
    }
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        this.sessionListenerPipeline.sessionCreated(se);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        this.sessionListenerPipeline.sessionDestroyed(se);
    }
}