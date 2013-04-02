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
package org.platform.runtime.startup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.more.util.ClassUtil;
import org.platform.Assert;
import org.platform.api.context.AppContext;
import org.platform.api.context.AppContextFactory;
import org.platform.api.context.ContextConfig;
import org.platform.api.context.InitContext;
import org.platform.api.context.InitContextEvent;
import org.platform.api.context.InitContextListener;
import org.platform.runtime.PlatformFactoryFinder;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class PlatformListener implements ServletContextListener {
    protected final static String     GuiceName          = Guice.class.getName();
    protected static final String     ContextFactoryName = AppContextFactory.class.getName();
    private ContextConfig             contextConfig      = null;
    private List<InitContextListener> initListener       = new ArrayList<InitContextListener>();
    //
    //
    //
    /**子类可以自定义Injector对象，但要将systemModule加入到Module中。*/
    protected Injector getInjector(Module systemModule) {
        return null;
    }
    /**子类可以自定义Injector对象，但要将systemModule加入到Module中。*/
    protected AppContextFactory getContextFactory(ContextConfig config) {
        return null;
    }
    /**初始*/
    protected void initContext(ContextConfig config) {
        //1.扫描所有init钩子。
        Set<Class<?>> initHookSet = ClassUtil.getClassSet("org.*", InitContext.class);
        //2.对钩子进行排序。
        ArrayList<Class<?>> initHookList = new ArrayList<Class<?>>(initHookSet);
        Collections.sort(initHookList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                InitContext o1Anno = o1.getAnnotation(InitContext.class);
                InitContext o2Anno = o2.getAnnotation(InitContext.class);
                int o1AnnoIndex = o1Anno.startIndex();
                int o2AnnoIndex = o2Anno.startIndex();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.初始化执行钩子。
        for (Class<?> listenerClass : initHookList) {
            if (InitContextListener.class.isAssignableFrom(listenerClass) == false) {
                // TODO: handle exception
                continue;
            }
            try {
                InitContextListener listenerObject = (InitContextListener) listenerClass.newInstance();
                this.initListener.add(listenerObject);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        //4.准备AppContext、sysModule对象。
        AppContextFactory factory = this.getContextFactory(this.contextConfig);
        if (factory != null) {
            factory = PlatformFactoryFinder.getAppContextFactory(this.contextConfig);
        }
        Assert.isNotNull(factory, "can not be create AppContextFactory.");
        final AppContext appContext = factory.getAppContext(this.contextConfig.getServletContext());
        final List<InitContextListener> listenerList = this.initListener;
        Module sysModule = new Module() {
            @Override
            public void configure(Binder binder) {
                InitContextEvent event = new InitContextEvent(appContext, binder) {};
                for (InitContextListener listener : listenerList)
                    listener.onContextInitialized(event);
            }
        };
        //5.构建Guice并初始化InitContextListener。
        Injector guice = this.getInjector(sysModule);
        if (guice == null)
            Guice.createInjector(sysModule);
        //6.放入ServletContext环境。
        this.contextConfig.getServletContext().setAttribute(ContextFactoryName, factory);
        this.contextConfig.getServletContext().setAttribute(GuiceName, guice);
    }
    /**销毁*/
    protected void destroyContext(ContextConfig config) {
        final List<InitContextListener> listenerList = this.initListener;
        for (InitContextListener listener : listenerList)
            listener.onContextDestroyed();
    }
    //
    //
    //
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //1.创建initConfig
        this.contextConfig = new PlatformContextConfig(servletContextEvent.getServletContext());
        //2.初始化。
        this.initContext(this.contextConfig);
    }
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        this.destroyContext(this.contextConfig);
    }
}