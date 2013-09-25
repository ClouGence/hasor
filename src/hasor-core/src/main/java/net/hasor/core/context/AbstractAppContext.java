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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventManager;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import net.hasor.core.Settings;
import net.hasor.core.binder.ApiBinderModule;
import net.hasor.core.binder.BeanInfo;
import net.hasor.core.module.AbstractModulePropxy;
import net.hasor.core.module.ModuleReactor;
import org.more.UndefinedException;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
/**
 * {@link AppContext}接口的抽象实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractAppContext implements AppContext {
    private Object context;
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
    /**获取事件操作接口。*/
    public EventManager getEventManager() {
        return this.getEnvironment().getEventManager();
    }
    //
    //
    //-----------------------------------------------------------------------------------------Bean
    private Map<String, BeanInfo> beanInfoMap;
    private Injector              injector = null;
    /**通过名获取Bean的类型。*/
    public <T> Class<T> getBeanType(String name) {
        Hasor.assertIsNotNull(name, "bean name is null.");
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        BeanInfo info = this.beanInfoMap.get(name);
        if (info != null)
            return (Class<T>) info.getBeanType();
        throw null;
    }
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        return this.getEnvironment().getClassSet(featureType);
    }
    /**如果存在目标类型的Bean则返回Bean的名称。*/
    public String getBeanName(Class<?> targetClass) {
        Hasor.assertIsNotNull(targetClass, "targetClass is null.");
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        for (Entry<String, BeanInfo> ent : this.beanInfoMap.entrySet()) {
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
    //
    public BeanInfo getBeanInfo(String name) {
        if (this.beanInfoMap == null)
            this.collectBeanInfos();
        return this.beanInfoMap.get(name);
    }
    //
    private void collectBeanInfos() {
        this.beanInfoMap = new HashMap<String, BeanInfo>();
        Provider<BeanInfo>[] beanInfoProviderArray = this.getProviderByBindingType(BeanInfo.class);
        if (beanInfoProviderArray == null)
            return;
        for (Provider<BeanInfo> entry : beanInfoProviderArray) {
            BeanInfo beanInfo = entry.get();
            this.beanInfoMap.put(beanInfo.getName(), beanInfo);
        }
    }
    /**通过名称创建bean实例，使用guice，如果获取的bean不存在则会引发{@link UndefinedException}类型异常。*/
    public <T> T getBean(String name) {
        BeanInfo beanInfo = this.getBeanInfo(name);
        if (beanInfo == null)
            throw new UndefinedException("bean ‘" + name + "’ is undefined.");
        return (T) this.getGuice().getInstance(beanInfo.getBeanType());
    };
    /**获得Guice环境。*/
    public Injector getGuice() {
        return this.injector;
    }
    //
    //
    //----------------------------------------------------------------------------------Core Method
    /**通过类型创建该类实例，使用guice*/
    public <T> T getInstance(Class<T> beanType) {
        return this.getGuice().getInstance(beanType);
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> T[] getInstanceByBindingType(Class<T> bindingType) {
        ArrayList<T> providerList = new ArrayList<T>();
        TypeLiteral<T> BindingType_DEFS = TypeLiteral.get(bindingType);
        for (Binding<T> entry : this.getGuice().findBindingsByType(BindingType_DEFS)) {
            Provider<T> bindingTypeProvider = entry.getProvider();
            providerList.add(bindingTypeProvider.get());
        }
        //
        if (providerList.isEmpty())
            return null;
        return (T[]) providerList.toArray();
    }
    /**通过一个类型获取所有绑定到该类型的上的对象实例。*/
    public <T> Provider<T>[] getProviderByBindingType(Class<T> bindingType) {
        ArrayList<Provider<T>> providerList = new ArrayList<Provider<T>>();
        TypeLiteral<T> BindingType_DEFS = TypeLiteral.get(bindingType);
        for (Binding<T> entry : this.getGuice().findBindingsByType(BindingType_DEFS)) {
            Provider<T> bindingTypeProvider = entry.getProvider();
            providerList.add(bindingTypeProvider);
        }
        //
        if (providerList.isEmpty())
            return null;
        return providerList.toArray(new Provider[providerList.size()]);
    }
    //
    //
    //---------------------------------------------------------------------------------------Module
    /**选取或者生成指定模块的代理类型。*/
    protected AbstractModulePropxy generateModulePropxy(Module hasorModule) {
        for (AbstractModulePropxy info : this.getModulePropxyList())
            if (info.getTarget() == hasorModule)
                return info;
        return new ContextModulePropxy(hasorModule, this);
    }
    /**添加模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized ModuleInfo addModule(Module hasorModule) {
        if (this.isReady())
            throw new IllegalStateException("context is inited.");
        AbstractModulePropxy propxy = this.generateModulePropxy(hasorModule);
        List<AbstractModulePropxy> propxyList = this.getModulePropxyList();
        if (propxyList.contains(propxy) == false)
            propxyList.add(propxy);
        return propxy;
    }
    /**删除模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized boolean removeModule(Module hasorModule) {
        if (this.isReady())
            throw new IllegalStateException("context is inited.");
        AbstractModulePropxy targetInfo = null;
        for (AbstractModulePropxy info : this.getModulePropxyList())
            if (info.getTarget() == hasorModule) {
                targetInfo = info;
                break;
            }
        if (targetInfo != null) {
            this.getModulePropxyList().remove(targetInfo);
            return true;
        }
        return false;
    }
    /**获得所有模块*/
    public ModuleInfo[] getModules() {
        List<AbstractModulePropxy> haosrModuleList = this.getModulePropxyList();
        ModuleInfo[] infoArray = new ModuleInfo[haosrModuleList.size()];
        for (int i = 0; i < haosrModuleList.size(); i++)
            infoArray[i] = haosrModuleList.get(i);
        return infoArray;
    }
    //
    private List<AbstractModulePropxy> haosrModuleSet;
    /**创建或者获得用于存放所有ModuleInfo的集合对象*/
    protected List<AbstractModulePropxy> getModulePropxyList() {
        if (this.haosrModuleSet == null)
            this.haosrModuleSet = new ArrayList<AbstractModulePropxy>();
        return haosrModuleSet;
    }
    //
    /**为模块创建ApiBinder*/
    protected ApiBinderModule newApiBinder(final AbstractModulePropxy forModule, final Binder binder) {
        return new ApiBinderModule(this.getEnvironment(), forModule) {
            public Binder getGuiceBinder() {
                return binder;
            }
            public DependencySettings dependency() {
                return forModule;
            }
        };
    }
    /**通过guice创建{@link Injector}，该方法会促使调用模块init生命周期*/
    protected Injector createInjector(com.google.inject.Module[] guiceModules) {
        ArrayList<com.google.inject.Module> guiceModuleSet = new ArrayList<com.google.inject.Module>();
        guiceModuleSet.add(new MasterModule(this));
        if (guiceModules != null)
            for (com.google.inject.Module mod : guiceModules)
                guiceModuleSet.add(mod);
        return Guice.createInjector(guiceModuleSet.toArray(new com.google.inject.Module[guiceModuleSet.size()]));
    }
    /**打印模块状态*/
    protected void printModState() {
        List<AbstractModulePropxy> modList = this.getModulePropxyList();
        StringBuilder sb = new StringBuilder("");
        int size = String.valueOf(modList.size() - 1).length();
        for (int i = 0; i < modList.size(); i++) {
            ModuleInfo info = modList.get(i);
            sb.append(String.format("%0" + size + "d", i));
            sb.append('.');
            sb.append("-->[");
            //Running:运行中(start)、Initial:准备失败、Stopped:停止(stop)
            sb.append(!info.isReady() ? "Initial" : info.isStart() ? "Running" : "Stopped");
            sb.append("] ");
            sb.append(info.getDisplayName());
            sb.append(" (");
            sb.append(info.getTarget().getClass());
            sb.append(")\n");
        }
        if (sb.length() > 1)
            sb.deleteCharAt(sb.length() - 1);
        Hasor.info("Modules State List:\n%s", sb);
    }
    //
    //
    //---------------------------------------------------------------------------------------Module
    private boolean     isStart;
    private Environment environment;
    //
    /**获取环境接口。*/
    public Environment getEnvironment() {
        if (this.environment == null)
            this.environment = this.createEnvironment();
        return this.environment;
    }
    /**创建环境对象*/
    protected abstract Environment createEnvironment();
    /**判断容器是否处于运行状态*/
    public boolean isStart() {
        return this.isStart;
    }
    /**初始化容器，请注意容器只能被初始化一次。*/
    protected void initContext() {
        if (this.injector != null)
            return;
        /*触发ContextEvent_Init事件，并且创建创建guice*/
        Hasor.info("send init sign.");
        this.getEnvironment().getEventManager().doSyncEventIgnoreThrow(ContextEvent_Init, this);
        this.injector = this.createInjector(null);
        Hasor.assertIsNotNull(this.injector, "can not be create Injector.");
        /*使用反应堆对模块进行循环检查和排序*/
        List<ModuleInfo> readOnlyModules = new ArrayList<ModuleInfo>();
        for (AbstractModulePropxy amp : this.getModulePropxyList())
            readOnlyModules.add(amp);
        //
        ModuleReactor reactor = new ModuleReactor(readOnlyModules);
        List<ModuleInfo> result = reactor.process();
        List<AbstractModulePropxy> propxyList = this.getModulePropxyList();
        propxyList.clear();
        for (ModuleInfo info : result)
            propxyList.add((AbstractModulePropxy) info);
        // 
        Hasor.info("the init is completed!");
    }
    /**启动。向所有模块发送启动信号，并将容器的状态置为Start。（该方法会尝试init所有模块）*/
    public synchronized void start() {
        if (this.isStart == true)
            return;
        this.initContext();
        Hasor.info("send start sign.");
        this.getEnvironment().getEventManager().doSyncEventIgnoreThrow(ContextEvent_Start, this);
        List<AbstractModulePropxy> modulePropxyList = this.getModulePropxyList();
        for (AbstractModulePropxy mod : modulePropxyList)
            mod.start(this);
        this.isStart = true;
        /*打印模块状态*/
        this.printModState();
        Hasor.info("hasor started!");
    }
    /**停止。向所有模块发送停止信号，并将容器的状态置为Stop。*/
    public synchronized void stop() {
        if (this.isStart == false)
            return;
        Hasor.info("send stop sign.");
        this.getEnvironment().getEventManager().doSyncEventIgnoreThrow(ContextEvent_Stop, this);
        List<AbstractModulePropxy> modulePropxyList = this.getModulePropxyList();
        for (AbstractModulePropxy mod : modulePropxyList)
            mod.stop(this);
        this.isStart = false;
        /*打印模块状态*/
        this.printModState();
        Hasor.info("hasor stoped!");
    }
    /**重新初始化并启动容器，强迫所有模块都重新初始化(Init)并启动(Start)*/
    public synchronized void reboot() {
        this.stop();
        this.getEnvironment().release();
        this.environment = null;
        this.injector = null;
        this.beanInfoMap = null;
        this.haosrModuleSet = null;
        this.start();
    }
    /**调用停止执行，而后调用start指令。*/
    public synchronized void restart() {
        this.stop();
        this.start();
    }
}
/**该类负责处理模块在Guice.configure期间的初始化任务。*/
class MasterModule implements com.google.inject.Module {
    private AbstractAppContext appContet;
    public MasterModule(AbstractAppContext appContet) {
        this.appContet = appContet;
    }
    public void configure(Binder binder) {
        Hasor.info("send init sign...");
        List<AbstractModulePropxy> modulePropxyList = this.appContet.getModulePropxyList();
        /*引发模块init生命周期*/
        for (AbstractModulePropxy forModule : modulePropxyList) {
            ApiBinderModule apiBinder = this.appContet.newApiBinder(forModule, binder);
            forModule.init(apiBinder);//触发生命周期 
            apiBinder.configure(binder);
        }
        Hasor.info("init modules finish.");
        ExtBind.doBind(binder, appContet);
    }
}
/***/
class ContextModulePropxy extends AbstractModulePropxy {
    public ContextModulePropxy(Module targetModule, AbstractAppContext appContext) {
        super(targetModule, appContext);
    }
    protected AbstractModulePropxy getInfo(Class<? extends Module> targetModule, AppContext appContext) {
        List<AbstractModulePropxy> modulePropxyList = ((AbstractAppContext) appContext).getModulePropxyList();
        for (AbstractModulePropxy modulePropxy : modulePropxyList)
            if (targetModule == modulePropxy.getTarget().getClass())
                return modulePropxy;
        throw new UndefinedException(targetModule.getName() + " module is Undefined!");
    }
}
/**绑定*/
class ExtBind {
    public static void doBind(final Binder binder, final AppContext appContet) {
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
}