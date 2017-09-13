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
package net.hasor.core.context;
import net.hasor.core.*;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderInvocationHandler;
import net.hasor.core.binder.BinderHelper;
import net.hasor.core.container.BeanBuilder;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.container.ScopManager;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.*;
/**
 * 抽象类 AbstractAppContext 是 {@link AppContext} 接口的基础实现。
 * <p>它包装了大量细节代码，可以方便的通过子类来创建独特的上下文支持。<p>
 *
 * 提示：initContext 方法是整个类的入口方法。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class TemplateAppContext implements AppContext {
    public static final String       DefaultSettings = "hasor-config.xml";
    protected           Logger       logger          = LoggerFactory.getLogger(getClass());
    private final       ShutdownHook shutdownHook    = new ShutdownHook(this);
    //
    /**通过名获取Bean的类型。*/
    public Class<?> getBeanType(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        BeanContainer container = getContainer();
        BindInfo<?> bindInfo = container.findBindInfoByID(bindID);
        if (bindInfo != null) {
            return bindInfo.getBindType();
        }
        return null;
    }
    /** @return 判断是否存在某个ID的绑定。*/
    public boolean containsBindID(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        BeanContainer container = getContainer();
        BindInfo<?> bindInfo = container.findBindInfoByID(bindID);
        return bindInfo != null;
    }
    /** @return 获取已经注册的BeanID。*/
    public String[] getBindIDs() {
        BeanContainer container = getContainer();
        Collection<String> nameList = container.getBindInfoIDs();
        if (nameList == null || nameList.isEmpty()) {
            return StringUtils.EMPTY_STRING_ARRAY;
        }
        return nameList.toArray(new String[nameList.size()]);
    }
    /**如果存在目标类型的Bean则返回Bean的名称。*/
    public String[] getNames(final Class<?> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        BeanContainer container = getContainer();
        Collection<String> nameList = container.getBindInfoNamesByType(targetClass);
        if (nameList == null || nameList.isEmpty()) {
            return StringUtils.EMPTY_STRING_ARRAY;
        }
        return nameList.toArray(new String[nameList.size()]);
    }
    /*---------------------------------------------------------------------------------------Bean*/
    /**创建Bean。*/
    public <T> T getInstance(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        BeanContainer container = getContainer();
        BindInfo<T> bindInfo = container.findBindInfoByID(bindID);
        if (bindInfo != null) {
            return this.getInstance(bindInfo);
        }
        return null;
    }
    /**创建Bean。*/
    public <T> T getInstance(Class<T> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        Provider<T> provider = this.getProvider(targetClass);
        if (provider != null) {
            logger.debug("getInstance form getProvider, targetClass is {}.", targetClass);
            return provider.get();
        } else {
            logger.debug("getInstance form getDefaultInstance, targetClass is {}.", targetClass);
            return getBeanBuilder().getDefaultInstance(targetClass, this);
        }
    }
    /**创建Bean。*/
    public <T> T getInstance(final BindInfo<T> info) {
        Provider<T> provider = this.getProvider(info);
        if (provider != null) {
            return provider.get();
        }
        return null;
    }
    /**仅仅执行依赖注入。*/
    public <T> T justInject(T object) {
        if (object == null) {
            return null;
        }
        return this.justInject(object, object.getClass());
    }
    /**仅仅执行依赖注入。*/
    public <T> T justInject(T object, Class<?> beanType) {
        if (object == null || beanType == null) {
            return null;
        }
        try {
            return (T) this.getContainer().justInject(object, beanType, this);
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
    /**创建Bean。*/
    public <T> Provider<T> getProvider(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        BeanContainer container = getContainer();
        BindInfo<T> bindInfo = container.findBindInfoByID(bindID);
        if (bindInfo != null) {
            return this.getProvider(bindInfo);
        }
        return null;
    }
    /**创建Bean。*/
    public <T> Provider<T> getProvider(final Class<T> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        BindInfo<T> bindInfo = getBindInfo(targetClass);
        final AppContext appContext = this;
        //
        if (bindInfo == null) {
            return new Provider<T>() {
                public T get() {
                    return getBeanBuilder().getDefaultInstance(targetClass, appContext);
                }
            };
        } else {
            return getProvider(bindInfo);
        }
    }
    /**创建Bean。*/
    public <T> Provider<T> getProvider(final BindInfo<T> info) {
        if (info == null) {
            return null;
        }
        final AppContext appContext = this;
        Provider<T> provider = new Provider<T>() {
            public T get() {
                return getBeanBuilder().getInstance(info, appContext);
            }
        };
        return provider;
    }
    ;
    //
    /**获取用于创建Bean的{@link BeanBuilder}*/
    protected BeanBuilder getBeanBuilder() {
        return getContainer();
    }
    /**获取用于创建Bean对象的{@link BeanContainer}接口*/
    protected abstract BeanContainer getContainer();
    // 
    /*------------------------------------------------------------------------------------Binding*/
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<T> findBindingBean(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BeanContainer container = getContainer();
        List<BindInfo<T>> typeRegisterList = container.findBindInfoList(bindType);
        if (typeRegisterList == null || typeRegisterList.isEmpty()) {
            return new ArrayList<T>(0);
        }
        ArrayList<T> returnData = new ArrayList<T>();
        for (BindInfo<T> adapter : typeRegisterList) {
            T instance = this.getInstance(adapter);
            returnData.add(instance);
        }
        return returnData;
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<Provider<T>> findBindingProvider(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BeanContainer container = getContainer();
        List<BindInfo<T>> typeRegisterList = container.findBindInfoList(bindType);
        if (typeRegisterList == null || typeRegisterList.isEmpty()) {
            return new ArrayList<Provider<T>>(0);
        }
        ArrayList<Provider<T>> returnData = new ArrayList<Provider<T>>();
        for (BindInfo<T> adapter : typeRegisterList) {
            Provider<T> provider = this.getProvider(adapter);
            returnData.add(provider);
        }
        return returnData;
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> T findBindingBean(final String withName, final Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfo<T> typeRegister = this.findBindingRegister(withName, bindType);
        if (typeRegister != null) {
            return this.getInstance(typeRegister);
        }
        return null;
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> Provider<T> findBindingProvider(final String withName, final Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfo<T> typeRegister = this.findBindingRegister(withName, bindType);
        if (typeRegister != null) {
            return this.getProvider(typeRegister);
        }
        return null;
    }
    /**根据ID获取{@link BindInfo}。*/
    @Override
    public <T> BindInfo<T> getBindInfo(String bindID) {
        BeanContainer container = getContainer();
        return container.findBindInfoByID(bindID);
    }
    @Override
    public <T> BindInfo<T> getBindInfo(Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        return getContainer().findBindInfoByType(bindType);
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    @Override
    public <T> List<BindInfo<T>> findBindingRegister(Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        return getContainer().findBindInfoList(bindType);
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    @Override
    public <T> BindInfo<T> findBindingRegister(final String withName, final Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        return getContainer().findBindInfo(withName, bindType);
    }
    //
    /*------------------------------------------------------------------------------------Process*/
    /**查找Module（由Module初始化的子Module不再查找范围内）。*/
    protected Module[] findModules() {
        Environment env = this.getEnvironment();
        boolean loadErrorShow = env.getSettings().getBoolean("hasor.modules.loadErrorShow", true);
        boolean loadModule = env.getSettings().getBoolean("hasor.modules.loadModule", true);
        if (!loadModule) {
            return new Module[0];
        }
        //
        ArrayList<Module> moduleList = new ArrayList<Module>();
        String[] allModules = env.getSettings().getStringArray("hasor.modules.module");
        Set<String> moduleTypeSet = new LinkedHashSet<String>(Arrays.asList(allModules));
        for (String moduleType : moduleTypeSet) {
            if (StringUtils.isBlank(moduleType))
                continue;
            //
            try {
                Class<?> moduleClass = this.getClassLoader().loadClass(moduleType);
                moduleList.add((Module) moduleClass.newInstance());
            } catch (Throwable e) {
                logger.warn("load module Type {} is failure. -> {}:{}", moduleType, e.getClass(), e.getMessage());
                if (loadErrorShow) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return moduleList.toArray(new Module[moduleList.size()]);
    }
    /**开始进入初始化过程.*/
    protected void doInitialize() throws Throwable {
        //
    }
    /**初始化过程完成.*/
    protected void doInitializeCompleted() {
        this.getContainer().doInitializeCompleted(getEnvironment());
    }
    /**开始进入容器启动过程.*/
    protected void doStart() {
        List<ContextStartListener> listenerList = findBindingBean(ContextStartListener.class);
        for (ContextStartListener listener : listenerList) {
            listener.doStart(this);
        }
    }
    /**容器启动完成。*/
    protected void doStartCompleted() {
        List<ContextStartListener> listenerList = findBindingBean(ContextStartListener.class);
        for (ContextStartListener listener : listenerList) {
            listener.doStartCompleted(this);
        }
    }
    /**开始进入容器停止.*/
    protected void doShutdown() {
        List<ContextShutdownListener> listenerList = findBindingBean(ContextShutdownListener.class);
        for (ContextShutdownListener listener : listenerList) {
            listener.doShutdown(this);
        }
    }
    /**容器启动停止。*/
    protected void doShutdownCompleted() {
        List<ContextShutdownListener> listenerList = findBindingBean(ContextShutdownListener.class);
        for (ContextShutdownListener listener : listenerList) {
            listener.doShutdownCompleted(this);
        }
        this.getContainer().doShutdownCompleted();
    }
    //
    /*--------------------------------------------------------------------------------------Utils*/
    /**为模块创建ApiBinder。*/
    protected ApiBinder newApiBinder() throws Throwable {
        //
        // .寻找ApiBinder扩展
        Map<Class<?>, Class<?>> extBinderMap = new HashMap<Class<?>, Class<?>>();
        XmlNode[] nodeArray = this.getEnvironment().getSettings().getXmlNodeArray("hasor.apiBinderSet.binder");
        if (nodeArray != null && nodeArray.length > 0) {
            for (XmlNode atNode : nodeArray) {
                if (atNode == null) {
                    continue;
                }
                String binderTypeStr = atNode.getAttribute("type");
                String binderImplStr = atNode.getText();
                if (StringUtils.isBlank(binderTypeStr) || StringUtils.isBlank(binderImplStr)) {
                    continue;
                }
                //
                Class<?> binderType = getEnvironment().getClassLoader().loadClass(binderTypeStr);
                Class<?> binderImpl = getEnvironment().getClassLoader().loadClass(binderImplStr);
                if (!binderType.isInterface()) {
                    continue;
                }
                //
                extBinderMap.put(binderType, binderImpl);
            }
        }
        //
        // .创建扩展
        AbstractBinder binder = new AbstractBinder(this.getEnvironment()) {
            protected BeanBuilder getBeanBuilder() {
                return getContainer();
            }
            protected ScopManager getScopManager() {
                return getContainer();
            }
        };
        Map<Class<?>, Object> implMap = new HashMap<Class<?>, Object>();
        for (Map.Entry<Class<?>, Class<?>> ent : extBinderMap.entrySet()) {
            Class<?> implKey = ent.getValue();
            if (implMap.containsKey(implKey)) {
                continue;
            }
            ApiBinderCreater creater = (ApiBinderCreater) implKey.newInstance();
            Object exter = creater.createBinder(binder);
            if (exter != null) {
                implMap.put(implKey, exter);
            }
        }
        //
        // .扩展的映射（这样做的目的是保证不同key应射了同一个实现之后，实现类避免重复初始化）
        Map<Class<?>, Object> supportMap = new HashMap<Class<?>, Object>();
        supportMap.put(ApiBinder.class, binder);
        for (Map.Entry<Class<?>, Class<?>> ent : extBinderMap.entrySet()) {
            Object supportVal = implMap.get(ent.getValue());
            if (supportVal != null) {
                supportMap.put(ent.getKey(), supportVal);
            }
        }
        //
        // .返回
        Class<?>[] apiArrays = supportMap.keySet().toArray(new Class<?>[supportMap.size()]);
        return (ApiBinder) Proxy.newProxyInstance(this.
                getClassLoader(), apiArrays, new ApiBinderInvocationHandler(supportMap));
    }
    /**当完成所有初始化过程之后调用，负责向 Context 绑定一些预先定义的类型。*/
    protected void doBind(final ApiBinder apiBinder) {
        final AppContext appContet = this;
        /*绑定Settings对象的Provider*/
        apiBinder.bindType(Settings.class).toProvider(new Provider<Settings>() {
            public Settings get() {
                return appContet.getEnvironment().getSettings();
            }
        });
        /*绑定EventContext对象的Provider*/
        apiBinder.bindType(EventContext.class).toProvider(new Provider<EventContext>() {
            public EventContext get() {
                return appContet.getEnvironment().getEventContext();
            }
        });
        /*绑定Environment对象的Provider*/
        apiBinder.bindType(Environment.class).toProvider(new Provider<Environment>() {
            public Environment get() {
                return appContet.getEnvironment();
            }
        });
        /*绑定AppContext对象的Provider*/
        apiBinder.bindType(AppContext.class).toProvider(new Provider<AppContext>() {
            public AppContext get() {
                return appContet;
            }
        });
    }
    //
    /*------------------------------------------------------------------------------------Creater*/
    /**
     * 确定 AppContext 目前状态是否处于启动状态。
     * @return 返回 true 表示已经完成初始化并且启动完成。false表示尚未完成启动过程。
     */
    public boolean isStart() {
        return this.getContainer().isInit();
    }
    /**获取环境接口。*/
    public abstract Environment getEnvironment();
    /**获取当创建Bean时使用的{@link ClassLoader}*/
    public ClassLoader getClassLoader() {
        return this.getEnvironment().getClassLoader();
    }
    /**安装模块的工具方法。*/
    protected void installModule(ApiBinder apiBinder, Module module) throws Throwable {
        if (this.isStart()) {
            throw new IllegalStateException("AppContent is started.");
        }
        if (module == null) {
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("loadModule " + module.getClass());
        }
        module.loadModule(apiBinder);
        BinderHelper.onInstall(this.getEnvironment(), module);
    }
    /**
     * 模块启动通知，如果在启动期间发生异常，将会抛出该异常。
     * @param modules 启动时使用的模块。
     * @throws Throwable 启动过程中引发的异常。
     */
    public synchronized final void start(Module... modules) throws Throwable {
        if (this.isStart()) {
            logger.error("appContext is started.");
            return;
        }
        /*1.findModules*/
        logger.info("appContext -> findModules.");
        ArrayList<Module> findModules = new ArrayList<Module>();
        findModules.addAll(Arrays.asList(this.findModules()));
        findModules.addAll(Arrays.asList(modules));
        /*2.doInitialize*/
        logger.info("appContext -> doInitialize.");
        doInitialize();
        /*3.Bind*/
        ApiBinder apiBinder = newApiBinder();
        for (Module module : findModules) {
            if (module == null) {
                continue;
            }
            this.installModule(apiBinder, module);
        }
        logger.info("appContext -> doBind.");
        doBind(apiBinder);
        /*4.引发事件*/
        logger.info("appContext -> doInitializeCompleted");
        doInitializeCompleted();
        //
        //-------------------------------------------------------------------------------------------
        /*5.Start*/
        logger.info("appContext -> doStart");
        doStart();
        /*6.发送启动事件*/
        logger.info("appContext -> fireSyncEvent ,eventType = {}", ContextEvent_Started);
        getEnvironment().getEventContext().fireSyncEvent(ContextEvent_Started, this);
        logger.info("appContext -> doStartCompleted");
        doStartCompleted();/*用于扩展*/
        //
        /*7.打印状态*/
        logger.info("Hasor Started!");
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
    /**发送停止通知*/
    public synchronized final void shutdown() {
        if (!this.isStart()) {
            return;
        }
        EventContext ec = getEnvironment().getEventContext();
        /*1.Init*/
        logger.info("shutdown - doShutdown.");
        doShutdown();
        /*2.引发事件*/
        logger.info("shutdown - fireSyncEvent.");
        try {
            ec.fireSyncEvent(ContextEvent_Shutdown, this);
        } catch (Throwable throwable) {
            /**/
        }
        logger.info("shutdown - doShutdownCompleted..");
        doShutdownCompleted();
        logger.info("shutdown - finish.");
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException e) {
            if (!"Shutdown in progress".equals(e.getMessage())) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}