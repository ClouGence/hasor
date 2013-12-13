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
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.Environment;
import net.hasor.core.EventManager;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import net.hasor.core.Settings;
import net.hasor.core.binder.AbstractApiBinder;
import net.hasor.core.binder.BeanMetaData;
import net.hasor.core.module.ModulePropxy;
import net.hasor.core.module.ModuleReactor;
import org.more.UndefinedException;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
/**
 * 抽象类 AbstractAppContext 是 {@link AppContext} 接口的基础实现。
 * <p>它包装了大量细节代码，可以方便的通过子类来创建独特的上下文支持。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractAppContext implements AppContext {
    //
    //-----------------------------------------------------------------------------------------Bean
    private Map<String, BeanMetaData> beanInfoMap;
    private void collectBeanInfos() {
        this.beanInfoMap = new HashMap<String, BeanMetaData>();
        List<Provider<BeanMetaData>> beanInfoProviderArray = this.findProviderByType(BeanMetaData.class);
        if (beanInfoProviderArray == null)
            return;
        for (Provider<BeanMetaData> entry : beanInfoProviderArray) {
            BeanMetaData beanMetaData = entry.get();
            this.beanInfoMap.put(beanMetaData.getName(), beanMetaData);
        }
    }
    /**通过名获取Bean的类型。*/
    public <T> Class<T> getBeanType(String name) {
        Hasor.assertIsNotNull(name, "bean name is null.");
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        BeanMetaData info = this.beanInfoMap.get(name);
        if (info != null)
            return (Class<T>) info.getBeanType();
        throw null;
    }
    /**如果存在目标类型的Bean则返回Bean的名称。*/
    public String getBeanName(Class<?> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        for (Entry<String, BeanMetaData> ent : this.beanInfoMap.entrySet()) {
            if (ent.getValue().getBeanType() == targetClass)
                return ent.getKey();
        }
        return null;
    }
    /**获取已经注册的Bean名称。*/
    public String[] getBeanNames() {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        return this.beanInfoMap.keySet().toArray(new String[this.beanInfoMap.size()]);
    }
    /**通过名称创建bean实例，使用guice，如果获取的bean不存在则会引发{@link UndefinedException}类型异常。*/
    public <T> T getBean(String name) {
        BeanMetaData beanMetaData = this.getBeanInfo(name);
        if (beanMetaData == null)
            throw new UndefinedException("bean ‘" + name + "’ is undefined.");
        return (T) this.getGuice().getInstance(beanMetaData.getBeanType());
    };
    /**通过类型创建该类实例，使用guice*/
    public <T> T getInstance(Class<T> beanType) {
        return this.getGuice().getInstance(beanType);
    }
    /**获取 Bean 的描述接口。*/
    public BeanMetaData getBeanInfo(String name) {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        return this.beanInfoMap.get(name);
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<T> findBeanByType(Class<T> bindingType) {
        ArrayList<T> providerList = new ArrayList<T>();
        TypeLiteral<T> BindingType_DEFS = TypeLiteral.get(bindingType);
        for (Binding<T> entry : this.getGuice().findBindingsByType(BindingType_DEFS)) {
            Provider<T> bindingTypeProvider = entry.getProvider();
            providerList.add(bindingTypeProvider.get());
        }
        //
        if (providerList.isEmpty())
            return null;
        return providerList;
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> List<Provider<T>> findProviderByType(Class<T> bindingType) {
        ArrayList<Provider<T>> providerList = new ArrayList<Provider<T>>();
        TypeLiteral<T> BindingType_DEFS = TypeLiteral.get(bindingType);
        for (Binding<T> entry : this.getGuice().findBindingsByType(BindingType_DEFS)) {
            Provider<T> bindingTypeProvider = entry.getProvider();
            providerList.add(bindingTypeProvider);
        }
        //
        if (providerList.isEmpty())
            return null;
        return providerList;
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> T findBeanByType(String withName, Class<T> bindingType) {
        Provider<T> provider = findProviderByType(withName, bindingType);
        if (provider == null)
            return null;
        return provider.get();
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> Provider<T> findProviderByType(String withName, Class<T> bindingType) {
        TypeLiteral<T> BindingType_DEFS = TypeLiteral.get(bindingType);
        Named named = Names.named(withName);
        //
        for (Binding<T> entry : this.getGuice().findBindingsByType(BindingType_DEFS)) {
            Provider<T> bindingTypeProvider = entry.getProvider();
            Annotation nameAnno = entry.getKey().getAnnotation();
            if (!named.equals(nameAnno))/*参见 Names.named。*/
                continue;
            return bindingTypeProvider;
        }
        return null;
    }
    //
    //--------------------------------------------------------------------------------------Context
    private Injector injector = null;
    private Object   context;
    /**获得Guice环境。*/
    public Injector getGuice() {
        return this.injector;
    }
    /**获取上下文*/
    public Object getContext() {
        return this.context;
    }
    /**设置上下文*/
    public void setContext(Object context) {
        this.context = context;
    }
    /**获取系统启动时间*/
    public long getStartTime() {
        return this.getEnvironment().getStartTime();
    };
    /**表示AppContext是否准备好。*/
    public boolean isReady() {
        return this.getGuice() != null;
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
    /**获取事件操作接口。*/
    public EventManager getEventManager() {
        return this.getEnvironment().getEventManager();
    }
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        return this.getEnvironment().getClassSet(featureType);
    }
    //---------------------------------------------------------------------------------------Module
    private List<ModulePropxy> moduleSet;
    /**创建或者获得用于存放所有ModuleInfo的集合对象*/
    protected List<ModulePropxy> getModuleList() {
        if (this.moduleSet == null)
            this.moduleSet = new ArrayList<ModulePropxy>();
        return moduleSet;
    }
    /**添加模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized ModuleInfo addModule(Module hasorModule) {
        if (this.isReady())
            throw new IllegalStateException("context is inited.");
        /*防止重复添加*/
        for (ModulePropxy info : this.getModuleList())
            if (info.getTarget() == hasorModule)
                return info;
        /*添加模块*/
        ModulePropxy propxy = new ContextModulePropxy(hasorModule, this);
        List<ModulePropxy> propxyList = this.getModuleList();
        if (propxyList.contains(propxy) == false)
            propxyList.add(propxy);
        return propxy;
    }
    /**删除模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized boolean removeModule(Module hasorModule) {
        if (this.isReady())
            throw new IllegalStateException("context is inited.");
        ModulePropxy targetInfo = null;
        for (ModulePropxy info : this.getModuleList())
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
        List<ModulePropxy> haosrModuleList = this.getModuleList();
        ModuleInfo[] infoArray = new ModuleInfo[haosrModuleList.size()];
        for (int i = 0; i < haosrModuleList.size(); i++)
            infoArray[i] = haosrModuleList.get(i);
        return infoArray;
    }
    //----------------------------------------------------------------------------------------Utils
    private boolean isStart;
    /**判断容器是否处于运行状态*/
    public boolean isStart() {
        return this.isStart;
    }
    /**为模块创建ApiBinder*/
    protected AbstractApiBinder newApiBinder(final ModulePropxy forModule, Binder binder) {
        return new AbstractApiBinder(binder, this.getEnvironment(), forModule) {
            public DependencySettings dependency() {
                return forModule;
            }
        };
    }
    /**通过guice创建{@link Injector}，该方法会促使调用模块init生命周期*/
    protected Injector createInjector(com.google.inject.Module[] guiceModules) {
        return Guice.createInjector(guiceModules);
    }
    /**打印模块状态*/
    protected static void printModState(AbstractAppContext appContext) {
        List<ModulePropxy> modList = appContext.getModuleList();
        StringBuilder sb = new StringBuilder("");
        int size = String.valueOf(modList.size() - 1).length();
        for (int i = 0; i < modList.size(); i++) {
            ModuleInfo info = modList.get(i);
            sb.append(String.format("%0" + size + "d", i));
            sb.append('.');
            sb.append("-->[");
            //Running:运行中(start)、Failed:准备失败、Stopped:停止(stop)
            sb.append(!info.isReady() ? "Failed " : info.isStart() ? "Running" : "Stopped");
            sb.append("] ");
            sb.append(info.getDisplayName());
            sb.append(" (");
            sb.append(info.getTarget().getClass());
            sb.append(")\n");
        }
        if (sb.length() > 1)
            sb.deleteCharAt(sb.length() - 1);
        Hasor.logInfo("Modules State List:\n%s", sb);
    }
    //-----------------------------------------------------------------------------------------Life
    /**初始化容器，请注意容器只能被初始化一次。该方法在创建 Guice 过程中会引发 doInitialize 方法的调用。*/
    protected void initContext() {
        if (this.injector != null)
            return;
        /*1.创建创建guice*/
        Hasor.logInfo("createInjector...");
        this.injector = this.createInjector(new com.google.inject.Module[] { new RootInitializeModule(this) });
        Hasor.assertIsNotNull(this.injector, "can not be create Injector.");
        /*2.使用反应堆对模块进行循环检查和排序*/
        this.doReactor();
        /*3.完成init*/
        Hasor.logInfo("the init is completed!");
    }
    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    public synchronized void start() {
        if (this.isStart() == true)
            return;
        /*1.初始化*/
        this.initContext();
        /*2.启动*/
        this.doStart();
    }
    /**停止。向所有模块发送停止信号，并将容器的状态置为Stop。*/
    public synchronized void stop() {
        if (this.isStart() == false)
            return;
        doStop();
    }
    /**重新初始化并启动容器，强迫所有模块都重新初始化(Init)并启动(Start)*/
    public synchronized void reboot() {
        this.stop();
        this.getEnvironment().release();
        this.environment = null;
        this.injector = null;
        this.beanInfoMap = null;
        this.moduleSet.clear();
        this.start();
    }
    /**调用停止执行，而后调用start指令。*/
    public synchronized void restart() {
        this.stop();
        this.start();
    }
    //--------------------------------------------------------------------------------------Process
    /**执行 Initialize 过程。*/
    protected void doInitialize(Binder binder) {
        Hasor.logInfo("send init sign...");
        this.getEventManager().doSyncEventIgnoreThrow(ContextEvent_Initialize, getEnvironment(), binder);
        //
        List<ModulePropxy> modulePropxyList = this.getModuleList();
        /*引发模块init生命周期*/
        for (ModulePropxy forModule : modulePropxyList) {
            AbstractApiBinder apiBinder = this.newApiBinder(forModule, binder);
            forModule.init(apiBinder);//触发生命周期 
            apiBinder.configure(binder);
        }
        this.doBind(binder);
        //
        this.getEventManager().doSyncEventIgnoreThrow(ContextEvent_Initialized, getEnvironment(), binder);
        Hasor.logInfo("init modules finish.");
    }
    /**使用反应堆对模块进行循环检查和排序*/
    private void doReactor() {
        List<ModuleInfo> readOnlyModules = new ArrayList<ModuleInfo>();
        for (ModulePropxy amp : this.getModuleList())
            readOnlyModules.add(amp);
        ModuleReactor reactor = new ModuleReactor(readOnlyModules);//创建反应器
        List<ModuleInfo> result = reactor.process();
        List<ModulePropxy> propxyList = this.getModuleList();
        propxyList.clear();
        for (ModuleInfo info : result)
            propxyList.add((ModulePropxy) info);
    }
    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    protected void doStart() {
        Hasor.logInfo("send start sign.");
        /*1.执行Aware通知*/
        List<AppContextAware> awareList = this.findBeanByType(AppContextAware.class);
        if (awareList == null)
            return;
        for (AppContextAware weak : awareList)
            weak.setAppContext(this);
        /*2.发送启动事件*/
        this.getEnvironment().getEventManager().doSyncEventIgnoreThrow(ContextEvent_Start, this);
        List<ModulePropxy> modulePropxyList = this.getModuleList();
        for (ModulePropxy mod : modulePropxyList)
            mod.start(this);
        this.isStart = true;
        /*3.打印模块状态*/
        printModState(this);
        Hasor.logInfo("hasor started!");
    }
    /**执行 Stop 过程。*/
    protected void doStop() {
        /*1.发送停止事件*/
        Hasor.logInfo("send stop sign.");
        List<ModulePropxy> modulePropxyList = this.getModuleList();
        for (ModulePropxy mod : modulePropxyList)
            mod.stop(this);
        this.getEnvironment().getEventManager().doSyncEventIgnoreThrow(ContextEvent_Stoped, this);
        this.isStart = false;
        /*2.打印模块状态*/
        printModState(this);
        Hasor.logInfo("hasor stoped!");
    }
    /**当完成所有初始化过程之后调用，负责向 Guice 绑定一些预先定义的类型。*/
    protected void doBind(Binder binder) {
        final AbstractAppContext appContet = this;
        /*绑定Environment对象的Provider*/
        binder.bind(Environment.class).toProvider(new Provider<Environment>() {
            public Environment get() {
                return appContet.getEnvironment();
            }
        });
        /*绑定EventManager对象的Provider*/
        binder.bind(EventManager.class).toProvider(new Provider<EventManager>() {
            public EventManager get() {
                return appContet.getEnvironment().getEventManager();
            }
        });
        /*绑定Settings对象的Provider*/
        binder.bind(Settings.class).toProvider(new Provider<Settings>() {
            public Settings get() {
                return appContet.getSettings();
            }
        });
        /*绑定AppContext对象的Provider*/
        binder.bind(AppContext.class).toProvider(new Provider<AppContext>() {
            public AppContext get() {
                return appContet;
            }
        });
    }
    /**负责处理 Init 阶段的入口类。*/
    private class RootInitializeModule implements com.google.inject.Module {
        private AbstractAppContext appContet;
        public RootInitializeModule(AbstractAppContext appContet) {
            this.appContet = appContet;
        }
        public void configure(Binder binder) {
            this.appContet.doInitialize(binder);
        }
    }
    /**位于容器中 ModulePropxy 抽象类的实现*/
    private class ContextModulePropxy extends ModulePropxy {
        public ContextModulePropxy(Module targetModule, AbstractAppContext appContext) {
            super(targetModule, appContext);
        }
        protected ModulePropxy getInfo(Class<? extends Module> targetModule, AppContext appContext) {
            List<ModulePropxy> modulePropxyList = ((AbstractAppContext) appContext).getModuleList();
            for (ModulePropxy modulePropxy : modulePropxyList)
                if (targetModule == modulePropxy.getTarget().getClass())
                    return modulePropxy;
            throw new UndefinedException(targetModule.getName() + " module is Undefined!");
        }
    }
}