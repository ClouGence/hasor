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
package org.more.hypha.aop.xml;
import org.more.core.error.LostException;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.Event;
import org.more.hypha.EventManager;
import org.more.hypha.aop.AopService;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.hypha.context.xml.XmlLoadedEvent;
/**
 * 用于解析aop:apply标签
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAop_Apply extends TagAop_NS implements XmlElementHook {
    public TagAop_Apply(XmlDefineResource configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        //1.取值
        String config = event.getAttributeValue("config");
        String toBeanExp = event.getAttributeValue("toBeanExp");//优先级高
        String toPackageExp = event.getAttributeValue("toPackageExp");
        //2.检测
        if (config == null)
            throw new LostException("apply标签，检测到未定义config属性或者属性值为空。");
        AopService service = this.getAopConfig();
        AopConfigDefine aopConfig = service.getAopDefine(config);
        if (aopConfig == null)
            throw new LostException("apply标签在应用[" + config + "]aop配置时无法找到其定义的AopConfigDefine类型对象。");
        //3.注册监听器 
        EventManager manager = this.getDefineResource().getEventManager();
        Event e = Event.getEvent(XmlLoadedEvent.class);
        if (toBeanExp != null)
            manager.addEventListener(e, new Listener_ToBeanApply(config, toBeanExp));
        else
            manager.addEventListener(e, new Listener_ToPackageApply(config, toPackageExp));
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {}
}