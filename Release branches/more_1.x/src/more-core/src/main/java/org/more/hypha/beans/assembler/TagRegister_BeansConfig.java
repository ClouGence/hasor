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
package org.more.hypha.beans.assembler;
import org.more.core.xml.XmlParserKit;
import org.more.hypha.Event;
import org.more.hypha.EventManager;
import org.more.hypha.beans.xml.BeansConfig_BeanType;
import org.more.hypha.beans.xml.BeansConfig_BeanTypeConfig;
import org.more.hypha.beans.xml.BeansConfig_MDParserConfig;
import org.more.hypha.beans.xml.BeansConfig_Parser;
import org.more.hypha.context.DestroyEvent;
import org.more.hypha.context.InitEvent;
import org.more.hypha.context.StartedServicesEvent;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.hypha.context.xml.XmlNameSpaceRegister;
/**
 * 该类实现了{@link XmlNameSpaceRegister}接口并且提供了对命名空间“http://project.byshell.org/more/schema/beans-config”的解析支持。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagRegister_BeansConfig implements XmlNameSpaceRegister {
    /**如果没有指定namespaceURL参数则该常量将会指定默认的命名空间。*/
    public static final String DefaultNameSpaceURL = "http://project.byshell.org/more/schema/beans-config";
    /**执行初始化注册。*/
    public void initRegister(String namespaceURL, XmlDefineResource resource) {
        //1.注册标签解析器
        XmlParserKit kit = new XmlParserKit();
        kit.regeditHook("/beanType-config", new BeansConfig_BeanTypeConfig(resource));
        kit.regeditHook("/beanType-config/beanType", new BeansConfig_BeanType(resource));
        kit.regeditHook("/mdParser-config", new BeansConfig_MDParserConfig(resource));
        kit.regeditHook("/mdParser-config/parser", new BeansConfig_Parser(resource));
        //
        //2.注册命名空间
        if (namespaceURL == null)
            namespaceURL = DefaultNameSpaceURL;
        resource.regeditXmlParserKit(namespaceURL, kit);
        //4.注册事件
        EventManager em = resource.getEventManager();
        em.addEventListener(Event.getEvent(InitEvent.class), new OnInit());
        em.addEventListener(Event.getEvent(StartedServicesEvent.class), new OnStarted());
        em.addEventListener(Event.getEvent(DestroyEvent.class), new OnDestroy());
    }
}