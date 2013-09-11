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
package net.hasor.core.anno.context;
import java.io.IOException;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import net.hasor.core.ModuleSettings;
import net.hasor.core.anno.DefineModule;
import net.hasor.core.context.DefaultAppContext;
import net.hasor.core.module.GuiceModulePropxy;
import org.more.util.StringUtils;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AnnoAppContext extends DefaultAppContext {
    /**容器事件：ContextEvent_LoadModule*/
    public static final String ContextEvent_LoadModule = "ContextEvent_LoadModule";
    //
    //
    //
    //
    //
    public AnnoAppContext() throws IOException {
        super();
    }
    public AnnoAppContext(String mainConfig) throws IOException {
        super(mainConfig);
    }
    public AnnoAppContext(String mainConfig, Object context) throws IOException {
        super(mainConfig, context);
    }
    protected void initContext() throws IOException {
        super.initContext();
        this.loadModule();
    }
    //
    /**装载模块*/
    protected void loadModule() {
        this.getEventManager().doSyncEventIgnoreThrow(ContextEvent_LoadModule, (AppContext) this);//发送阶段事件
        //1.扫描classpath包
        Set<Class<?>> initHookSet = this.getClassSet(DefineModule.class);
        Hasor.info("find Module : " + Hasor.logString(initHookSet));
        //2.过滤未实现HasorModule接口的类
        for (Class<?> modClass : initHookSet) {
            Module modObject = null;
            ModuleInfo moduleInfo = null;
            if (Module.class.isAssignableFrom(modClass) == true) {
                /*Hasor 模块*/
                modObject = this.createModule(modClass);
                moduleInfo = this.addModule(modObject);
            } else if (Module.class.isAssignableFrom(modClass) == true) {
                /*Guice 模块*/
                Module guiceObject = this.createModule(modClass);
                moduleInfo = this.addModule(new GuiceModulePropxy(guiceObject));
            } else
                /*错误*/
                Hasor.warning("not implemented HasorModule or Module :%s", moduleInfo);
            //
            if (moduleInfo instanceof ModuleSettings) {
                ModuleSettings infoCfg = (ModuleSettings) moduleInfo;
                DefineModule modAnno = modClass.getAnnotation(DefineModule.class);
                String dispName = StringUtils.isBlank(modAnno.displayName()) ? moduleInfo.getModuleObject().getClass().getSimpleName() : modAnno.displayName();
                infoCfg.setDisplayName(dispName);
                infoCfg.setDescription(modAnno.description());
            }
        }
    }
    private <T> T createModule(Class<?> listenerClass) {
        try {
            return (T) listenerClass.newInstance();
        } catch (Exception e) {
            Hasor.error("create %s an error!%s", listenerClass, e);
            return null;
        }
    }
}