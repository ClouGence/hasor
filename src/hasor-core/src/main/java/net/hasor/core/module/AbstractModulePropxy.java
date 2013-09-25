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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.DependencySettings;
import net.hasor.core.AppContext;
import net.hasor.core.Dependency;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import net.hasor.core.context.AbstractAppContext;
import org.more.UnhandledException;
import org.more.util.exception.ExceptionUtils;
/**
 * 
 * @version : 2013-7-26
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractModulePropxy implements ModuleInfo/*提供模块基本信息*/, DependencySettings, Module {
    private String           displayName;
    private String           description;
    private Module           targetModule;
    private String           namespace;
    private AppContext       appContext;
    private List<Dependency> dependency;
    private boolean          isReady;
    private boolean          isStart;
    //
    public AbstractModulePropxy(Module targetModule, AppContext appContext) {
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
    protected abstract AbstractModulePropxy getInfo(Class<? extends Module> targetModule, AppContext appContext);
    //
    public void reverse(Class<? extends Module> targetModule) {
        if (isReady())
            /*模块已经准备好，只有当模块在准备期才可以使用该方法*/
            throw new IllegalStateException("Module is ready, only can use this method in run-up.");
        //
        AbstractModulePropxy moduleInfo = this.getInfo(targetModule, this.appContext);
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
        AbstractModulePropxy moduleInfo = this.getInfo(targetModule, this.appContext);
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
    //
    //
    //
    /**模块的 init 生命周期调用*/
    protected void onInit(Module forModule, ApiBinder apiBinder) {
        String eventName = forModule.getClass().getName();
        String phase = AbstractAppContext.ContextEvent_Init;
        apiBinder.getEnvironment().getEventManager().doSyncEventIgnoreThrow(eventName, phase, forModule);
        //
        forModule.init(apiBinder);
    }
    /**发送模块启动信号*/
    protected void onStart(Module forModule, AppContext appContext) {
        String eventName = forModule.getClass().getName();
        String phase = AbstractAppContext.ContextEvent_Start;
        appContext.getEventManager().doSyncEventIgnoreThrow(eventName, phase, forModule);
        //
        forModule.start(appContext);
    }
    /**发送模块停止信号*/
    protected void onStop(Module forModule, AppContext appContext) {
        String eventName = forModule.getClass().getName();
        String phase = AbstractAppContext.ContextEvent_Stop;
        appContext.getEventManager().doSyncEventIgnoreThrow(eventName, phase, forModule);
        //
        forModule.stop(appContext);
    }
}