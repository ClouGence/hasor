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
import org.more.NoDefinitionException;
import org.more.NotFoundException;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.AbstractEventManager;
import org.more.hypha.aop.AopResourceExpand;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.commons.Tag_Abstract;
import org.more.hypha.context.XmlDefineResource;
import org.more.hypha.event.Config_LoadedXmlEvent;
/**
 * 用于解析aop:apply标签
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAop_Apply extends Tag_Abstract implements XmlElementHook {
    public TagAop_Apply(XmlDefineResource configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        //1.取值
        String config = event.getAttributeValue("config");
        String toBeanExp = event.getAttributeValue("toBeanExp");//优先级高
        String toPackageExp = event.getAttributeValue("toPackageExp");
        //2.检测
        if (config == null)
            throw new NoDefinitionException("apply标签，检测到未定义config属性或者属性值为空。");
        AopResourceExpand plugin = (AopResourceExpand) this.getDefineResource().getPlugin(AopResourceExpand.AopDefineResourcePluginName);
        AopConfigDefine aopConfig = plugin.getAopDefine(config);
        if (aopConfig == null)
            throw new NotFoundException("apply标签在应用[" + config + "]aop配置时无法找到其定义的AopConfigDefine类型对象。");
        //3.注册监听器 
        AbstractEventManager manager = this.getDefineResource().getEventManager();
        if (toBeanExp != null)
            manager.addEventListener(Config_LoadedXmlEvent.class, new Listener_ToBeanApply(config, toBeanExp));
        else
            manager.addEventListener(Config_LoadedXmlEvent.class, new Listener_ToPackageApply(config, toPackageExp));
    }
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {}
}