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
package org.more.hypha.aop.assembler;
import java.io.IOException;
import org.more.core.error.LoadException;
import org.more.core.event.Event;
import org.more.core.xml.XmlParserKit;
import org.more.hypha.aop.AopService;
import org.more.hypha.commons.xml.AbstractXmlRegister;
import org.more.hypha.context.InitEvent;
import org.more.hypha.xml.XmlDefineResource;
import org.more.hypha.xml.XmlNameSpaceRegister;
import org.more.hypha.xml.tags.aop.TagAop_Apply;
import org.more.hypha.xml.tags.aop.TagAop_Before;
import org.more.hypha.xml.tags.aop.TagAop_Config;
import org.more.hypha.xml.tags.aop.TagAop_Filter;
import org.more.hypha.xml.tags.aop.TagAop_Informed;
import org.more.hypha.xml.tags.aop.TagAop_PointGroup;
import org.more.hypha.xml.tags.aop.TagAop_Pointcut;
import org.more.hypha.xml.tags.aop.TagAop_Returning;
import org.more.hypha.xml.tags.aop.TagAop_Throwing;
import org.more.hypha.xml.tags.aop.TagAop_UseConfig;
/**
 * 该类实现了{@link XmlNameSpaceRegister}接口并且提供了对命名空间“http://project.byshell.org/more/schema/beans-aop”的解析支持。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class Register_Aop extends AbstractXmlRegister {
    /**执行初始化注册。*/
    public void initRegister(XmlParserKit parserKit, XmlDefineResource resource) throws LoadException, IOException {
        //1.添加Aop插件
        AopService service = new AopService_Impl();
        resource.getFlash().setAttribute(AopService_Impl.ServiceName, service);
        //2.注册标签解析器
        parserKit.regeditHook("/config", new TagAop_Config(resource));
        parserKit.regeditHook("*/pointcut", new TagAop_Pointcut(resource));
        parserKit.regeditHook("*/pointGroup", new TagAop_PointGroup(resource));
        parserKit.regeditHook("/useConfig", new TagAop_UseConfig(resource));
        parserKit.regeditHook("/@useConfig", new TagAop_UseConfig(resource));
        parserKit.regeditHook("/config/filter", new TagAop_Filter(resource));
        parserKit.regeditHook("/config/informed", new TagAop_Informed(resource));
        parserKit.regeditHook("/config/before", new TagAop_Before(resource));
        parserKit.regeditHook("/config/returning", new TagAop_Returning(resource));
        parserKit.regeditHook("/config/throwing", new TagAop_Throwing(resource));
        parserKit.regeditHook("/apply", new TagAop_Apply(resource));
        //2.注册事件
        resource.getEventManager().addEventListener(Event.getEvent(InitEvent.class), new OnInit(service));
    }
}