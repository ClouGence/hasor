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
import org.more.hypha.define.aop.AopProcessor;
import org.more.hypha.define.aop.AopDefineInformed;
import org.more.hypha.define.aop.AopMethodInformed;
import org.more.hypha.define.aop.AopPointcutType;
import org.more.hypha.xml.XmlDefineResource;
/**
 * 该类是解析Informed类型标签的基类。
 * @version 2010-10-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagAop_AbstractListener extends TagAop_AbstractInformed<AopProcessor> {
    public TagAop_AbstractListener(XmlDefineResource configuration) {
        super(configuration);
    }
    /**返回Informed类型。*/
    protected abstract AopPointcutType getPointcutType();
    /**根据标签定义信息创建一个{@link AopDefineInformed}类型对象，如果标签中配置了method属性则创建{@link AopMethodInformed}返回。*/
    protected AopProcessor createDefine(StartElementEvent event) {
        String method = event.getAttributeValue("method");
        if (method != null) {
            AopMethodInformed informed = new AopMethodInformed();
            informed.setMethod(method);
            return informed;
        } else
            return new AopDefineInformed();
    }
    /**开始解析标签，确定PointcutType属性。*/
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        AopProcessor define = this.getDefine(context);
        define.setPointcutType(this.getPointcutType());
    }
}