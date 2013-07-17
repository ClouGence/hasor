/*
 * Copyright 2008-2009 the original author or authors.
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
package org.hasor.annotation.context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.hasor.Hasor;
import org.hasor.annotation.Module;
import org.hasor.context.HasorModule;
import org.hasor.context.core.DefaultAppContext;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoAppContext extends DefaultAppContext {
    public AnnoAppContext() throws IOException {
        super();
    }
    public AnnoAppContext(String mainConfig) throws IOException {
        super(mainConfig);
    }
    public AnnoAppContext(String mainConfig, Object context) throws IOException {
        super(mainConfig, context);
    }
    @Override
    protected void initContext() throws IOException {
        super.initContext();
        this.loadModule();
    }
    //
    /**装载模块*/
    protected void loadModule() {
        //1.扫描classpath包
        Set<Class<?>> initHookSet = this.getClassSet(Module.class);
        //2.过滤未实现HasorModule接口的类
        List<Class<? extends HasorModule>> initHookList = new ArrayList<Class<? extends HasorModule>>();
        for (Class<?> cls : initHookSet) {
            if (HasorModule.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented HasorModule :%s", cls);
            } else {
                initHookList.add((Class<? extends HasorModule>) cls);
            }
        }
        //3.排序最终数据
        Collections.sort(initHookList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                Module o1Anno = o1.getAnnotation(Module.class);
                Module o2Anno = o2.getAnnotation(Module.class);
                int o1AnnoIndex = o1Anno.startIndex();
                int o2AnnoIndex = o2Anno.startIndex();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        Hasor.info("find HasorModule : " + Hasor.logString(initHookList));
        //4.扫描所有ContextListener。
        Hasor.info("create HasorModule...");
        for (Class<?> modClass : initHookList) {
            HasorModule modObject = this.createModule(modClass);
            if (modObject != null)
                this.addModule(modObject);
        }
    }
    /**创建{@link HasorModule}接口对象。*/
    protected HasorModule createModule(Class<?> listenerClass) {
        try {
            return (HasorModule) listenerClass.newInstance();
        } catch (Exception e) {
            Hasor.error("create %s an error!%s", listenerClass, e);
            return null;
        }
    }
}