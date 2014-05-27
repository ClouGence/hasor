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
package net.hasor.quick.anno;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.ModuleInfo;
import net.hasor.core.context.StandardAppContext;
import org.more.util.StringUtils;
/**
 * 支持通过 @AnnoModule 注解来增加 Hasor Module。
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AnnoStandardAppContext extends StandardAppContext {
    /***/
    public AnnoStandardAppContext() throws IOException, URISyntaxException {
        super();
    }
    /***/
    public AnnoStandardAppContext(String mainSettings) throws IOException, URISyntaxException {
        super(mainSettings);
    }
    /***/
    public AnnoStandardAppContext(File mainSettings) {
        super(mainSettings);
    }
    /***/
    public AnnoStandardAppContext(URI mainSettings) {
        super(mainSettings);
    }
    /***/
    public AnnoStandardAppContext(String mainSettings, Object context) throws IOException, URISyntaxException {
        super(mainSettings);
        this.setContext(context);
    }
    /***/
    public AnnoStandardAppContext(File mainSettings, Object context) {
        super(mainSettings);
        this.setContext(context);
    }
    /***/
    public AnnoStandardAppContext(URI mainSettings, Object context) {
        super(mainSettings);
        this.setContext(context);
    }
    // 
    protected void doInitialize() {
        this.loadModule();
        super.doInitialize();
    }
    //
    /**装载模块*/
    protected void loadModule() {
        //1.扫描classpath包
        Set<Class<?>> initHookSet = this.getEnvironment().findClass(AnnoModule.class);
        if (Hasor.isInfoLogger()) {
            StringBuffer sb = new StringBuffer();
            for (Class<?> e : initHookSet)
                sb.append("\n  " + e.getName());
            String outData = (sb.length() == 0 ? "nothing." : sb.toString());
            Hasor.logInfo("find Module : " + outData);
        }
        //2.过滤未实现HasorModule接口的类
        for (Class<?> modClass : initHookSet) {
            if (!Module.class.isAssignableFrom(modClass)) {
                Hasor.logWarn("not implemented net.hasor.core.Module :%s", modClass);
                continue;/*错误*/
            }
            /*Hasor 模块*/
            Module modObject = this.createModule(modClass);
            ModuleInfo moduleInfo = this.addModule(modObject);
            //
            AnnoModule modAnno = modClass.getAnnotation(AnnoModule.class);
            String dispName = StringUtils.isBlank(modAnno.displayName()) ? modClass.getSimpleName() : modAnno.displayName();
            String description = StringUtils.isBlank(modAnno.description()) ? modClass.getName() : modAnno.description();
            moduleInfo.setDisplayName(dispName);
            moduleInfo.setDescription(description);
        }
    }
    private <T> T createModule(Class<?> listenerClass) {
        try {
            return (T) listenerClass.newInstance();
        } catch (Exception e) {
            Hasor.logError("create %s an error!%s", listenerClass, e);
            return null;
        }
    }
}