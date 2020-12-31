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
import net.hasor.core.*;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

/**
 *
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeListener implements ServletContextListener, HttpSessionListener, ServletRequestListener {
    protected           Logger               logger           = LoggerFactory.getLogger(getClass());
    public static final String               AppContextName   = AppContext.class.getName();
    private             boolean              contextIsOutsite = false;
    private             Supplier<AppContext> appContext       = null;
    private             SpiTrigger           spiTrigger       = null;

    /*----------------------------------------------------------------------------------------------------*/
    public RuntimeListener() {
        this.contextIsOutsite = false;
    }

    public RuntimeListener(AppContext appContext) {
        this(Provider.of(Objects.requireNonNull(appContext, "appContext is null.")));
    }

    public RuntimeListener(Supplier<AppContext> appContext) {
        this.appContext = Objects.requireNonNull(appContext, "appContext is null.");
        this.contextIsOutsite = true;
    }

    /**获取{@link AppContext}*/
    public static AppContext getAppContext(ServletContext servletContext) {
        return (AppContext) servletContext.getAttribute(RuntimeListener.AppContextName);
    }
    /*----------------------------------------------------------------------------------------------------*/

    /**创建{@link AppContext}对象*/
    protected Hasor newHasor(ServletContext sc, String configName, Properties properties) throws Throwable {
        Hasor webHasor = Hasor.create(sc);
        //
        if (StringUtils.isNotBlank(configName)) {
            webHasor.mainSettingWith(configName);
        }
        if (properties != null && !properties.isEmpty()) {
            properties.forEach((key, val) -> webHasor.addVariable(key.toString(), val.toString()));
        }
        return webHasor;
    }

    /**获取启动模块*/
    protected Module newRootModule(ServletContext sc, String rootModule) throws Exception {
        if (StringUtils.isBlank(rootModule)) {
            logger.info("web initModule is undefinition.");
            return null;
        } else {
            Class<Module> startModuleClass = (Class<Module>) Thread.currentThread().getContextClassLoader().loadClass(rootModule);
            logger.info("web initModule is " + rootModule);
            return startModuleClass.newInstance();
        }
    }

    /**加载属性文件*/
    protected Properties loadEnvProperties(ServletContext sc, String envPropertieName) throws IOException {
        if (StringUtils.isBlank(envPropertieName)) {
            logger.info("properties file is undefinition.");
            return null;
        } else {
            InputStream resourceAsStream = ResourcesUtils.getResourceAsStream(envPropertieName);
            if (resourceAsStream == null) {
                logger.error("properties file is " + envPropertieName + " , but there is not exist.");
                return null;
            }
            logger.info("properties file is " + envPropertieName);
            Properties prop = new Properties();
            prop.load(new InputStreamReader(resourceAsStream, Settings.DefaultCharset));
            return prop;
        }
    }

    protected AppContext doInit(ServletContext sc) {
        try {
            String rootModule = sc.getInitParameter("hasor-root-module");       // 启动入口
            String configName = sc.getInitParameter("hasor-hconfig-name");      // 配置文件名
            String envProperties = sc.getInitParameter("hasor-envconfig-name"); // 环境变量配置
            //
            Properties properties = this.loadEnvProperties(sc, envProperties);
            Module startModule = this.newRootModule(sc, rootModule);
            //
            Hasor newHasor = this.newHasor(sc, configName, properties);
            String webContextDir = sc.getRealPath("/");
            newHasor.addVariable("HASOR_WEBROOT", webContextDir);
            return newHasor.build(startModule);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    @Override
    public final void contextInitialized(final ServletContextEvent servletContextEvent) {
        // 1. 初始化
        if (this.appContext == null) {
            this.appContext = Provider.of(this.doInit(servletContextEvent.getServletContext()));
        }
        this.spiTrigger = this.appContext.get().getInstance(SpiTrigger.class);
        // 2.放入ServletContext环境。
        logger.info("ServletContext Attribut is " + RuntimeListener.AppContextName);
        servletContextEvent.getServletContext().setAttribute(RuntimeListener.AppContextName, this.appContext.get());
        //
        this.spiTrigger.notifySpiWithoutResult(ServletContextListener.class, listener -> {
            listener.contextInitialized(servletContextEvent);
        });
    }

    @Override
    public final void contextDestroyed(final ServletContextEvent servletContextEvent) {
        this.spiTrigger.notifySpiWithoutResult(ServletContextListener.class, listener -> {
            listener.contextDestroyed(servletContextEvent);
        });
        if (!this.contextIsOutsite) {
            this.appContext.get().shutdown();
            this.logger.info("shutdown.");
        }
    }

    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        this.spiTrigger.notifySpiWithoutResult(HttpSessionListener.class, listener -> {
            listener.sessionCreated(se);
        });
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        this.spiTrigger.notifySpiWithoutResult(HttpSessionListener.class, listener -> {
            listener.sessionDestroyed(se);
        });
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        this.spiTrigger.notifySpiWithoutResult(ServletRequestListener.class, listener -> {
            listener.requestDestroyed(sre);
        });
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        this.spiTrigger.notifySpiWithoutResult(ServletRequestListener.class, listener -> {
            listener.requestInitialized(sre);
        });
    }
}
