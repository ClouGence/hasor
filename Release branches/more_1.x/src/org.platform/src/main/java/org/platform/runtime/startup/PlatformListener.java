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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.more.util.ClassUtil;
import org.platform.Assert;
import org.platform.api.context.Config;
import org.platform.api.context.ContextEvent;
import org.platform.api.context.ContextListener;
import org.platform.api.context.InitContext;
import org.platform.api.context.InitListener;
import org.platform.runtime.Platform;
import org.platform.runtime.config.PlatformConfig;
import org.platform.runtime.context.AbstractInitContext;
import org.platform.runtime.support.SafetyServiceSupportListener;
import org.platform.runtime.support.ServicesServiceSupportListener;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
/**
 * 该类实现启动过程中如下动作：<br/>
 * <pre>
 * 1.SpanClasses -> 2.Sort InitHook -> 3.Create InitHook ->
 * 4.Create Event & InitContext -> 5.Create Guice -> 6.do ContextListener
 * </pre>
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class PlatformListener implements ServletContextListener {
    protected final static String PlatformBuild = PlatformBuild.class.getName();
    private List<ContextListener> initListener  = new ArrayList<ContextListener>();
    /*----------------------------------------------------------------------------------------------------*/
    //
    /**子类可以自定义Injector对象，但要将systemModule加入到Module中，否则初始化过程会失败。*/
    protected Injector getInjector(Module platformStartModule) {
        return Guice.createInjector(platformStartModule);
    }
    //
    /**获取内置的{@link ContextListener}，这些监听器不可删除，子类可以通过该方法得到系统定义了哪些内置监听器。*/
    protected final List<Class<?>> internalInitListenerClasses() {
        List<Class<?>> internal = new ArrayList<Class<?>>();
        internal.add(ServicesServiceSupportListener.class);
        internal.add(SafetyServiceSupportListener.class);
        //
        return internal;
    }
    //
    /**获取监听器类型集合，用于搜索程序中所有标记了InitListener注解的类型。*/
    protected Set<Class<?>> findInitListenerClasses() {
        String[] spanPackage = new String[] { "org.*", "com.*", "net.*", "cn.*" };
        Platform.info("loadPackages : " + Platform.logString(spanPackage));
        return ClassUtil.getClassSet(spanPackage, InitListener.class);
    }
    //
    /**该方法会最终决定初始化哪些InitListener。初始化顺序按照返回集合的顺序。*/
    protected List<Class<?>> decideInitListenerClasses(List<Class<?>> sourceList) {
        Collections.sort(sourceList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                InitListener o1Anno = o1.getAnnotation(InitListener.class);
                InitListener o2Anno = o2.getAnnotation(InitListener.class);
                int o1AnnoIndex = o1Anno.startIndex();
                int o2AnnoIndex = o2Anno.startIndex();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        return sourceList;
    }
    //
    /**创建{@link ContextListener}类型对象*/
    protected ContextListener createInitListenerClasse(Class<?> listenerClass) {
        try {
            return (ContextListener) listenerClass.newInstance();
        } catch (Exception e) {
            Platform.error("create " + Platform.logString(listenerClass) + " an error.", e);
            return null;
        }
    }
    /*----------------------------------------------------------------------------------------------------*/
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //1.扫描所有ContextListener。
        Set<Class<?>> initHookSet = this.findInitListenerClasses();
        initHookSet = (initHookSet == null) ? new HashSet<Class<?>>() : initHookSet;
        //2.加入内置ContextListener
        List<Class<?>> initHookList = new ArrayList<Class<?>>(initHookSet);
        List<Class<?>> internalListener = this.internalInitListenerClasses();
        for (Class<?> internal : internalListener)
            if (initHookList.contains(internal) == false)
                initHookList.add(internal);
        //3.决定最终ContextListener集合，同时决定初始化顺序。
        initHookList = this.decideInitListenerClasses(initHookList);
        Platform.info("find ContextListener : " + Platform.logString(initHookList));
        Platform.info("create ContextListener...");
        //4.初始化执行钩子。
        for (Class<?> listenerClass : initHookList) {
            if (ContextListener.class.isAssignableFrom(listenerClass) == false) {
                Platform.warning("not implemented ContextListener ：" + Platform.logString(listenerClass));
                continue;
            }
            ContextListener listenerObject = this.createInitListenerClasse(listenerClass);
            if (listenerObject != null)
                this.initListener.add(listenerObject);
        }
        //5.准备PlatformContextEvent、sysModule对象。
        final Config config = new PlatformConfig(servletContextEvent.getServletContext());
        final InitContext initContext = new AbstractInitContext(config) {};
        final PlatformContextEvent eventObject = new PlatformContextEvent(initContext);
        final List<ContextListener> listenerList = this.initListener;
        final Module sysModule = new Module() {
            @Override
            public void configure(Binder binder) {
                for (ContextListener listener : listenerList) {
                    if (listener == null)
                        continue;
                    Platform.info("send ContextEvent to : " + Platform.logString(listener.getClass()));
                    eventObject.setBinder(binder);
                    listener.onContextInitialized(eventObject);
                }
            }
        };
        //6.构建Guice并init @InitContext注解类。
        Platform.info("create guice and begin start...");
        Injector guice = this.getInjector(sysModule);
        Assert.isNotNull(guice, "can not be create Injector.");
        //7.构建初始化PlatformBuild对象
        PlatformBuild build = new PlatformBuild();
        build.buildPlatform(initContext, guice);
        //8.放入ServletContext环境。
        Platform.info("ServletContext Attribut : " + PlatformBuild + " -->> " + Platform.logString(build));
        config.getServletContext().setAttribute(PlatformBuild, build);
        Platform.info("platform started!");
    }
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        final List<ContextListener> listenerList = this.initListener;
        for (ContextListener listener : listenerList)
            listener.onContextDestroyed();
    }
    /*----------------------------------------------------------------------------------------------------*/
    /**继承ContextEvent类用于实现getBinder方法。*/
    private static class PlatformContextEvent extends ContextEvent {
        private static final long serialVersionUID = -5525268844947074998L;
        private transient Binder  binder           = null;
        protected PlatformContextEvent(InitContext initContext) {
            super(initContext);
        }
        public void setBinder(Binder binder) {
            this.binder = binder;
        }
        @Override
        public Binder getBinder() {
            return this.binder;
        }
    }
}