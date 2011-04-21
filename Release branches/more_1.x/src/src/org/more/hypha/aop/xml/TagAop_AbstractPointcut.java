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
import org.more.RepeateException;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.aop.AopInfoConfig;
import org.more.hypha.aop.define.AbstractPointcutDefine;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.aop.define.AopPointcutGroupDefine;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析切点标签的基类，该类会解析name属性。
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public abstract class TagAop_AbstractPointcut<T extends AbstractPointcutDefine> extends TagAop_NS implements XmlElementHook {
    private static final String PointcutDefine = "$more_Aop_PointcutDefine";
    /**创建{@link TagAop_AbstractPointcut}类型对象。*/
    public TagAop_AbstractPointcut(XmlDefineResource configuration) {
        super(configuration);
    }
    /**创建一个{@link AbstractPointcutDefine}定义对象。*/
    protected abstract T createDefine();
    /**获取创建的{@link AbstractPointcutDefine}定义对象。*/
    protected final T getDefine(XmlStackDecorator context) {
        return (T) context.getAttribute(PointcutDefine);
    };
    /**开始处理标签*/
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        context.createStack();
        T define = this.createDefine();
        String name = event.getAttributeValue("name");
        if (name != null)
            define.setName(name);// or this.putAttribute(define, "name", name);
        context.setAttribute(PointcutDefine, define);
    }
    /**结束处理标签*/
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        T define = this.getDefine(context);
        boolean isReg = false;
        //1.Pointcut出现在Group下面
        T parentDefine = (T) context.getParentStack().getAttribute(PointcutDefine);
        if (parentDefine != null) {
            if (parentDefine instanceof AopPointcutGroupDefine) {
                ((AopPointcutGroupDefine) parentDefine).addPointcutDefine(define);
                isReg = true;
            }
        }
        //2.Pointcut出现在Config下面 
        AopConfigDefine parentConfig = (AopConfigDefine) context.getAttribute(TagAop_Config.ConfigDefine);
        if (isReg == false && parentConfig != null) {
            if (parentConfig != null) {
                if (parentConfig.getDefaultPointcutDefine() != null)
                    throw new RepeateException("不能对AopConfigDefine类型的[" + parentConfig.getName() + "]进行第二次定义aop切点。");
                parentConfig.setDefaultPointcutDefine(define);
                isReg = true;
            }
        }
        //3.注册到环境中
        if (isReg == false && define.getName() != null) {
            AopInfoConfig plugin = (AopInfoConfig) this.getFlash().getAttribute(AopInfoConfig.ServiceName);
            if (plugin.containPointcutDefine(define.getName()) == true)
                throw new RepeateException("不能重复定义[" + define.getName() + "]切入点对象。");
            plugin.addPointcutDefine(define);
        }
        //
        context.removeAttribute(PointcutDefine);
        context.dropStack();
    }
}