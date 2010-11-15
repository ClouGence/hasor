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
package org.more.hypha.aop.support;
import org.more.core.xml.XmlParserKit;
import org.more.hypha.DefineResource;
import org.more.hypha.aop.AopDefineResourcePlugin;
import org.more.hypha.aop.assembler.AopDefineResourcePlugin_Impl;
import org.more.hypha.configuration.NameSpaceRegister;
import org.more.hypha.configuration.XmlConfiguration;
/**
 * 该类实现了{@link NameSpaceRegister}接口并且提供了对命名空间“http://project.byshell.org/more/schema/aop”的解析支持。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class Register_Aop implements NameSpaceRegister {
    /**如果没有指定namespaceURL参数则该常量将会指定默认的命名空间。*/
    public static final String DefaultNameSpaceURL = "http://project.byshell.org/more/schema/aop";
    /**执行初始化注册。*/
    public void initRegister(String namespaceURL, XmlConfiguration configuration, DefineResource resource) {
        //1.添加Aop插件
        resource.setPlugin(AopDefineResourcePlugin.AopDefineResourcePluginName, new AopDefineResourcePlugin_Impl(resource));
        //2.注册标签解析器
        XmlParserKit kit = new XmlParserKit();
        kit.regeditHook("/config", new TagAop_Config(resource));
        kit.regeditHook("*/pointcut", new TagAop_Pointcut(resource));
        kit.regeditHook("*/pointGroup", new TagAop_PointGroup(resource));
        kit.regeditHook("/useConfig", new TagAop_UseConfig(resource));
        kit.regeditHook("/@useConfig", new TagAop_UseConfig(resource));
        kit.regeditHook("/config/filter", new TagAop_Filter(resource));
        kit.regeditHook("/config/informed", new TagAop_Informed(resource));
        kit.regeditHook("/config/before", new TagAop_Before(resource));
        kit.regeditHook("/config/returning", new TagAop_Returning(resource));
        kit.regeditHook("/config/throwing", new TagAop_Throwing(resource));
        kit.regeditHook("/apply", new TagAop_Apply(resource));
        //3.注册命名空间
        if (namespaceURL == null)
            namespaceURL = DefaultNameSpaceURL;
        configuration.regeditXmlParserKit(namespaceURL, kit);
    }
}