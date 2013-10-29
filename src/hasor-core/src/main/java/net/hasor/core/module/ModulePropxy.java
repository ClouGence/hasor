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
package net.hasor.core.module;
import static net.hasor.core.AppContext.ModuleEvent_Start;
import static net.hasor.core.AppContext.ModuleEvent_Stoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.DependencySettings;
import net.hasor.core.AppContext;
import net.hasor.core.Dependency;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import org.more.UnhandledException;
import org.more.util.exception.ExceptionUtils;
/**
 * 
 * @version : 2013-7-26
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class ModulePropxy implements ModuleInfo/*提供模块基本信息*/, DependencySettings, Module {
    private String           displayName;
    private String           description;
    private Module           targetModule;
    private String           namespace;
    private AppContext       appContext;
    private List<Dependency> dependency;
    private boolean          isReady;
    private boolean          isStart;
    //
    public ModulePropxy(Module targetModule, AppContext appContext) {
        this.targetModule = Hasor.assertIsNotNull(targetModule);
        this.appContext = Hasor.assertIsNotNull(appContext);
        //
        this.description = targetModule.getClass().getName();
        this.displayName = targetModule.getClass().getSimpleName();
        this.dependency = new ArrayList<Dependency>();
        this.isReady = false;
    }
    //
    //
    //----------------------------------------------------------------------------------Base Method
    public String getSettingsNamespace() {
        return this.namespace;
    }
    public void bindingSettingsNamespace(String settingsNamespace) {
        if (isReady())
            /*模块已经准备好，只有当模块在准备期才可以使用该方法*/
            throw new IllegalStateException("Module is ready, only can use this method in run-up.");
        //
        this.namespace = settingsNamespace;
    }
    public void setDisplayName(String displayName) {
        this.description = displayName;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDisplayName() {
        return this.displayName;
    }
    public String getDescription() {
        return this.description;
    }
    public Module getTarget() {
        return this.targetModule;
    }
    public List<Dependency> getDependency() {
        return Collections.unmodifiableList(this.dependency);
    }
    public boolean isDependencyReady() {
        for (Dependency dep : this.dependency)
            if (dep.getModuleInfo().isReady() == false)
                if (dep.isOption() == false)
                    return false;
        return true;
    }
    public boolean isReady() {
        if (isDependencyReady() == false)
            return false;
        return this.isReady;
    }
    public boolean isStart() {
        return this.isStart;
    }
    public String toString() {
        return String.format("displayName is %s, class is %s",//
                this.displayName, this.targetModule.getClass());
    }
    //
    //
    //----------------------------------------------------------------------------Dependency Method
    /**尝试从容器中获取模块的代理对象*/
    protected abstract ModulePropxy getInfo(Class<? extends Module> targetModule, AppContext appContext);
    //
    public void reverse(Class<? extends Module> targetModule) {
        if (isReady())
            /*模块已经准备好，只有当模块在准备期才可以使用该方法*/
            throw new IllegalStateException("Module is ready, only can use this method in run-up.");
        //
        ModulePropxy moduleInfo = this.getInfo(targetModule, this.appContext);
        moduleInfo.weak(targetModule);
    }
    public void weak(Class<? extends Module> targetModule) {
        this._addDep(targetModule, true);
    }
    public void forced(Class<? extends Module> targetModule) {
        this._addDep(targetModule, false);
    }
    private void _addDep(Class<? extends Module> targetModule, boolean forced) {
        if (isReady())
            /*模块已经准备好，只有当模块在准备期才可以使用该方法*/
            throw new IllegalStateException("Module is ready, only can use this method in run-up.");
        //
        ModulePropxy moduleInfo = this.getInfo(targetModule, this.appContext);
        for (Dependency dep : this.dependency)
            if (dep.getModuleInfo() == moduleInfo)
                throw new IllegalStateException("before dependence is included.");
        //
        Dependency dep = new DependencyBean(moduleInfo, forced);
        this.dependency.add(dep);
    }
    //
    //
    //
    //
    //--------------------------------------------------------------------------------Lifety Method
    private boolean isFullStart() {
        return this.appContext.getSettings().getBoolean("hasor.fullStart", false);
    }
    /*解封异常*/
    private void proForceModule(Throwable e) {
        e = ExceptionUtils.getRootCause(e);
        if (e instanceof RuntimeException)
            throw (RuntimeException) e;
        else if (e instanceof Error)
            throw (Error) e;
        else
            throw new UnhandledException(e);
    }
    public final void init(ApiBinder apiBinder) {
        try {
            Module forModule = this.getTarget();
            this.onInit(forModule, apiBinder);
            Hasor.info("init Event on : %s", forModule.getClass());
            this.isReady = true;
        } catch (Throwable e) {
            this.isReady = false;
            Hasor.error("%s is not init! %s", this.getDisplayName(), e.getMessage());
            if (isFullStart())
                this.proForceModule(e);
        }
    }
    public final void start(AppContext appContext) {
        if (this.isReady() == false/*准备失败*/|| this.isStart() == true/*已经启动*/)
            return;
        //
        try {
            Module forModule = this.getTarget();
            this.onStart(forModule, appContext);
            Hasor.info("start Event on : %s", forModule.getClass());
            this.isStart = true;
        } catch (Throwable e) {
            this.isStart = false;
            Hasor.error("%s in the start phase encounters an error.\n%s", this.getDisplayName(), e);
            if (isFullStart())
                this.proForceModule(e);
        }
    }
    public final void stop(AppContext appContext) {
        if (this.isStart() == false/*已经处于停止状态*/)
            return;
        //
        Module forModule = this.getTarget();
        try {
            this.onStop(forModule, appContext);
            Hasor.info("stop Event on : %s", forModule.getClass());
            this.isStart = false;
        } catch (Throwable e) {
            Hasor.error("%s in the stop phase encounters an error.\n%s", this.getDisplayName(), e);
        }
    }
    /*利用 AppContext 作 KEY 可以保证在不同环境下静态字段内容的正确性*/
    private static Map<AppContext, ModuleInfo> loacalModuleInfo = new HashMap<AppContext, ModuleInfo>();
    /**根据 AppContext 容器获取当前 ModuleInfo。<p>
     * 注意：只有当 ModuleInfo 位于 start stop 过程内，该方法才会返回相对应的ModuleInfo。否则返回值为空。*/
    public static ModuleInfo getLocalModuleInfo(AppContext appContext) {
        return (appContext != null) ? loacalModuleInfo.get(appContext) : null;
    }
    /**模块的 init 生命周期调用*/
    protected void onInit(Module forModule, ApiBinder apiBinder) {
        forModule.init(apiBinder);
    }
    /**发送模块启动信号*/
    protected void onStart(Module forModule, AppContext appContext) {
        loacalModuleInfo.put(this.appContext, this);
        appContext.getEventManager().doSyncEventIgnoreThrow(ModuleEvent_Start, forModule, appContext);
        forModule.start(appContext);
        loacalModuleInfo.remove(this.appContext);
    }
    /**发送模块停止信号*/
    protected void onStop(Module forModule, AppContext appContext) {
        loacalModuleInfo.put(this.appContext, this);
        forModule.stop(appContext);
        appContext.getEventManager().doSyncEventIgnoreThrow(ModuleEvent_Stoped, forModule, appContext);
        loacalModuleInfo.remove(this.appContext);
    }
}