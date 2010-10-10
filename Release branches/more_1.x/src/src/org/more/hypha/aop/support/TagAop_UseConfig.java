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
import org.more.NotFoundException;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.aop.AopDefineResourcePlugin;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.support.TagBeans_AbstractBeanDefine;
import org.more.hypha.configuration.Tag_Abstract;
import org.more.hypha.configuration.XmlConfiguration;
/**
 * 用于解析aop:useConfig标签
 * @version 2010-10-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAop_UseConfig extends Tag_Abstract implements XmlElementHook {
    /**创建{@link TagAop_UseConfig}对象*/
    public TagAop_UseConfig(XmlConfiguration configuration) {
        super(configuration);
    }
    /**执行标签解析*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        AopDefineResourcePlugin plugin = (AopDefineResourcePlugin) this.getConfiguration().getPlugin(AopDefineResourcePlugin.AopPluginName);
        AbstractBeanDefine bean = (AbstractBeanDefine) context.getAttribute(TagBeans_AbstractBeanDefine.BeanDefine);
        String name = event.getAttributeValue("name");
        //
        AopConfigDefine aopConfig = plugin.getAopDefine(name);
        if (aopConfig == null)
            throw new NotFoundException("useConfig 在[" + bean.getName() + "]上的[" + name + "]配置，无法在aop配置库中找到。");
        plugin.setAop(bean, aopConfig);
    }
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {}
}