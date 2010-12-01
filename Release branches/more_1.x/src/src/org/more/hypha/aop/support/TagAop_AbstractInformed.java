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
import org.more.hypha.aop.AopDefineResourcePlugin;
import org.more.hypha.aop.define.AbstractInformed;
import org.more.hypha.aop.define.AbstractPointcutDefine;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.aop.define.AopDefineInformed;
import org.more.hypha.context.Tag_Abstract;
import org.more.hypha.context.XmlDefineResource;
/**
 * 处理informed类型标签的refBean属性。
 * @version 2010-10-9
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public abstract class TagAop_AbstractInformed<T extends AopDefineInformed> extends Tag_Abstract implements XmlElementHook {
    public static final String AopInformedDefine = "$more_aop_AopInformedDefine";
    public TagAop_AbstractInformed(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建一个{@link AbstractInformed}定义对象。*/
    protected abstract T createDefine(StartElementEvent event);
    /**获取创建的{@link AbstractInformed}定义对象。*/
    protected final T getDefine(XmlStackDecorator context) {
        return (T) context.getAttribute(AopInformedDefine);
    };
    /**开始标签解析*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) throws NoDefinitionException, NotFoundException {
        T define = this.createDefine(event);
        context.setAttribute(AopInformedDefine, define);
        //1.获取所属config
        AopConfigDefine config = (AopConfigDefine) context.getAttribute(TagAop_Config.ConfigDefine);
        //2.解析标签
        String refBean = event.getAttributeValue("refBean");
        if (refBean == null)
            throw new NoDefinitionException("[" + config.getName() + "]解析informed、before、returning、throwing、filter标签时没有定义refBean属性。");
        if (this.getDefineResource().containsBeanDefine(refBean) == false)
            throw new NotFoundException("[" + config.getName() + "]解析informed、before、returning、throwing、filter标签时无法找到定义的[" + refBean + "]Bean。");
        define.setRefBean(refBean);
        String pointcutRef = event.getAttributeValue("pointcut-ref");
        //3.将Informed添加到父类的config中。
        if (pointcutRef != null) {
            AopDefineResourcePlugin plugin = (AopDefineResourcePlugin) this.getDefineResource().getPlugin(AopDefineResourcePlugin.AopDefineResourcePluginName);
            AbstractPointcutDefine pointcutDefine = plugin.getPointcutDefine(pointcutRef);
            config.addInformed(define, pointcutDefine);
        } else
            config.addInformed(define);
    }
    /**结束标签解析，删除使用的变量。*/
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        context.removeAttribute(AopInformedDefine);
    }
}