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
package net.hasor.core.context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Environment;
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.context.listener.ContextShutdownListener;
import net.hasor.core.context.listener.ContextStartListener;
import org.more.util.ArrayUtils;
import org.more.util.ClassUtils;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 抽象类 AbstractAppContext 是 {@link AppContext} 接口的基础实现。
 * <p>它包装了大量细节代码，可以方便的通过子类来创建独特的上下文支持。<p>
 * 
 * 提示：initContext 方法是整个类的入口方法。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class TemplateAppContext implements AppContext {
    public static final String DefaultSettings = "hasor-config.xml";
    protected Logger           logger          = LoggerFactory.getLogger(getClass());
    //
    /**通过名获取Bean的类型。*/
    public Class<?> getBeanType(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        DefineContainer container = getContextData().getBindInfoContainer();
        BindInfo<?> bindInfo = container.getBindInfoByID(bindID);
        if (bindInfo != null) {
            return bindInfo.getBindType();
        }
        return null;
    }
    /** @return 判断是否存在某个ID的绑定。*/
    public boolean containsBindID(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        DefineContainer container = getContextData().getBindInfoContainer();
        BindInfo<?> bindInfo = container.getBindInfoByID(bindID);
        return bindInfo != null;
    }
    /** @return 获取已经注册的BeanID。*/
    public String[] getBindIDs() {
        DefineContainer container = getContextData().getBindInfoContainer();
        Collection<String> nameList = container.getBindInfoIDs();
        if (nameList == null || nameList.isEmpty() == true) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return nameList.toArray(new String[nameList.size()]);
    }
    /**根据ID获取{@link BindInfo}。*/
    public <T> BindInfo<T> getBindInfo(String bindID) {
        DefineContainer container = getContextData().getBindInfoContainer();
        return container.getBindInfoByID(bindID);
    }
    /**如果存在目标类型的Bean则返回Bean的名称。*/
    public String[] getNames(final Class<?> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        DefineContainer container = getContextData().getBindInfoContainer();
        Collection<String> nameList = container.getBindInfoNamesByType(targetClass);
        if (nameList == null || nameList.isEmpty() == true) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return nameList.toArray(new String[nameList.size()]);
    }
    /*---------------------------------------------------------------------------------------Bean*/
    /**创建Bean。*/
    public <T> T getInstance(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        DefineContainer container = getContextData().getBindInfoContainer();
        BindInfo<T> bindInfo = container.getBindInfoByID(bindID);
        if (bindInfo != null) {
            return this.getInstance(bindInfo);
        }
        return null;
    }
    /**创建Bean。*/
    public <T> T getInstance(final Class<T> targetClass) {
        Provider<T> provider = this.getProvider(targetClass);
        if (provider != null) {
            return provider.get();
        }
        DefineContainer container = getContextData().getBindInfoContainer();
        return getBeanBuilder().getDefaultInstance(targetClass, container, this);
    }
    /**创建Bean。*/
    public <T> T getInstance(final BindInfo<T> info) {
        Provider<T> provider = this.getProvider(info);
        if (provider != null) {
            return provider.get();
        }
        return null;
    }
    /**创建Bean。*/
    public <T> Provider<T> getProvider(String bindID) {
        Hasor.assertIsNotNull(bindID, "bindID is null.");
        DefineContainer container = getContextData().getBindInfoContainer();
        BindInfo<T> bindInfo = container.getBindInfoByID(bindID);
        if (bindInfo != null) {
            return this.getProvider(bindInfo);
        }
        return null;
    }
    /**创建Bean。*/
    public <T> Provider<T> getProvider(final Class<T> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        Provider<T> targetProvider = null;
        //
        final DefineContainer container = getContextData().getBindInfoContainer();
        List<BindInfo<T>> typeRegisterList = container.getBindInfoByType(targetClass);
        if (typeRegisterList != null && typeRegisterList.isEmpty() == false) {
            for (int i = typeRegisterList.size() - 1; i >= 0; i--) {
                BindInfo<T> adapter = typeRegisterList.get(i);
                if (adapter.getBindName() == null) {
                    Provider<T> provider = this.getProvider(adapter);
                    if (provider != null) {
                        targetProvider = provider;
                        break;
                    }
                }
            }
        }
        if (targetProvider == null) {
            final AppContext appContext = this;
            targetProvider = new Provider<T>() {
                public T get() {
                    return getBeanBuilder().getDefaultInstance(targetClass, container, appContext);
                }
            };
        }
        return targetProvider;
    }
    /**创建Bean。*/
    public <T> Provider<T> getProvider(final BindInfo<T> info) {
        if (info == null) {
            return null;
        }
        final AppContext appContext = this;
        final DefineContainer container = getContextData().getBindInfoContainer();
        Provider<T> provider = new Provider<T>() {
            public T get() {
                return getBeanBuilder().getInstance(info, container, appContext);
            }
        };
        return provider;
    };
    //
    /**获取用于创建Bean的{@link BeanBuilder}*/
    protected BeanBuilder getBeanBuilder() {
        return getContextData().getBeanBuilder();
    }
    /**获取用于创建Bean对象的{@link ContextData}接口*/
    protected abstract ContextData getContextData();
    // 
    /*------------------------------------------------------------------------------------Binding*/
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<T> findBindingBean(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        DefineContainer container = getContextData().getBindInfoContainer();
        List<BindInfo<T>> typeRegisterList = container.getBindInfoByType(bindType);
        if (typeRegisterList == null || typeRegisterList.isEmpty()) {
            return new ArrayList<T>(0);
        }
        ArrayList<T> returnData = new ArrayList<T>();
        for (BindInfo<T> adapter : typeRegisterList) {
            T instance = this.getInstance(adapter);
            returnData.add(instance);
        }
        return returnData;
    };
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<Provider<T>> findBindingProvider(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        DefineContainer container = getContextData().getBindInfoContainer();
        List<BindInfo<T>> typeRegisterList = container.getBindInfoByType(bindType);
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
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<BindInfo<T>> findBindingRegister(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        DefineContainer container = getContextData().getBindInfoContainer();
        List<BindInfo<T>> typeRegisterList = container.getBindInfoByType(bindType);
        if (typeRegisterList == null || typeRegisterList.isEmpty()) {
            return new ArrayList<BindInfo<T>>(0);
        }
        ArrayList<BindInfo<T>> returnData = new ArrayList<BindInfo<T>>();
        for (BindInfo<T> adapter : typeRegisterList) {
            returnData.add(adapter);
        }
        return returnData;
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> BindInfo<T> findBindingRegister(final String withName, final Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        DefineContainer container = getContextData().getBindInfoContainer();
        List<BindInfo<T>> typeRegisterList = container.getBindInfoByType(bindType);
        if (typeRegisterList != null && typeRegisterList.isEmpty() == false) {
            for (BindInfo<T> adapter : typeRegisterList) {
                if (StringUtils.equals(adapter.getBindName(), withName)) {
                    return adapter;
                }
            }
        }
        return null;
    }
    //
    /*------------------------------------------------------------------------------------Process*/
    /**查找Module（由Module初始化的子Module不再查找范围内）。*/
    protected Module[] findModules() {
        ArrayList<String> moduleTyleList = new ArrayList<String>();
        Environment env = this.getEnvironment();
        boolean loadModule = env.getSettings().getBoolean("hasor.modules.loadModule", true);
        if (loadModule) {
            List<XmlNode> allModules = env.getSettings().merageXmlNode("hasor.modules", "module");
            for (XmlNode module : allModules) {
                String moduleTypeString = module.getText();
                if (StringUtils.isBlank(moduleTypeString)) {
                    continue;
                }
                if (!moduleTyleList.contains(moduleTypeString)) {
                    moduleTyleList.add(moduleTypeString);
                }
            }
        }
        //
        ArrayList<Module> moduleList = new ArrayList<Module>();
        for (String modStr : moduleTyleList) {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                Class<?> moduleType = ClassUtils.getClass(loader, modStr);
                moduleList.add((Module) moduleType.newInstance());
            } catch (Throwable e) {
                logger.warn("load module Type {} is failure. -> {}:{}", modStr, e.getClass(), e.getMessage());
                if (this.getEnvironment().isDebug())
                    logger.error(e.getMessage(), e);
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
        this.getContextData().doInitializeCompleted(this);
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
        this.getContextData().doShutdownCompleted(this);
    }
    //
    /*--------------------------------------------------------------------------------------Utils*/
    /**为模块创建ApiBinder。*/
    protected ApiBinder newApiBinder(final Module forModule) {
        return new AbstractBinder() {
            protected ContextData contextData() {
                return getContextData();
            }
        };
    }
    /**当完成所有初始化过程之后调用，负责向 Context 绑定一些预先定义的类型。*/
    protected void doBind(final ApiBinder apiBinder) {
        final AppContext appContet = this;
        /*绑定Environment对象的Provider*/
        apiBinder.bindType(Environment.class).toProvider(new Provider<Environment>() {
            public Environment get() {
                return appContet.getEnvironment();
            }
        });
        /*绑定Settings对象的Provider*/
        apiBinder.bindType(Settings.class).toProvider(new Provider<Settings>() {
            public Settings get() {
                return appContet.getEnvironment().getSettings();
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
        return this.getContextData().isStart();
    }
    /**获取环境接口。*/
    public Environment getEnvironment() {
        return this.getContextData().getEnvironment();
    }
    /**安装模块的工具方法。*/
    protected void installModule(Module module) throws Throwable {
        if (this.isStart()) {
            throw new IllegalStateException("AppContent is started.");
        }
        if (module == null) {
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("loadModule " + module.getClass());
        }
        ApiBinder apiBinder = this.newApiBinder(module);
        module.loadModule(apiBinder);
        //
        /*确保由代码加载的module也可以接收到onStart方法的调用。*/
        Hasor.onStart(this.getEnvironment(), module);
    }
    /**
     * 模块启动通知，如果在启动期间发生异常，将会抛出该异常。
     * @param modules 启动时使用的模块。
     * @throws Throwable 启动过程中引发的异常。
     */
    public synchronized final void start(Module... modules) throws Throwable {
        if (this.isStart()) {
            logger.info("Hasor started , modules is empty.");
            return;
        }
        /*1.Init*/
        logger.info("begin start , doInitialize now.");
        doInitialize();
        /*2.Bind*/
        ArrayList<Module> findModules = new ArrayList<Module>();
        findModules.addAll(Arrays.asList(this.findModules()));
        findModules.addAll(Arrays.asList(modules));
        for (Module module : findModules) {
            this.installModule(module);
        }
        ApiBinder apiBinder = newApiBinder(null);
        logger.info("AppContext doBind.");
        doBind(apiBinder);
        /*3.引发事件*/
        EventContext ec = getEnvironment().getEventContext();
        ec.fireSyncEvent(EventContext.ContextEvent_Initialized, apiBinder);
        doInitializeCompleted();
        logger.info("doInitialize completed!");
        //
        /*3.Start*/
        logger.info("doStart now.");
        doStart();
        /*4.发送启动事件*/
        ec.fireSyncEvent(EventContext.ContextEvent_Started, this);
        logger.info("doStartCompleted now.");
        doStartCompleted();/*用于扩展*/
        //
        /*5.打印状态*/
        logger.info("doStart completed!");
        logger.info("Hasor Started!");
    }
    /**发送停止通知*/
    public synchronized final void shutdown() {
        if (!this.isStart()) {
            return;
        }
        EventContext ec = getEnvironment().getEventContext();
        /*1.Init*/
        logger.info("doShutdown now.");
        doShutdown();
        /*2.引发事件*/
        ec.fireSyncEvent(EventContext.ContextEvent_Shutdown, this);
        logger.info("doShutdownCompleted now.");
        doShutdownCompleted();
        logger.info("doShutdown completed!");
    }
}