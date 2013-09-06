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
package net.hasor.context.core;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.hasor.Hasor;
import net.hasor.context.ApiBinder;
import net.hasor.context.AppContext;
import net.hasor.context.Environment;
import net.hasor.context.EventManager;
import net.hasor.context.HasorModule;
import net.hasor.context.InitContext;
import net.hasor.context.ModuleInfo;
import net.hasor.context.ModuleSettings;
import net.hasor.context.Settings;
import net.hasor.context.binder.ApiBinderModule;
import net.hasor.context.reactor.ModuleInfoBean;
import net.hasor.context.reactor.ModuleReactor;
import org.more.util.exception.ExceptionUtils;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
/**
 * {@link AppContext}接口默认实现。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultAppContext extends AbstractAppContext {
    /**容器事件：ContextEvent_Init*/
    public static final String ContextEvent_Init    = "ContextEvent_Init";
    /**容器事件：ContextEvent_Start*/
    public static final String ContextEvent_Start   = "ContextEvent_Start";
    /**容器事件：ContextEvent_Stop*/
    public static final String ContextEvent_Stop    = "ContextEvent_Stop";
    /**容器事件：ContextEvent_Destroy*/
    public static final String ContextEvent_Destroy = "ContextEvent_Destroy";
    //
    private boolean            running;
    private Injector           guice;
    private boolean            forceModule;
    //
    public DefaultAppContext() throws IOException {
        super();
    }
    public DefaultAppContext(String mainConfig) throws IOException {
        super(mainConfig);
    }
    public DefaultAppContext(String mainConfig, Object context) throws IOException {
        super(mainConfig, context);
    }
    protected void initContext() throws IOException {
        super.initContext();
        this.running = false;
        this.guice = null;
        this.forceModule = this.getSettings().getBoolean("hasor.forceModule", false);
    }
    //
    //
    //
    //
    /**添加模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized ModuleInfo addModule(HasorModule hasorModule) {
        if (this.isInit())
            throw new IllegalStateException("context is inited.");
        for (ModuleInfo info : this.getModuleList())
            if (info.getModuleObject() == hasorModule)
                return info;
        ModuleInfo info = new ModuleInfoBean(hasorModule, this);
        this.getModuleList().add(info);
        return info;
    }
    /**删除模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    public synchronized ModuleInfo removeModule(HasorModule hasorModule) {
        if (this.isInit())
            throw new IllegalStateException("context is inited.");
        ModuleInfo targetInfo = null;
        for (ModuleInfo info : this.getModuleList())
            if (info.getModuleObject() == hasorModule) {
                targetInfo = info;
                break;
            }
        if (targetInfo != null)
            this.getModuleList().remove(targetInfo);
        return targetInfo;
    }
    /**获得所有模块*/
    public ModuleInfo[] getModules() {
        List<ModuleInfo> haosrModuleSet = getModuleList();
        return haosrModuleSet.toArray(new ModuleInfo[haosrModuleSet.size()]);
    }
    private List<ModuleInfo> haosrModuleSet;
    /**创建或者获得用于存放所有ModuleInfo的集合对象*/
    protected List<ModuleInfo> getModuleList() {
        if (this.haosrModuleSet == null)
            this.haosrModuleSet = new ArrayList<ModuleInfo>();
        return haosrModuleSet;
    }
    //
    //
    //
    //
    public synchronized void init() {
        if (this.guice != null)
            return;
        /*执行准备过程*/
        Hasor.info("compile startup sequence!");
        List<ModuleInfo> hasorModules = this.getModuleList();
        for (ModuleInfo mod : hasorModules)
            this.onReady(mod);
        /*使用反应堆对模块进行循环检查和排序*/
        List<ModuleInfo> readOnlyModules = Collections.unmodifiableList(hasorModules);
        ModuleReactor reactor = new ModuleReactor(readOnlyModules);
        List<ModuleInfo> result = reactor.process();
        {
            /*更新顺序*/
            List<ModuleInfo> infoList = getModuleList();
            infoList.clear();
            infoList.addAll(result);
        }
        /*创建guice并且触发init过程*/
        this.getEventManager().doSyncEventIgnoreThrow(ContextEvent_Init, (InitContext) this);//发送阶段事件
        this.guice = this.createInjector(null);
        Hasor.assertIsNotNull(this.guice, "can not be create Injector.");
    }
    public synchronized void start() {
        if (this.running == true)
            return;
        /*创建Guice对象，并且引发模块的init事件*/
        if (this.guice == null)
            this.init();
        /*发送完成初始化信号*/
        this.running = true;
        Hasor.info("send start sign.");
        this.getEventManager().doSyncEventIgnoreThrow(ContextEvent_Start, (AppContext) this);//发送阶段事件
        ModuleInfo[] hasorModules = this.getModules();
        for (ModuleInfo mod : hasorModules) {
            if (mod == null)
                continue;
            this.onStart(mod);
        }
        /*打印模块状态*/
        this.printModState();
        Hasor.info("hasor started!");
    }
    public synchronized void stop() {
        if (this.running == false)
            return;
        /*发送停止信号*/
        this.running = false;
        Hasor.info("send stop sign.");
        this.getEventManager().doSyncEventIgnoreThrow(ContextEvent_Stop, (AppContext) this);//发送阶段事件
        this.getEventManager().clean();
        //
        ModuleInfo[] hasorModules = this.getModules();
        for (ModuleInfo mod : hasorModules) {
            if (mod == null)
                continue;
            this.onStop(mod);
        }
        Hasor.info("hasor stoped!");
    }
    public synchronized void destroy() {
        Hasor.info("send destroy sign.");
        this.getEventManager().doSyncEventIgnoreThrow(ContextEvent_Destroy, (AppContext) this);//发送阶段事件
        this.getEventManager().clean();
        this.stop();
        ModuleInfo[] hasorModules = this.getModules();
        for (ModuleInfo mod : hasorModules) {
            if (mod == null)
                continue;
            this.onDestroy(mod);
        }
        Hasor.info("hasor destroy!");
    }
    public boolean isRunning() {
        return this.running;
    }
    public boolean isInit() {
        return this.guice != null;
    }
    //
    //
    //
    //
    private void proForceModule(Throwable e) {
        e = ExceptionUtils.getRootCause(e);
        if (e instanceof RuntimeException)
            throw (RuntimeException) e;
        else if (e instanceof Error)
            throw (Error) e;
        else
            throw new RuntimeException(e);
    }
    /**准备事件*/
    protected void onReady(ModuleInfo forModule) {
        HasorModule modObj = forModule.getModuleObject();
        String eventName = modObj.getClass().getName();
        try {
            modObj.configuration((ModuleSettings) forModule);
            getEventManager().doSyncEventIgnoreThrow(eventName, ModuleInfoBean.Prop_Ready, true);
        } catch (Throwable e) {
            getEventManager().doSyncEventIgnoreThrow(eventName, ModuleInfoBean.Prop_Ready, false, e);
            Hasor.error("%s is not onReady! %s", forModule.getDisplayName(), e.getMessage());
            if (this.forceModule)
                this.proForceModule(e);
        }
    }
    /**模块的 init 生命周期调用。*/
    protected void onInit(ModuleInfo forModule, Binder binder) {
        if (!forModule.isReady())
            return;/*不启动没有准备好的模块*/
        if (forModule.isDependencyInit() == false)
            return;/*依赖的模块尚未准备好*/
        ApiBinder apiBinder = this.newApiBinder(forModule, binder);
        HasorModule modObj = forModule.getModuleObject();
        String eventName = modObj.getClass().getName();
        try {
            modObj.init(apiBinder);
            if (apiBinder instanceof Module)
                binder.install((Module) apiBinder);
            //
            Hasor.info("init Event on : %s", modObj.getClass());
            getEventManager().doSyncEventIgnoreThrow(eventName, ModuleInfoBean.Prop_Init, true);
        } catch (Throwable e) {
            getEventManager().doSyncEventIgnoreThrow(eventName, ModuleInfoBean.Prop_Init, false, e);
            Hasor.error("%s is not init! %s", forModule.getDisplayName(), e.getMessage());
            if (this.forceModule)
                this.proForceModule(e);
        }
    }
    /**发送模块启动信号*/
    protected void onStart(ModuleInfo forModule) {
        if (!forModule.isReady() /*准备失败*/|| !forModule.isInit()/*初始化失败*/|| forModule.isRunning()/*尚在运行*/)
            return;
        if (forModule.isDependencyRunning() == false)
            return;/*依赖的模块尚未启动*/
        //
        HasorModule modObj = forModule.getModuleObject();
        String eventName = modObj.getClass().getName();
        try {
            modObj.start(this);
            Hasor.info("start Event on : %s", modObj.getClass());
            getEventManager().doSyncEventIgnoreThrow(eventName, ModuleInfoBean.Prop_Running, true);
        } catch (Throwable e) {
            Hasor.error("%s in the start phase encounters an error.\n%s", forModule.getDisplayName(), e);
            if (this.forceModule)
                this.proForceModule(e);
        }
    }
    /**发送模块停止信号*/
    protected void onStop(ModuleInfo forModule) {
        if (!forModule.isReady() || !forModule.isRunning())
            return;/*不处理没有准备好的模块，不处理已经停止的模块*/
        //
        HasorModule modObj = forModule.getModuleObject();
        String eventName = modObj.getClass().getName();
        try {
            modObj.stop(this);
            Hasor.info("stop Event on : %s", modObj.getClass());
            getEventManager().doSyncEventIgnoreThrow(eventName, ModuleInfoBean.Prop_Running, false);
        } catch (Throwable e) {
            Hasor.error("%s in the stop phase encounters an error.\n%s", forModule.getDisplayName(), e);
        }
    }
    /**模块的 destroy 生命周期调用。*/
    protected void onDestroy(ModuleInfo forModule) {
        if (!forModule.isReady())
            return;/*不处理没有准备好的模块*/
        /*向尚未停止的模块发送停止信号*/
        if (forModule.isRunning())
            this.onStop(forModule);
        //
        HasorModule modObj = forModule.getModuleObject();
        try {
            modObj.destroy(this);
            Hasor.info("destroy Event on : %s", modObj.getClass());
        } catch (Throwable e) {
            Hasor.error("%s in the destroy phase encounters an error.\n%s", e);
        }
    }
    //
    //
    //
    //
    /**获取Guice接口*/
    public Injector getGuice() {
        return this.guice;
    }
    /**为模块创建ApiBinder*/
    protected ApiBinder newApiBinder(final ModuleInfo forModule, final Binder binder) {
        return new ApiBinderModule(this, forModule) {
            public Binder getGuiceBinder() {
                return binder;
            }
        };
    }
    /**通过guice创建{@link Injector}，该方法会促使调用模块init生命周期*/
    protected Injector createInjector(Module[] guiceModules) {
        ArrayList<Module> guiceModuleSet = new ArrayList<Module>();
        guiceModuleSet.add(new MasterModule(this));
        if (guiceModules != null)
            for (Module mod : guiceModules)
                guiceModuleSet.add(mod);
        return Guice.createInjector(guiceModuleSet.toArray(new Module[guiceModuleSet.size()]));
    }
    /**打印模块状态*/
    protected void printModState() {
        List<ModuleInfo> modList = this.getModuleList();
        StringBuilder sb = new StringBuilder("");
        int size = String.valueOf(modList.size() - 1).length();
        for (int i = 0; i < modList.size(); i++) {
            ModuleInfo info = modList.get(i);
            sb.append(String.format("%0" + size + "d", i));
            sb.append('.');
            sb.append("-->[");
            //Running:运行中(start)、Initial:准备失败、Stopped:停止(stop)
            sb.append(!info.isReady() ? "Initial" : info.isRunning() ? "Running" : "Stopped");
            sb.append("] ");
            sb.append(info.getDisplayName());
            sb.append(" (");
            sb.append(info.getModuleObject().getClass());
            sb.append(")\n");
        }
        if (sb.length() > 1)
            sb.deleteCharAt(sb.length() - 1);
        Hasor.info("Modules State List:\n%s", sb);
    }
}
/**该类负责处理模块在Guice.configure期间的初始化任务。*/
class MasterModule implements Module {
    private DefaultAppContext appContet;
    public MasterModule(DefaultAppContext appContet) {
        this.appContet = appContet;
    }
    public void configure(Binder binder) {
        Hasor.info("send init sign...");
        ModuleInfo[] hasorModules = this.appContet.getModules();
        /*引发模块init生命周期*/
        for (ModuleInfo mod : hasorModules) {
            appContet.onInit(mod, binder);//触发生命周期
        }
        Hasor.info("init modules finish.");
        ExtBind.doBind(binder, appContet);
    }
}
class ExtBind {
    public static void doBind(final Binder binder, final AppContext appContet) {
        /*绑定InitContext对象的Provider*/
        binder.bind(InitContext.class).toProvider(new Provider<InitContext>() {
            public InitContext get() {
                return appContet;
            }
        });
        /*绑定AppContext对象的Provider*/
        binder.bind(AppContext.class).toProvider(new Provider<AppContext>() {
            public AppContext get() {
                return appContet;
            }
        });
        /*绑定EventManager对象的Provider*/
        binder.bind(EventManager.class).toProvider(new Provider<EventManager>() {
            public EventManager get() {
                return appContet.getEventManager();
            }
        });
        /*绑定Settings对象的Provider*/
        binder.bind(Settings.class).toProvider(new Provider<Settings>() {
            public Settings get() {
                return appContet.getSettings();
            }
        });
        /*绑定Environment对象的Provider*/
        binder.bind(Environment.class).toProvider(new Provider<Environment>() {
            public Environment get() {
                return appContet.getEnvironment();
            }
        });
    }
}