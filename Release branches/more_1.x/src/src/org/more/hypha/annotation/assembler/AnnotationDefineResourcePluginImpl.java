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
package org.more.hypha.annotation.assembler;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.more.hypha.DefineResource;
import org.more.hypha.annotation.AnnotationDefineResourcePlugin;
import org.more.hypha.annotation.KeepWatchParser;
import org.more.hypha.aop.AopDefineResourcePlugin;
/**
 * 注解插件接口{@link AnnotationDefineResourcePlugin}的实现类。
 * @version 2010-10-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnotationDefineResourcePluginImpl implements AnnotationDefineResourcePlugin {
    private DefineResource                         config      = null;
    private ArrayList<String>                      parserTypes = new ArrayList<String>();
    private HashMap<String, List<KeepWatchParser>> parserMap   = new HashMap<String, List<KeepWatchParser>>();
    //
    public AnnotationDefineResourcePluginImpl(DefineResource config) {
        this.config = config;
    }
    public DefineResource getTarget() {
        return this.config;
    }
    public AopDefineResourcePlugin getAopDefineResourcePlugin() {
        return (AopDefineResourcePlugin) this.config.getPlugin(AopDefineResourcePlugin.AopDefineResourcePluginName);
    }
    /**
     * 注册一个注解解析器，该方法会通知注解解析系统，当遇到某个特定的注解时这个类交付这个注解处理器进行处理。
     * @param anno 注册的注解类型。
     * @param watch 解析器。
     */
    public void registerAnnoKeepWatch(Class<? extends Annotation> anno, KeepWatchParser watch) {
        String annoType = anno.getName();
        List<KeepWatchParser> parserList = null;
        if (this.parserTypes.contains(annoType) == false) {
            parserList = new ArrayList<KeepWatchParser>();
            this.parserMap.put(annoType, parserList);
            this.parserTypes.add(annoType);
        } else
            parserList = this.parserMap.get(annoType);
        parserList.add(watch);
    }
    public boolean containsKeepWatchParser(String annoType) {
        return this.parserTypes.contains(annoType);
    }
    /**根据Anno类型定义确定获取注册在其类型上的解析器。*/
    public Collection<KeepWatchParser> getAnnoKeepWatch(String annoType) {
        return this.parserMap.get(annoType);
    }
    /**正式解析类，className表示要解析的类名。annoTypes表示这个类中声明的所有注解。*/
    public synchronized void parserClass(String className, List<String> annoTypes) throws ClassNotFoundException {
        //1.按照注册顺序排序
        Collections.sort(annoTypes, new Comparator<String>() {
            public int compare(String o1, String o2) {
                int a = AnnotationDefineResourcePluginImpl.this.parserTypes.indexOf(o1);
                int b = AnnotationDefineResourcePluginImpl.this.parserTypes.indexOf(o2);
                if (a > b)
                    return 1;
                else if (a == b)
                    return 0;
                return -1;
            }
        });
        //2.解析
        for (String anno : annoTypes) {
            for (KeepWatchParser p : this.parserMap.get(anno))
                p.process(this.config.getClassLoader().loadClass(className), this.config, this);
        }
    }
}