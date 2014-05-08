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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import net.hasor.core.RegisterInfo;
import net.hasor.core.Settings;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.binder.BeanInfo;
import net.hasor.core.binder.TypeRegister;
import net.hasor.core.module.ModuleProxy;
import org.more.UndefinedException;
import org.more.util.ArrayUtils;
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
    public String getBeanName(Class<?> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        List<BeanInfo> infoArray = this.findBindingBean(BeanInfo.class);
        if (infoArray == null || infoArray.isEmpty())
            return null;
        /*查找*/
        for (BeanInfo info : infoArray) {
            if (info.getType().equals(targetClass))
                return info.getName();
        }
        return null;
    }
    public String[] getBeanNames() {
        List<BeanInfo> infoArray = this.findBindingBean(BeanInfo.class);
        if (infoArray == null || infoArray.isEmpty())
            return ArrayUtils.EMPTY_STRING_ARRAY;
        /*查找*/
        List<String> names = new ArrayList<String>();
        for (BeanInfo info : infoArray) {
            names.add(info.getName());
            String[] aliasNames = info.getAliasName();
            if (aliasNames != null)
                for (String aliasName : aliasNames)
                    names.add(aliasName);
        }
        return names.toArray(new String[names.size()]);
    }
    public <T> T getInstance(String name) {
        BeanInfo info = this.findBindingBean(name, BeanInfo.class);
        if (info == null)
            return null;
        Class<?> targetType = info.getType();
        return (T) this.getInstance(targetType);
    }
    /**创建Bean。*/
    public abstract <T> T getInstance(Class<T> oriType);
    /**创建Bean。*/
    public abstract <T> T getBean(String name);
    //
    /*------------------------------------------------------------------------------------Binding*/
    public <T> T findBindingBean(String withName, Class<T> bindingType) {
        return findBindingBean(withName, bindingType, this);
    }
    public <T> Provider<T> findBindingProvider(String withName, Class<T> bindingType) {
        return findBindingProvider(withName, bindingType, this);
    }
    public <T> List<T> findBindingBean(Class<T> bindingType) {
        return findBindingBean(bindingType, this);
    }
    public <T> List<Provider<T>> findBindingProvider(Class<T> bindingType) {
        return findBindingProvider(bindingType, this);
    }
    /**在RegisterScope范围内查找绑定。*/
    protected final <T> T findBindingBean(String withName, Class<T> bindingType, RegisterScope scope) {
        Provider<? extends T> targetProvider = this.findBindingProvider(withName, bindingType, scope);
        return targetProvider != null ? targetProvider.get() : null;
    }
    /**在RegisterScope范围内查找绑定。*/
    protected final <T> Provider<T> findBindingProvider(String withName, Class<T> bindingType, RegisterScope scope) {
        Hasor.assertIsNotNull(withName, "withName is null.");
        Hasor.assertIsNotNull(bindingType, "bindingType is null.");
        Hasor.assertIsNotNull(scope, "scope is null.");
        //
        List<RegisterInfo<T>> targetRegisterList = this.findBindingRegisterInfo(bindingType, scope);
        /*找到那个RegisterInfo*/
        for (RegisterInfo<T> info : targetRegisterList) {
            String bindName = info.getBindName();
            boolean nameTest = withName.equals(bindName);
            if (nameTest)
                return info.getProvider();
        }
        return null;
    }
    /**在RegisterScope范围内查找绑定。*/
    protected final <T> List<T> findBindingBean(Class<T> bindingType, RegisterScope scope) {
        List<T> targetList = new ArrayList<T>();
        List<Provider<T>> targetProviderList = this.findBindingProvider(bindingType, scope);
        /*将Provider<T>列表转换为List<T>*/
        for (Provider<T> pro : targetProviderList) {
            T target = pro.get();
            targetList.add(target);
        }
        return targetList;
    }
    /**在RegisterScope范围内查找绑定。*/
    protected final <T> List<Provider<T>> findBindingProvider(Class<T> bindingType, RegisterScope scope) {
        List<RegisterInfo<T>> targetInfoList = this.findBindingRegisterInfo(bindingType, scope);
        /*将RegisterInfo<T>列表转换为Provider<T>列表*/
        List<Provider<T>> targetList = new ArrayList<Provider<T>>();
        for (RegisterInfo<T> info : targetInfoList) {
            Provider<T> target = info.getProvider();
            targetList.add(target);
        }
        return targetList;
    }
    /**通过一个类型获取所有绑定到该类型的上的RegisterInfo，从该处获取的RegisterInfo必然都是 public的。*/
    protected final <T> List<RegisterInfo<T>> findBindingRegisterInfo(Class<T> bindingType, RegisterScope scope) {
        Hasor.assertIsNotNull(bindingType, "bindingType is null.");
        Hasor.assertIsNotNull(scope, "scope is null.");
        //
        List<RegisterInfo<T>> arrayList = new ArrayList<RegisterInfo<T>>();
        findBindingRegisterInfo(bindingType, scope, arrayList);
        return arrayList;
    };
    /**通过一个类型获取所有绑定到该类型的上的RegisterInfo，从该处获取的RegisterInfo必然都是 public的。*/
    private final <T> void findBindingRegisterInfo(Class<T> bindingType, RegisterScope scope, List<RegisterInfo<T>> toList) {
        Hasor.assertIsNotNull(bindingType, "bindingType is null.");
        Hasor.assertIsNotNull(scope, "scope is null.");
        /*处理Scope本层*/
        Iterator<RegisterInfo<?>> iterator = scope.getRegisterIterator();
        if (iterator != null) {
            while (iterator.hasNext()) {
                RegisterInfo<?> info = iterator.next();
                if (info.getRegisterType() == bindingType)
                    toList.add((RegisterInfo<T>) info);
            }
        }
        /*处理Scope父层*/
        RegisterScope parentScope = scope.getParentScope();
        if (parentScope != null)
            this.findBindingRegisterInfo(bindingType, parentScope, toList);
    };
    public RegisterScope getParentScope() {
        return this.getParent();
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
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType) {
        return this.getEnvironment().findClass(featureType);
    }
    //
    /*-------------------------------------------------------------------------------------Module*/
    private List<ModuleProxy> tempModuleSet;
    /**创建或者获得用于存放所有ModuleInfo的集合对象*/
    private List<ModuleProxy> getModuleList() {
        if (this.tempModuleSet == null)
            this.tempModuleSet = new ArrayList<ModuleProxy>();
        return tempModuleSet;
    }
    /**添加模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized ModuleInfo addModule(Module hasorModule) {
        if (this.isReady())
            throw new IllegalStateException("context is inited.");
        /*防止重复添加*/
        for (ModuleProxy info : this.getModuleList())
            if (info.getTarget() == hasorModule)
                return info;
        /*添加模块*/
        ModuleProxy propxy = new ContextModulePropxy(hasorModule, this);
        List<ModuleProxy> propxyList = this.getModuleList();
        if (propxyList.contains(propxy) == false)
            propxyList.add(propxy);
        return propxy;
    }
    /**删除模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized boolean removeModule(Module hasorModule) {
        if (this.isReady())
            throw new IllegalStateException("context is inited.");
        ModuleProxy targetInfo = null;
        for (ModuleProxy info : this.getModuleList())
            if (info.getTarget() == hasorModule) {
                targetInfo = info;
                break;
            }
        if (targetInfo != null) {
            this.getModuleList().remove(targetInfo);
            return true;
        }
        return false;
    }
    /**获得所有模块*/
    public ModuleInfo[] getModules() {
        if (!this.isReady())
            throw new IllegalStateException("context is not ready.");
        List<ModuleInfo> moduleList = this.findBindingBean(ModuleInfo.class);
        ModuleInfo[] infoArray = new ModuleInfo[moduleList.size()];
        for (int i = 0; i < moduleList.size(); i++)
            infoArray[i] = moduleList.get(i);
        return infoArray;
    }
    /**位于容器中 ModulePropxy 抽象类的实现*/
    private class ContextModulePropxy extends ModuleProxy {
        public ContextModulePropxy(Module targetModule, AbstractAppContext appContext) {
            super(targetModule, appContext);
        }
        protected ModuleProxy getInfo(Class<? extends Module> targetModule, AppContext appContext) {
            List<ModuleProxy> modulePropxyList = ((AbstractAppContext) appContext).getModuleList();
            for (ModuleProxy moduleProxy : modulePropxyList)
                if (targetModule == moduleProxy.getTarget().getClass())
                    return moduleProxy;
            throw new UndefinedException(targetModule.getName() + " module is Undefined!");
        }
    }
    /**初始化过程，注意：apiBinder 参数只能在 init 阶段中使用。*/
    protected void doInit(ApiBinder apiBinder) throws Throwable {
        if (this.tempModuleSet != null) {
            for (ModuleProxy propxy : tempModuleSet) {
                apiBinder.bindingType(ModuleInfo.class).toInstance(propxy);
                //apiBinder.bindingType(ModuleProxy.class).toInstance(propxy);
            }
        }
        this.doBind(apiBinder);
    };
    /**当完成所有初始化过程之后调用，负责向 Guice 绑定一些预先定义的类型。*/
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
    /*--------------------------------------------------------------------------------------Utils*/
    /**注册一个类型*/
    protected abstract <T> TypeRegister<T> registerType(Class<T> type);
    /**为模块创建ApiBinder*/
    protected ApiBinder newApiBinder(final ModuleProxy forModule) {
        return new AbstractBinder(this.getEnvironment()) {
            public ModuleSettings configModule() {
                return forModule;
            }
            protected <T> TypeRegister<T> registerType(Class<T> type) {
                return AbstractAppContext.this.registerType(type);
            }
        };
    }
    /**打印模块状态*/
    protected static void printModState(AbstractAppContext appContext) {
        ModuleInfo[] modArray = appContext.getModules();
        StringBuilder sb = new StringBuilder("");
        int size = String.valueOf(modArray.length).length();
        for (int i = 0; i < modArray.length; i++) {
            ModuleInfo info = modArray[i];
            sb.append(String.format("%0" + size + "d", i));
            sb.append('.');
            sb.append("-->[");
            //Running:运行中(start)、Failed:准备失败、Stopped:停止(stop)
            sb.append(!info.isReady() ? "Failed " : info.isStart() ? "Running" : "Stopped");
            sb.append("] ");
            sb.append(info.getDisplayName());
            sb.append(" (");
            sb.append(info.getDescription());
            sb.append(")\n");
        }
        if (sb.length() > 1)
            sb.deleteCharAt(sb.length() - 1);
        Hasor.logInfo("Modules State List:\n%s", sb);
    }
    //    /**使用反应堆对模块进行循环检查和排序*/
    //    private List<ModuleProxy> doReactor() {
    //        List<ModuleInfo> readOnlyModules = new ArrayList<ModuleInfo>();
    //        for (ModuleProxy amp : this.getModuleList())
    //            readOnlyModules.add(amp);
    //        ModuleReactor reactor = new ModuleReactor(readOnlyModules);//创建反应器
    //        List<ModuleInfo> result = reactor.process();
    //        List<ModuleProxy> propxyList = new ArrayList<ModuleProxy>();
    //        for (ModuleInfo info : result)
    //            propxyList.add((ModuleProxy) info);
    //        return propxyList;
    //    }
    //    
    //    
    //    
    //    
    //    
    //    
    //    
    //    
    //    //-----------------------------------------------------------------------------------------Life
    //    private boolean isStart;
    //    /**判断容器是否处于运行状态*/
    //    public boolean isStart() {
    //        return this.isStart;
    //    }
    //    /**表示AppContext是否准备好。*/
    //    public boolean isReady() {
    //        return this.getGuice() != null;
    //    }
    //    /**初始化容器，请注意容器只能被初始化一次。该方法在创建 Guice 过程中会引发 doInitialize 方法的调用。*/
    //    protected void initContext() {
    //        if (this.injector != null)
    //            return;
    //        /*1.创建创建guice*/
    //        Hasor.logInfo("createInjector...");
    //        this.injector = this.createInjector(new com.google.inject.Module[] { new RootInitializeModule(this) });
    //        Hasor.assertIsNotNull(this.injector, "can not be create Injector.");
    //        /*2.使用反应堆对模块进行循环检查和排序*/
    //        this.doReactor();
    //        /*3.完成init*/
    //        Hasor.logInfo("the init is completed!");
    //    }
    //    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    //    public synchronized void start() {
    //        if (this.isStart() == true)
    //            return;
    //        /*1.初始化*/
    //        this.initContext();
    //        /*2.启动*/
    //        this.doStart();
    //    }
    //    //--------------------------------------------------------------------------------------Process
    //    /**执行 Initialize 过程。*/
    //    protected void doInitialize(final Binder guiceBinder) {
    //        Hasor.logInfo("send init sign...");
    //        List<ModuleProxy> modulePropxyList = this.getModuleList();
    //        /*引发模块init生命周期*/
    //        for (ModuleProxy forModule : modulePropxyList) {
    //            AbstractBinderContext apiBinder = this.newApiBinder(forModule, guiceBinder);
    //            forModule.init(apiBinder);//触发生命周期 
    //            apiBinder.configure(guiceBinder);
    //        }
    //        this.doBind(guiceBinder);
    //        /*引发事件*/
    //        AbstractBinderContext apiBinder = new AbstractBinderContext(this.getEnvironment()) {
    //            public ModuleSettings configModule() {
    //                return null;
    //            }
    //            public Binder getGuiceBinder() {
    //                return guiceBinder;
    //            }
    //        };
    //        this.getEventManager().doSync(ContextEvent_Initialized, apiBinder);
    //        Hasor.logInfo("init modules finish.");
    //    }
    //    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    //    protected void doStart() {
    //        Hasor.logInfo("send start sign.");
    //        /*1.执行Aware通知*/
    //        List<AppContextAware> awareList = this.findBindingBean(AppContextAware.class);
    //        if (awareList != null) {
    //            for (AppContextAware weak : awareList)
    //                weak.setAppContext(this);
    //        }
    //        /*2.逐一启动模块*/
    //        List<ModuleProxy> modulePropxyList = this.getModuleList();
    //        for (ModuleProxy mod : modulePropxyList)
    //            mod.start(this);
    //        this.isStart = true;
    //        /*3.发送启动事件*/
    //        this.getEnvironment().getEventManager().doSync(ContextEvent_Started, this);
    //        /*4.打印模块状态*/
    //        printModState(this);
    //        Hasor.logInfo("hasor started!");
    //    }
}