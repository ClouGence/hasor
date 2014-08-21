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
import java.util.List;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoFactory;
import net.hasor.core.Environment;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.context.listener.ContextInitializeListener;
import net.hasor.core.context.listener.ContextStartListener;
import org.more.util.ArrayUtils;
/**
 * 抽象类 AbstractAppContext 是 {@link AppContext} 接口的基础实现。
 * <p>它包装了大量细节代码，可以方便的通过子类来创建独特的上下文支持。<p>
 * 
 * 提示：initContext 方法是整个 AbstractAppContext 的入口方法。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractAppContext implements AppContext {
    //    /**查找RegisterInfo*/
    //    public final <T> RegisterInfoAdapter<T> getRegister(final String withName, final Class<T> bindType) {
    //        Hasor.assertIsNotNull(bindType, "bindType is null.");
    //        //
    //        Iterator<RegisterInfoAdapter<T>> registerIterator = this.getRegisterIterator(bindType);
    //        if (registerIterator == null) {
    //            return null;
    //        }
    //        while (registerIterator.hasNext()) {
    //            RegisterInfoAdapter<T> register = registerIterator.next();
    //            if (StringUtils.equals(withName, register.getBindName())) {
    //                return register;
    //            }
    //        }
    //        return null;
    //    }
    //    /**根据Type查找RegisterInfo迭代器*/
    //    public final <T> Iterator<RegisterInfoAdapter<T>> getRegisterIterator(final Class<T> bindType) {
    //        Hasor.assertIsNotNull(bindType, "bindType is null.");
    //        //
    //        Iterator<RegisterInfoAdapter<T>> registerIterator = this.localRegisterIterator(bindType);
    //        RegisterScope parentScope = this.getParentScope();
    //        if (parentScope != null) {
    //            Iterator<RegisterInfoAdapter<T>> parentIterator = parentScope.getRegisterIterator(bindType);
    //            registerIterator = MergeUtils.mergeIterator(registerIterator, parentIterator);
    //        }
    //        return registerIterator;
    //    }
    //    /**查找所有RegisterInfo迭代器*/
    //    public final Iterator<RegisterInfoAdapter<?>> getRegisterIterator() {
    //        Iterator<RegisterInfoAdapter<?>> registerIterator = this.localRegisterIterator();
    //        RegisterScope parentScope = this.getParentScope();
    //        if (parentScope != null) {
    //            Iterator<RegisterInfoAdapter<?>> parentIterator = parentScope.getRegisterIterator();
    //            registerIterator = MergeUtils.mergeIterator(registerIterator, parentIterator);
    //        }
    //        return registerIterator;
    //    }
    //    /**已注册的类型列表。*/
    //    protected Iterator<RegisterInfoAdapter<?>> localRegisterIterator() {
    //        Iterator<? extends BindInfoBuilder<?>> builderIterator = this.getRegisterFactory().getRegisterIterator();
    //        return Iterators.converIterator(builderIterator, new Converter<BindInfoBuilder<?>, RegisterInfoAdapter<?>>() {
    //            public RegisterInfoAdapter<?> converter(BindInfoBuilder<?> target) {
    //                return target.toInfo();
    //            }
    //        });
    //    }
    //    /**已注册的类型列表。*/
    //    protected <T> Iterator<RegisterInfoAdapter<T>> localRegisterIterator(final Class<T> bindType) {
    //        Iterator<? extends BindInfoBuilder<T>> builderIterator = this.getRegisterFactory().getRegisterIterator(bindType);
    //        return Iterators.converIterator(builderIterator, new Converter<BindInfoBuilder<T>, RegisterInfoAdapter<T>>() {
    //            public RegisterInfoAdapter<T> converter(BindInfoBuilder<T> target) {
    //                return target.toInfo();
    //            }
    //        });
    //    }
    //
    /*---------------------------------------------------------------------------------------Bean*/
    //    /**通过名获取Bean的类型。*/
    //    public Class<?> getBeanType(final String name) {
    //        Hasor.assertIsNotNull(name, "name is null.");
    //        //
    //        BindInfoFactory bactory = this.getBindInfoFactory();
    //        BindInfo<BeanInfo> bindInfo = bactory.getRegister(name, BeanInfo.class);
    //        if (bindInfo == null) {
    //            return null;
    //        }
    //        BeanInfo<?> info = bactory.getInstance(bindInfo);
    //        BindInfo<?> typeRegister = info.getReferInfo();;
    //        if (typeRegister != null) {
    //            return typeRegister.getBindType();
    //        }
    //        return null;
    //    }
    //    /**获取已经注册的Bean名称。*/
    //    public String[] getBeanNames() {
    //        Iterator<RegisterInfoAdapter<BeanInfo>> infoRegisterIterator = this.getRegisterIterator(BeanInfo.class);
    //        if (infoRegisterIterator == null || infoRegisterIterator.hasNext() == false) {
    //            return ArrayUtils.EMPTY_STRING_ARRAY;
    //        }
    //        //
    //        Set<String> nameSet = new HashSet<String>();
    //        while (infoRegisterIterator.hasNext()) {
    //            RegisterInfoAdapter<BeanInfo> infoRegister = infoRegisterIterator.next();
    //            BeanInfo<?> info = infoRegister.getProvider().get();
    //            String[] names = info.getNames();
    //            for (String n : names) {
    //                nameSet.add(n);
    //            }
    //        }
    //        return nameSet.toArray(new String[nameSet.size()]);
    //    }
    //    /**创建Bean。*/
    //    public <T> T getBean(final String name) {
    //        Hasor.assertIsNotNull(name, "name is null.");
    //        //
    //        RegisterInfoAdapter<BeanInfo> infoRegister = this.getRegister(name, BeanInfo.class);
    //        if (infoRegister == null) {
    //            return null;
    //        }
    //        BeanInfo<?> info = infoRegister.getProvider().get();
    //        BindInfo<?> typeRegister = info.getReferInfo();
    //        if (typeRegister != null) {
    //            return (T) this.getBindInfoFactory().getInstance(typeRegister);
    //        }
    //        return null;
    //    };
    //  /**获取父层级*/
    //  public abstract AbstractAppContext getParent();
    /**如果存在目标类型的Bean则返回Bean的名称。*/
    public String[] getNames(final Class<?> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        //
        String[] returnData = this.getBindInfoFactory().getNamesOfType(targetClass);
        if (returnData == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return returnData;
    }
    /**创建Bean。*/
    public <T> T getInstance(final Class<T> targetClass) {
        /* 1.由于同一个Type可能会有多个注册，每个注册可能会映射了不同的实现，例如:
         *     String -> "HelloWord"   > name = "Hi"
         *     String -> "Say goodBy." > name = "By"
         *     String -> "Body .."     > name = null  (匿名的)
         * 因此查找那个没有名字的Type，倘若存在匿名的Type，返回它，否则返回null。*/
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        //
        BindInfoFactory infoFactory = this.getBindInfoFactory();
        BindInfo<T> info = infoFactory.getRegister(null, targetClass);
        if (info == null) {
            return null;
        }
        return infoFactory.getInstance(info);
    };
    /**创建Bean。*/
    public <T> T getInstance(final BindInfo<T> info) {
        return this.getBindInfoFactory().getInstance(info);
    }
    /**创建Bean。*/
    public <T> Provider<T> getProvider(final BindInfo<T> info) {
        if (info == null) {
            return null;
        }
        if (info instanceof BindInfoProviderAdapter) {
            return ((BindInfoProviderAdapter<T>) info).getProvider();
        }
        final AppContext app = this;
        return new Provider<T>() {
            public T get() {
                return app.getInstance(info);
            }
        };
    };
    /**获取用于创建Bean对象的{@link BindInfoFactory}接口*/
    protected abstract BindInfoFactory getBindInfoFactory();
    //
    /*------------------------------------------------------------------------------------Binding*/
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<T> findBindingBean(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfoFactory infoFactory = this.getBindInfoFactory();
        String[] namesOfType = this.getBindInfoFactory().getNamesOfType(bindType);
        if (namesOfType == null || namesOfType.length == 0) {
            return new ArrayList<T>(0);
        }
        ArrayList<T> returnData = new ArrayList<T>();
        for (String name : namesOfType) {
            BindInfo<T> info = infoFactory.getRegister(name, bindType);
            Provider<T> provider = this.getProvider(info);
            if (provider != null) {
                T obj = provider.get();
                if (obj != null) {
                    returnData.add(obj);
                }
            }
        }
        return returnData;
    };
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<Provider<T>> findBindingProvider(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfoFactory infoFactory = this.getBindInfoFactory();
        String[] namesOfType = this.getBindInfoFactory().getNamesOfType(bindType);
        if (namesOfType == null || namesOfType.length == 0) {
            return new ArrayList<Provider<T>>(0);
        }
        ArrayList<Provider<T>> returnData = new ArrayList<Provider<T>>();
        for (String name : namesOfType) {
            BindInfo<T> info = infoFactory.getRegister(name, bindType);
            Provider<T> provider = this.getProvider(info);
            if (provider != null) {
                returnData.add(provider);
            }
        }
        return returnData;
    };
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> T findBindingBean(final String withName, final Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfo<T> info = this.getBindInfoFactory().getRegister(withName, bindType);
        if (info != null) {
            Provider<T> provider = this.getProvider(info);
            if (provider != null) {
                return provider.get();
            }
        }
        return null;
    };
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> Provider<T> findBindingProvider(final String withName, final Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfo<T> typeRegister = this.getBindInfoFactory().getRegister(withName, bindType);
        if (typeRegister != null) {
            return this.getProvider(typeRegister);
        }
        return null;
    };
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<BindInfo<T>> findBindingRegister(final Class<T> bindType) {
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfoFactory infoFactory = this.getBindInfoFactory();
        String[] namesOfType = this.getBindInfoFactory().getNamesOfType(bindType);
        if (namesOfType == null || namesOfType.length == 0) {
            return new ArrayList<BindInfo<T>>(0);
        }
        ArrayList<BindInfo<T>> returnData = new ArrayList<BindInfo<T>>();
        for (String name : namesOfType) {
            BindInfo<T> info = infoFactory.getRegister(name, bindType);
            if (info != null) {
                returnData.add(info);
            }
        }
        return returnData;
    };
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> BindInfo<T> findBindingRegister(final String withName, final Class<T> bindType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindType, "bindType is null.");
        //
        BindInfo<T> typeRegister = this.getBindInfoFactory().getRegister(withName, bindType);
        if (typeRegister != null) {
            return typeRegister;
        }
        return null;
    };
    //
    /*--------------------------------------------------------------------------------------Event*/
    public void pushListener(final String eventType, final EventListener eventListener) {
        this.getEnvironment().pushListener(eventType, eventListener);
    }
    public void addListener(final String eventType, final EventListener eventListener) {
        this.getEnvironment().addListener(eventType, eventListener);
    }
    public void removeListener(final String eventType, final EventListener eventListener) {
        this.getEnvironment().removeListener(eventType, eventListener);
    }
    public void fireSyncEvent(final String eventType, final Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, objects);
    }
    public void fireSyncEvent(final String eventType, final EventCallBackHook callBack, final Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, callBack, objects);
    }
    public void fireAsyncEvent(final String eventType, final Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, objects);
    }
    public void fireAsyncEvent(final String eventType, final EventCallBackHook callBack, final Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, callBack, objects);
    }
    //
    /*------------------------------------------------------------------------------------Context*/
    /**获取上下文*/
    public Object getContext() {
        return this.getEnvironment().getContext();
    }
    /**获取应用程序配置。*/
    public Settings getSettings() {
        return this.getEnvironment().getSettings();
    };
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(final Class<?> featureType) {
        return this.getEnvironment().findClass(featureType);
    }
    //
    /*------------------------------------------------------------------------------------Process*/
    /**开始进入初始化过程.*/
    protected void doInitialize() {
        BindInfoFactory registerFactory = this.getBindInfoFactory();
        if (registerFactory instanceof ContextInitializeListener) {
            ApiBinder apiBinder = this.newApiBinder(null);
            ((ContextInitializeListener) registerFactory).doInitialize(apiBinder);
        }
    }
    /**初始化过程完成.*/
    protected void doInitializeCompleted() {
        BindInfoFactory registerFactory = this.getBindInfoFactory();
        if (registerFactory instanceof ContextInitializeListener) {
            ((ContextInitializeListener) registerFactory).doInitializeCompleted(this);
        }
    }
    /**开始进入容器启动过程.*/
    protected void doStart() {
        BindInfoFactory registerFactory = this.getBindInfoFactory();
        if (registerFactory instanceof ContextStartListener) {
            ((ContextStartListener) registerFactory).doStart(this);
        }
    }
    /**容器启动完成。*/
    protected void doStartCompleted() {
        BindInfoFactory registerFactory = this.getBindInfoFactory();
        if (registerFactory instanceof ContextStartListener) {
            ((ContextStartListener) registerFactory).doStartCompleted(this);
        }
    }
    //
    /*--------------------------------------------------------------------------------------Utils*/
    /**为模块创建ApiBinder。*/
    protected ApiBinder newApiBinder(final Module forModule) {
        return new AbstractBinder(this.getEnvironment()) {
            protected BindInfoFactory getBindTypeFactory() {
                return AbstractAppContext.this.getBindInfoFactory();
            }
        };
    }
    /**当完成所有初始化过程之后调用，负责向 Context 绑定一些预先定义的类型。*/
    protected void doBind(final ApiBinder apiBinder) {
        final AbstractAppContext appContet = this;
        /*绑定Environment对象的Provider*/
        apiBinder.bindType(Environment.class).toProvider(new Provider<Environment>() {
            public Environment get() {
                return appContet.getEnvironment();
            }
        });
        /*绑定Settings对象的Provider*/
        apiBinder.bindType(Settings.class).toProvider(new Provider<Settings>() {
            public Settings get() {
                return appContet.getSettings();
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
    private boolean startState = false;
    public boolean isStart() {
        return this.startState;
    }
    /**安装模块的工具方法。*/
    protected void installModule(Module module) throws Throwable {
        if (this.isStart()) {
            throw new IllegalStateException("AppContent is started.");
        }
        if (module == null) {
            return;
        }
        ApiBinder apiBinder = this.newApiBinder(module);
        module.loadModule(apiBinder);
    }
    public synchronized final void start(Module... modules) throws Throwable {
        if (this.isStart()) {
            return;
        }
        final AbstractAppContext appContext = this;
        /*1.Init*/
        Hasor.logInfo("send init sign...");
        appContext.doInitialize();
        /*2.Bind*/
        if (modules != null && modules.length > 0) {
            for (Module module : modules) {
                this.installModule(module);
            }
        }
        ApiBinder apiBinder = appContext.newApiBinder(null);
        appContext.doBind(apiBinder);
        /*3.引发事件*/
        appContext.fireSyncEvent(EventContext.ContextEvent_Initialized, apiBinder);
        appContext.doInitializeCompleted();
        Hasor.logInfo("the init is completed!");
        //
        /*3.Start*/
        Hasor.logInfo("send start sign...");
        appContext.doStart();
        /*2.执行Aware通知*/
        List<AppContextAware> awareList = appContext.findBindingBean(AppContextAware.class);
        if (awareList.isEmpty() == false) {
            for (AppContextAware weak : awareList) {
                weak.setAppContext(appContext);
            }
        }
        /*3.发送启动事件*/
        appContext.fireSyncEvent(EventContext.ContextEvent_Started, appContext);
        appContext.doStartCompleted();/*用于扩展*/
        /*3.打印状态*/
        this.startState = true;
        Hasor.logInfo("Hasor Started now!");
    }
}