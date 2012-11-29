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
package org.more.hypha.xml.tags.aop;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.define.aop.AopPointcut;
import org.more.hypha.xml.XmlDefineResource;
/**
 * 用于解析aop:pointcut标签
 * @version 2010-9-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAop_Pointcut extends TagAop_AbstractPointcut<AopPointcut> {
    /**创建{@link TagAop_Pointcut}对象*/
    public TagAop_Pointcut(XmlDefineResource configuration) {
        super(configuration);
    }
    /**返回{@link AopPointcut}类型对象。*/
    protected AopPointcut createDefine() {
        return new AopPointcut();
    }
    /**开始标签解析expression属性。*/
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        AopPointcut define = this.getDefine(context);
        define.setExpression(event.getAttributeValue("expression"));
    }
}