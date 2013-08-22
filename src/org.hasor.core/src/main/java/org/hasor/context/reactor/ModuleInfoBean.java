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
package org.hasor.context.reactor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.Dependency;
import org.hasor.context.HasorEventListener;
import org.hasor.context.HasorModule;
import org.hasor.context.InitContext;
import org.hasor.context.ModuleInfo;
import org.hasor.context.ModuleSettings;
import org.more.UndefinedException;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-7-26
 * @author 赵永春 (zyc@hasor.net)
 */
public final class ModuleInfoBean implements ModuleSettings, HasorEventListener {
    public static final String Prop_Ready   = "ready";
    public static final String Prop_Init    = "init";
    public static final String Prop_Running = "running";
    //
    private String             displayName;
    private String             description;
    private HasorModule        moduleObject;
    private String             namespace;
    private AppContext         appContext;
    private List<Dependency>   dependency;
    /*运行状态*/
    private boolean            running;
    /*是否准备好*/
    private boolean            ready;
    /*是否初始化*/
    private boolean            init;
    //
    //
    public ModuleInfoBean(HasorModule moduleObject, AppContext appContext) {
        this.moduleObject = Hasor.assertIsNotNull(moduleObject);
        this.appContext = appContext;
        this.dependency = new ArrayList<Dependency>();
        this.ready = false;
        this.running = false;
        this.displayName = moduleObject.getClass().getSimpleName();
        this.description = moduleObject.getClass().getName();
        /*ModuleInfoBean通过注册事件监听器监听来自于容器对模块状态的属性更新事件*/
        appContext.getEventManager().addEventListener(this.moduleObject.getClass().getName(), this);
    }
    public void onEvent(String event, Object[] params) {
        if (params == null || params.length < 2)
            return;
        /*该方法是用于接收来自于容器的模块事件*/
        String propName = (String) params[0];
        Object propValue = params[1];
        if (StringUtils.equalsIgnoreCase(Prop_Ready, propName) == true) {
            this.ready = (Boolean) propValue;
        } else if (StringUtils.equalsIgnoreCase(Prop_Init, propName) == true) {
            this.init = (Boolean) propValue;
        } else if (StringUtils.equalsIgnoreCase(Prop_Running, propName) == true) {
            this.running = (Boolean) propValue;
        }
    }
    private ModuleInfo getInfo(Class<? extends HasorModule> targetModule) {
        ModuleInfo[] infoArray = this.appContext.getModules();
        if (infoArray != null)
            for (ModuleInfo info : infoArray)
                if (targetModule == info.getModuleObject().getClass())
                    return info;
        throw new UndefinedException(targetModule.getName() + " module is Undefined!");
    }
    public void beforeMe(Class<? extends HasorModule> targetModule) {
        if (ready())
            /*模块已经准备好，只有当模块在准备期才可以使用该方法*/
            throw new IllegalStateException("Module is ready, only can use this method in run-up.");
        //
        ModuleInfo targetModuleInfo = this.getInfo(targetModule);
        for (Dependency dep : this.dependency)
            if (dep.getModuleInfo() == targetModuleInfo)
                throw new IllegalStateException("before dependence is included.");
        //
        Dependency dep = new DependencyBean(targetModuleInfo, true);
        this.dependency.add(dep);
    }
    public void followTarget(Class<? extends HasorModule> targetModule) {
        if (ready())
            /*模块已经准备好，只有当模块在准备期才可以使用该方法*/
            throw new IllegalStateException("Module is ready, only can use this method in run-up.");
        //
        ModuleInfo targetModuleInfo = this.getInfo(targetModule);
        for (Dependency dep : this.dependency)
            if (dep.getModuleInfo() == targetModuleInfo)
                throw new IllegalStateException("before dependence is included.");
        //
        Dependency dep = new DependencyBean(targetModuleInfo, false);
        this.dependency.add(dep);
    }
    public List<Dependency> getDependency() {
        if (!ready())
            /*模块尚未准备好，依赖尚不清楚*/
            throw new IllegalStateException("Module is not ready, Dependency is not clean.");
        return Collections.unmodifiableList(this.dependency);
    }
    List<Dependency> getInternalDependency() {
        return this.dependency;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
    public void bindingSettingsNamespace(String namespace) {
        if (ready())
            /*模块已经准备好，只有当模块在准备期才可以使用该方法*/
            throw new IllegalStateException("Module is ready, only can use this method in run-up.");
        //
        this.namespace = namespace;
    }
    public String getSettingsNamespace() {
        return this.namespace;
    }
    public HasorModule getModuleObject() {
        return this.moduleObject;
    }
    public String toString() {
        return String.format("displayName is %s, class is %s",//
                this.displayName, this.moduleObject.getClass());
    }
    public void afterMe(Class<? extends HasorModule> targetModule) {
        ModuleSettings setting = (ModuleSettings) this.getInfo(targetModule);
        setting.beforeMe(this.getModuleObject().getClass());
    }
    public boolean isRunning() {
        return this.running;
    }
    private boolean ready() {
        if (this.appContext.isInit() == false)
            return false;
        return this.isReady();
    }
    public boolean isReady() {
        return this.ready;
    }
    public boolean isInit() {
        return this.init;
    }
    public boolean isDependencyReady() {
        for (Dependency dep : this.getInternalDependency())
            if (dep.getModuleInfo().isDependencyReady() == false || dep.getModuleInfo().isReady() == false)
                if (dep.isOption() == false)
                    return false;
        return true;
    }
    public boolean isDependencyRunning() {
        for (Dependency dep : this.getInternalDependency())
            if (dep.getModuleInfo().isDependencyRunning() == false || dep.getModuleInfo().isRunning() == false)
                if (dep.isOption() == false)
                    return false;
        return true;
    }
    public boolean isDependencyInit() {
        for (Dependency dep : this.getInternalDependency())
            if (dep.getModuleInfo().isDependencyInit() == false || dep.getModuleInfo().isInit() == false)
                if (dep.isOption() == false)
                    return false;
        return true;
    }
    public InitContext getInitContext() {
        return this.appContext;
    }
}