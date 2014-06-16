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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.Environment;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.Plugin;
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.Settings;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.binder.BeanInfo;
import net.hasor.core.binder.TypeRegister;
import net.hasor.core.binder.register.FreeTypeRegister;
import net.hasor.core.builder.BeanBuilder;
import net.hasor.core.context.listener.ContextInitializeListener;
import net.hasor.core.context.listener.ContextStartListener;
import org.more.util.ArrayUtils;
import org.more.util.MergeUtils;
import org.more.util.StringUtils;
/**
 * 抽象类 AbstractAppContext 是 {@link AppContext} 接口的基础实现。
 * <p>它包装了大量细节代码，可以方便的通过子类来创建独特的上下文支持。<p>
 * 
 * 提示：initContext 方法是整个 AbstractAppContext 的入口方法。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractAppContext implements AppContext, RegisterScope {
    //
    /*---------------------------------------------------------------------------------------Bean*/
    public <T> Class<T> getBeanType(String name) {
        BeanInfo info = this.findBindingBean(name, BeanInfo.class);
        return info == null ? null : (Class<T>) info.getType();
    }
    public String[] getBeanNames(Class<?> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        List<BeanInfo> infoArray = this.findBindingBean(BeanInfo.class);
        if (infoArray == null)
            infoArray = Collections.emptyList();
        /*查找*/
        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < infoArray.size(); i++) {
            BeanInfo info = infoArray.get(i);
            String[] names = info.getNames();
            for (String nameItem : names)
                nameList.add(nameItem);
        }
        return nameList.toArray(new String[nameList.size()]);
    }
    public String[] getBeanNames() {
        List<BeanInfo> infoArray = this.findBindingBean(BeanInfo.class);
        if (infoArray == null || infoArray.isEmpty())
            return ArrayUtils.EMPTY_STRING_ARRAY;
        /*查找*/
        List<String> names = new ArrayList<String>();
        for (BeanInfo info : infoArray) {
            String[] aliasNames = info.getNames();
            for (String aliasName : aliasNames)
                names.add(aliasName);
        }
        return names.toArray(new String[names.size()]);
    }
    public <T> T getInstance(String name) {
        BeanInfo info = this.findBindingBean(name, BeanInfo.class);
        if (info == null)
            return null;
        return (T) this.getInstance(info.getType());
    }
    /**创建Bean。*/
    public <T> T getBean(String name) {
        /*1.在Hasor中所有Bean定义都是绑定到BeanInfo类型上，因此查找名字为name的BeanInfo对象。*/
        RegisterInfo<BeanInfo> regInfo = this.findRegisterInfo(name, BeanInfo.class);
        if (regInfo == null)
            return null;
        /*2.得到BeanInfo对象，并取得Bean的其真实类型和referID*/
        BeanInfo beanInfo = regInfo.getProvider().get();
        RegisterInfo<?> targetInfo = this.findRegisterInfo(beanInfo.getReferID(), beanInfo.getType());
        return (T) this.getBeanBuilder().getInstance(targetInfo);
    };
    /**创建Bean。*/
    public <T> T getInstance(Class<T> oriType) {
        /* 1.由于同一个Type可能会有多个注册，每个注册可能会映射了不同的实现，例如:
         *     String -> "HelloWord"   > name = "Hi"
         *     String -> "Say goodBy." > name = "By"
         *     String -> "Body .."     > name = null  (匿名的)
         * 因此查找那个没有名字的Type，倘若存在匿名的Type，返回它，否则返回null。*/
        RegisterInfo<T> info = this.findRegisterInfo(null, oriType);
        if (info == null)
            info = new FreeTypeRegister<T>(oriType);
        return this.getBeanBuilder().getInstance(info);
    };
    /**获取用于创建Bean对象的BeanBuilder接口*/
    protected BeanBuilder getBeanBuilder() {
        return this.getRegisterManager().getBeanBuilder();
    };
    //
    /*------------------------------------------------------------------------------------Binding*/
    public <T> T findBindingBean(String withName, Class<T> bindingType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindingType, "bindingType is null.");
        //
        List<RegisterInfo<T>> targetRegisterList = this.findRegisterInfo(bindingType);
        /*找到那个RegisterInfo*/
        for (RegisterInfo<T> info : targetRegisterList) {
            String bindName = info.getName();
            boolean nameTest = withName.equals(bindName);
            if (nameTest)
                return info.getProvider().get();
        }
        return null;
    }
    public <T> Provider<T> findBindingProvider(String withName, Class<T> bindingType) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindingType, "bindingType is null.");
        //F
        List<RegisterInfo<T>> targetRegisterList = this.findRegisterInfo(bindingType);
        /*找到那个RegisterInfo*/
        for (RegisterInfo<T> info : targetRegisterList) {
            String bindName = info.getName();
            boolean nameTest = withName.equals(bindName);
            if (nameTest)
                return info.getProvider();
        }
        return null;
    }
    public <T> List<T> findBindingBean(Class<T> bindingType) {
        List<RegisterInfo<T>> targetInfoList = this.findRegisterInfo(bindingType);
        /*将RegisterInfo<T>列表转换为<T>列表*/
        List<T> targetList = new ArrayList<T>();
        for (RegisterInfo<T> info : targetInfoList) {
            Provider<T> target = info.getProvider();
            targetList.add(target.get());
        }
        return targetList;
    }
    public <T> List<Provider<T>> findBindingProvider(Class<T> bindingType) {
        List<RegisterInfo<T>> targetInfoList = this.findRegisterInfo(bindingType);
        /*将RegisterInfo<T>列表转换为Provider<T>列表*/
        List<Provider<T>> targetList = new ArrayList<Provider<T>>();
        for (RegisterInfo<T> info : targetInfoList) {
            Provider<T> target = info.getProvider();
            targetList.add(target);
        }
        return targetList;
    }
    public RegisterScope getParentScope() {
        return this.getParent();
    }
    public final Iterator<RegisterInfo<?>> getRegisterIterator() {
        Iterator<RegisterInfo<?>> registerIterator = this.localRegisterIterator();
        RegisterScope scope = this.getParentScope();
        if (scope != null) {
            Iterator<RegisterInfo<?>> parentIterator = scope.getRegisterIterator();
            registerIterator = MergeUtils.mergeIterator(registerIterator, parentIterator);
        }
        return registerIterator;
    }
    /**查找RegisterInfo*/
    public <T> List<RegisterInfo<T>> findRegisterInfo(Class<T> bindType) {
        Iterator<RegisterInfo<T>> infoIterator = this.localRegisterIterator(bindType);
        if (infoIterator == null || infoIterator.hasNext() == false)
            return Collections.emptyList();
        //
        List<RegisterInfo<T>> infoList = new ArrayList<RegisterInfo<T>>();
        while (infoIterator.hasNext())
            infoList.add(infoIterator.next());
        return infoList;
    }
    /**查找RegisterInfo*/
    public <T> RegisterInfo<T> findRegisterInfo(String withName, Class<T> bindingType) {
        List<RegisterInfo<T>> infoList = this.findRegisterInfo(bindingType);
        for (RegisterInfo<T> info : infoList) {
            if (StringUtils.equals(withName, info.getName()))
                return info;
        }
        return null;
    }
    /**注册一个类型*/
    protected <T> TypeRegister<T> registerType(Class<T> type) {
        return this.getRegisterManager().registerType(type);
    }
    /**已注册的类型列表。*/
    protected Iterator<RegisterInfo<?>> localRegisterIterator() {
        return this.getBeanBuilder().getRegisterIterator();
    }
    /**已注册的类型列表。*/
    protected <T> Iterator<RegisterInfo<T>> localRegisterIterator(Class<T> type) {
        return this.getBeanBuilder().getRegisterIterator(type);
    }
    //
    /*--------------------------------------------------------------------------------------Event*/
    public void pushListener(String eventType, EventListener eventListener) {
        this.getEnvironment().pushListener(eventType, eventListener);
    }
    public void addListener(String eventType, EventListener eventListener) {
        this.getEnvironment().addListener(eventType, eventListener);
    }
    public void removeListener(String eventType, EventListener eventListener) {
        this.getEnvironment().removeListener(eventType, eventListener);
    }
    public void fireSyncEvent(String eventType, Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, objects);
    }
    public void fireSyncEvent(String eventType, EventCallBackHook callBack, Object... objects) {
        this.getEnvironment().fireSyncEvent(eventType, callBack, objects);
    }
    public void fireAsyncEvent(String eventType, Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, objects);
    }
    public void fireAsyncEvent(String eventType, EventCallBackHook callBack, Object... objects) {
        this.getEnvironment().fireAsyncEvent(eventType, callBack, objects);
    }
    //
    /*------------------------------------------------------------------------------------Context*/
    private AbstractAppContext parent;
    private Object             context;
    /**获取上下文*/
    public AbstractAppContext getParent() {
        return this.parent;
    }
    /**获取上下文*/
    public Object getContext() {
        return this.context;
    }
    /**设置上下文*/
    public void setContext(Object context) {
        this.context = context;
    }
    /**获取应用程序配置。*/
    public Settings getSettings() {
        return this.getEnvironment().getSettings();
    };
    private Environment environment;
    /**获取环境接口。*/
    public Environment getEnvironment() {
        if (this.environment == null)
            this.environment = this.createEnvironment();
        return this.environment;
    }
    /**创建环境对象*/
    protected abstract Environment createEnvironment();
    /**获取RegisterContext对象*/
    protected abstract RegisterManager getRegisterManager();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType) {
        return this.getEnvironment().findClass(featureType);
    }
    //
    /*------------------------------------------------------------------------------------Process*/
    /**开始进入初始化过程.*/
    protected void doInitialize() {
        RegisterManager regContext = this.getRegisterManager();
        if (regContext instanceof ContextInitializeListener)
            ((ContextInitializeListener) regContext).doInitialize(this);
    }
    /**初始化过程完成.*/
    protected void doInitializeCompleted() {
        RegisterManager regContext = this.getRegisterManager();
        if (regContext instanceof ContextInitializeListener)
            ((ContextInitializeListener) regContext).doInitializeCompleted(this);
    }
    /**开始进入容器启动过程.*/
    protected void doStart() {
        RegisterManager regContext = this.getRegisterManager();
        if (regContext instanceof ContextStartListener)
            ((ContextStartListener) regContext).doStart(this);
    }
    /**容器启动完成*/
    protected void doStartCompleted() {
        RegisterManager regContext = this.getRegisterManager();
        if (regContext instanceof ContextStartListener)
            ((ContextStartListener) regContext).doStartCompleted(this);
    }
    //
    //
    /*--------------------------------------------------------------------------------------Utils*/
    /**为模块创建ApiBinder*/
    protected ApiBinder newApiBinder(final Plugin forModule) {
        return new AbstractBinder(this.getEnvironment()) {
            protected <T> TypeRegister<T> registerType(Class<T> type) {
                return AbstractAppContext.this.registerType(type);
            }
        };
    }
    /**当完成所有初始化过程之后调用，负责向 Context 绑定一些预先定义的类型。*/
    protected void doBind(ApiBinder apiBinder) {
        final AbstractAppContext appContet = this;
        /*绑定Environment对象的Provider*/
        apiBinder.bindingType(Environment.class).toProvider(new Provider<Environment>() {
            public Environment get() {
                return appContet.getEnvironment();
            }
        });
        /*绑定Settings对象的Provider*/
        apiBinder.bindingType(Settings.class).toProvider(new Provider<Settings>() {
            public Settings get() {
                return appContet.getSettings();
            }
        });
        /*绑定AppContext对象的Provider*/
        apiBinder.bindingType(AppContext.class).toProvider(new Provider<AppContext>() {
            public AppContext get() {
                return appContet;
            }
        });
    }
    //
    //
    /*------------------------------------------------------------------------------------Creater*/
    synchronized final void installPlugin(Plugin... plugins) throws Throwable {
        /*1.Init*/
        Hasor.logInfo("send init sign...");
        this.doInitialize();
        /*2.Bind*/
        AbstractBinder apiBinder = new AbstractBinder(this.getEnvironment()) {
            protected <T> TypeRegister<T> registerType(Class<T> type) {
                return AbstractAppContext.this.registerType(type);
            }
        };
        for (Plugin plugin : plugins)
            plugin.loadPlugin(apiBinder);
        this.doBind(apiBinder);
        /*3.引发事件*/
        this.fireSyncEvent(ContextEvent_Initialized, apiBinder);
        this.doInitializeCompleted();
        Hasor.logInfo("the init is completed!");
        //
        /*3.Start*/
        Hasor.logInfo("send start sign...");
        this.doStart();
        /*2.执行Aware通知*/
        List<AppContextAware> awareList = this.findBindingBean(AppContextAware.class);
        if (awareList.isEmpty() == false) {
            for (AppContextAware weak : awareList)
                weak.setAppContext(this);
        }
        /*3.发送启动事件*/
        this.fireSyncEvent(ContextEvent_Started, this);
        this.doStartCompleted();/*用于扩展*/
        /*3.打印状态*/
        Hasor.logInfo("Hasor Started now!");
    }
}