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
package net.hasor.web.startup;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.listener.ListenerPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
/**
 *
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeListener implements ServletContextListener, HttpSessionListener {
    protected           Logger           logger           = LoggerFactory.getLogger(getClass());
    public static final String           AppContextName   = AppContext.class.getName();
    private             AppContext       appContext       = null;
    private             ListenerPipeline listenerPipeline = null;
    /*----------------------------------------------------------------------------------------------------*/
    //
    /**创建{@link AppContext}对象*/
    protected Hasor createAppContext(final ServletContext sc) throws Throwable {
        final class WebHasor extends Hasor {
            protected WebHasor(Object context) {
                super(context);
            }
        }
        //
        String webContextDir = sc.getRealPath("/");
        return WebHasor.create(sc).putFrameworkData("HASOR_WEBROOT", webContextDir);
    }
    //
    /**获取启动模块*/
    protected Module getStartModule(ServletContext sc) throws Exception {
        //
        //1.Start Module.
        Module startModule = null;
        String startModuleType = sc.getInitParameter("startModule");
        if (StringUtils.isBlank(startModuleType)) {
            logger.info("web initModule is undefinition.");
        } else {
            Class<Module> startModuleClass = (Class<Module>) Thread.currentThread().getContextClassLoader().loadClass(startModuleType);
            startModule = startModuleClass.newInstance();
            logger.info("web initModule is " + startModuleType);
        }
        return startModule;
    }
    //
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        //1.create AppContext
        try {
            ServletContext sc = servletContextEvent.getServletContext();
            Module startModule = this.getStartModule(sc);
            this.appContext = this.createAppContext(sc).build(startModule);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        //2.获取SessionListenerPipeline
        this.listenerPipeline = this.appContext.getInstance(ListenerPipeline.class);
        this.listenerPipeline.init(this.appContext);
        logger.info("sessionListenerPipeline created.");
        //3.放入ServletContext环境。
        logger.info("ServletContext Attribut is " + RuntimeListener.AppContextName);
        servletContextEvent.getServletContext().setAttribute(RuntimeListener.AppContextName, this.appContext);
        this.listenerPipeline.contextInitialized(servletContextEvent);
    }
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        if (this.listenerPipeline != null) {
            this.listenerPipeline.contextDestroyed(servletContextEvent);
        }
        this.appContext.shutdown();
        logger.info("shutdown.");
    }
    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        if (this.listenerPipeline != null) {
            this.listenerPipeline.sessionCreated(se);
        }
    }
    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        if (this.listenerPipeline != null) {
            this.listenerPipeline.sessionDestroyed(se);
        }
    }
    //
    /**获取{@link AppContext}*/
    public static AppContext getAppContext(ServletContext servletContext) {
        return (AppContext) servletContext.getAttribute(RuntimeListener.AppContextName);
    }
}