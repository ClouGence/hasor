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
package net.hasor.core;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.StatusAppContext;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.core.environment.StandardEnvironment;
import org.more.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.Map;

import static net.hasor.core.AppContext.ContextEvent_Shutdown;
import static net.hasor.core.AppContext.ContextEvent_Started;
/**
 * Hasor 基础工具包。
 * @version : 2013-4-3
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class Hasor {
    protected static Logger logger = LoggerFactory.getLogger(Hasor.class);
    /**
     * 将{@link AppContextAware}接口实现类注册到容器中，Hasor 会在启动的第一时间为这些对象执行注入。
     * @param awareProvider 需要被注册的 AppContextAware 接口实现对象。
     * @return 返回 aware 参数本身。
     */
    public static <T extends AppContextAware> Provider<T> autoAware(Environment env, final Provider<T> awareProvider) {
        if (awareProvider == null) {
            return awareProvider;
        }
        Hasor.assertIsNotNull(env, "EventContext is null.");
        env.getEventContext().pushListener(ContextEvent_Started, new EventListener<AppContext>() {
            public void onEvent(String event, AppContext eventData) throws Throwable {
                awareProvider.get().setAppContext(eventData);
            }
        });
        return awareProvider;
    }
    /**
     * 将{@link AppContextAware}接口实现类注册到容器中，Hasor 会在启动的第一时间为这些对象执行注入。
     * @param aware 需要被注册的 AppContextAware 接口实现对象。
     * @return 返回 aware 参数本身。
     */
    public static <T extends AppContextAware> T autoAware(Environment env, final T aware) {
        if (aware == null) {
            return aware;
        }
        Hasor.assertIsNotNull(env, "EventContext is null.");
        env.getEventContext().pushListener(ContextEvent_Started, new EventListener<AppContext>() {
            public void onEvent(String event, AppContext eventData) throws Throwable {
                aware.setAppContext(eventData);
            }
        });
        return aware;
    }
    //
    //
    //
    public static <TD, T extends EventListener<TD>> T pushStartListener(Environment env, T eventListener) {
        env.getEventContext().pushListener(ContextEvent_Started, eventListener);
        return eventListener;
    }
    public static <TD, T extends EventListener<TD>> T pushShutdownListener(Environment env, T eventListener) {
        env.getEventContext().pushListener(ContextEvent_Shutdown, eventListener);
        return eventListener;
    }
    public static <TD, T extends EventListener<TD>> T addStartListener(Environment env, T eventListener) {
        env.getEventContext().addListener(ContextEvent_Started, eventListener);
        return eventListener;
    }
    public static <TD, T extends EventListener<TD>> T addShutdownListener(Environment env, T eventListener) {
        env.getEventContext().addListener(ContextEvent_Shutdown, eventListener);
        return eventListener;
    }
    //
    //
    //
    /**如果参数为空会抛出 NullPointerException 异常。*/
    public static <T> T assertIsNotNull(final T object) {
        return Hasor.assertIsNotNull(object, ""); //$NON-NLS-1$
    }
    /**如果参数为空会抛出 NullPointerException 异常。*/
    public static <T> T assertIsNotNull(final T object, final String message) {
        if (object == null) {
            throw new NullPointerException("null argument:" + message); //$NON-NLS-1$
        }
        return object;
    }
    //
    //
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext() {
        return Hasor.createAppContext(TemplateAppContext.DefaultSettings, null, null, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(Module... modules) {
        return Hasor.createAppContext(TemplateAppContext.DefaultSettings, null, null, modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(File mainSettings) {
        return Hasor.createAppContext(mainSettings, null, null, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(String mainSettings) {
        return Hasor.createAppContext(mainSettings, null, null, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(URI mainSettings) {
        return Hasor.createAppContext(mainSettings, null, null, new Module[0]);
    }
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(File mainSettings, Module... modules) {
        return Hasor.createAppContext(mainSettings, null, null, modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(String mainSettings, Module... modules) {
        return Hasor.createAppContext(mainSettings, null, null, modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(URI mainSettings, Module... modules) {
        return Hasor.createAppContext(mainSettings, null, null, modules);
    }
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(File mainSettings, Map<String, String> loadEnvConfig, ClassLoader loader, Module... modules) {
        return Hasor.createAppContext(mainSettings.toURI(), loadEnvConfig, loader, modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(String mainSettings, Map<String, String> loadEnvConfig, ClassLoader loader, Module... modules) {
        logger.info("create AppContext ,mainSettings = {} , modules = {}", mainSettings, modules);
        //
        try {
            Environment env = new StandardEnvironment(null, mainSettings, loadEnvConfig, loader);
            AppContext appContext = new StatusAppContext<BeanContainer>(env, new BeanContainer());
            appContext.start(modules);
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(URI mainSettings, Map<String, String> loadEnvConfig, ClassLoader loader, Module... modules) {
        logger.info("create AppContext ,mainSettings = {} , modules = {}", mainSettings, modules);
        //
        try {
            Environment env = new StandardEnvironment(null, mainSettings, loadEnvConfig, loader);
            AppContext appContext = new StatusAppContext<BeanContainer>(env, new BeanContainer());
            appContext.start(modules);
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}