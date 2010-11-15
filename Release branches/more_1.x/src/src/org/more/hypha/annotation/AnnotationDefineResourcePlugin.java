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
package org.more.hypha.annotation;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.more.hypha.DefineResource;
import org.more.hypha.DefineResourcePlugin;
/**
 * 增强了{@link DefineResource}接口，以提供了更为丰富的anno相关方法。
 * @version 2010-10-8
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AnnotationDefineResourcePlugin extends DefineResourcePlugin {
    /**要注册的插件名*/
    public static final String AnnoDefineResourcePluginName = "$more_anno_ResourcePlugin";
    /**
     * 注册注册一个注解解析器，该方法会通知注解解析系统，当遇到某个特定的注解时这个类交付这个注解处理器进行处理。
     * @param anno 注册的注解类型。
     * @param watch 解析器。
     */
    public void registerAnnoKeepWatch(Class<? extends Annotation> anno, KeepWatchParser watch);
    /**检测是否注册了参数所表示的注解监视器，如果注册了返回true否则返回false*/
    public boolean containsKeepWatchParser(String annoType);
    /**根据Anno类型定义确定获取注册在其类型上的解析器。*/
    public Collection<KeepWatchParser> getAnnoKeepWatch(String annoType);
    /**通知aop解析器解析这个类，className参数表示的是预解析的类名。该类是通过{@link DefineResource}中的ClassLoader装载的。*/
    public void parserClass(String className) throws ClassNotFoundException, IOException;
    /**通知aop解析器解析这个类，classInputStream参数表示的是预解析的类输入流。*/
    public void parserClass(InputStream classInputStream) throws ClassNotFoundException, IOException;
}