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
import net.hasor.core.context.ContainerCreater;
import net.hasor.core.context.StatusAppContext;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static net.hasor.core.AppContext.ContextEvent_Shutdown;
import static net.hasor.core.AppContext.ContextEvent_Started;
/**
 * Hasor 基础工具包。
 * @version : 2013-4-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class Hasor extends HashMap<String, String> {
    protected static Logger logger = LoggerFactory.getLogger(Hasor.class);
    private final Object context;
    private       Object                  mainSettings    = TemplateAppContext.DefaultSettings;
    private final List<Module>            moduleList      = new ArrayList<Module>();
    private final HashMap<String, String> frameworkConfig = new HashMap<String, String>();
    private ClassLoader      loader;
    private ContainerCreater creater;
    //
    protected Hasor(Object context) {
        this.context = context;
    }
    public Hasor setMainSettings(File mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor setMainSettings(URI mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor setMainSettings(URL mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor setMainSettings(String mainSettings) {
        this.mainSettings = mainSettings;
        return this;
    }
    public Hasor putData(String key, String value) {
        this.put(key, value);
        return this;
    }
    public Hasor putAllData(Map<String, String> mapData) {
        this.putAll(mapData);
        return this;
    }
    public Hasor putFrameworkData(String key, String value) {
        this.frameworkConfig.put(key, value);
        return this;
    }
    public Hasor putFrameworkDataMap(Map<String, String> mapData) {
        this.frameworkConfig.putAll(mapData);
        return this;
    }
    public Hasor setLoader(ClassLoader loader) {
        this.loader = loader;
        return this;
    }
    public Hasor addModules(List<Module> moduleList) {
        if (moduleList != null) {
            this.moduleList.addAll(moduleList);
        }
        return this;
    }
    public Hasor addModules(Module... modules) {
        if (modules != null) {
            this.moduleList.addAll(Arrays.asList(modules));
        }
        return this;
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public AppContext build(Module... modules) {
        return this.addModules(modules).build();
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public AppContext build() {
        //
        // .单独处理RUN_PATH
        String runPath = new File("").getAbsolutePath();
        this.frameworkConfig.put("RUN_PATH", runPath);
        if (logger.isInfoEnabled()) {
            logger.info("runPath at {}", runPath);
        }
        //
        try {
            Environment env = null;
            if (this.mainSettings == null) {
                logger.info("create AppContext ,mainSettings = {}", TemplateAppContext.DefaultSettings);
                env = new StandardEnvironment(this.context, TemplateAppContext.DefaultSettings, this.frameworkConfig, this, this.loader);
            } else if (this.mainSettings instanceof String) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                env = new StandardEnvironment(this.context, (String) this.mainSettings, this.frameworkConfig, this, this.loader);
            } else if (this.mainSettings instanceof File) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                env = new StandardEnvironment(this.context, (File) this.mainSettings, this.frameworkConfig, this, this.loader);
            } else if (this.mainSettings instanceof URI) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                env = new StandardEnvironment(this.context, (URI) this.mainSettings, this.frameworkConfig, this, this.loader);
            } else if (this.mainSettings instanceof URL) {
                logger.info("create AppContext ,mainSettings = {}", this.mainSettings);
                env = new StandardEnvironment(this.context, (URL) this.mainSettings, this.frameworkConfig, this, this.loader);
            }
            //
            BeanContainer container = null;
            if (this.creater != null) {
                container = this.creater.create(env);
            } else {
                container = new BeanContainer();
            }
            //
            AppContext appContext = new StatusAppContext(env, assertIsNotNull(container));
            appContext.start(this.moduleList.toArray(new Module[this.moduleList.size()]));
            return appContext;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    //
    //
    //
    //
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
    /**用Builder的方式创建{@link AppContext}容器。*/
    public static Hasor create() {
        return new Hasor(null);
    }
    /**用Builder的方式创建{@link AppContext}容器。*/
    public static Hasor create(Object context) {
        return new Hasor(context);
    }
    //
    //
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext() {
        return create(null).build();
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(Module... modules) {
        return create(null).build(modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(File mainSettings) {
        return create(null).setMainSettings(mainSettings).build();
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(String mainSettings) {
        return create(null).setMainSettings(mainSettings).build();
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(URI mainSettings) {
        return create(null).setMainSettings(mainSettings).build();
    }
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(File mainSettings, Module... modules) {
        return create(null).setMainSettings(mainSettings).build(modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(String mainSettings, Module... modules) {
        return create(null).setMainSettings(mainSettings).build(modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(URI mainSettings, Module... modules) {
        return create(null).setMainSettings(mainSettings).build(modules);
    }
}