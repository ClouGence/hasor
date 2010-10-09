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
package org.more.hypha.beans.support;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.hypha.beans.ValueMetaData;
import org.more.hypha.beans.define.AbstractPropertyDefine;
import org.more.hypha.beans.define.AbstractValueMetaData;
import org.more.hypha.beans.define.Collection_ValueMetaData;
import org.more.hypha.configuration.XmlConfiguration;
/**
 * 负责解析属性元信息标签的基类，该类考虑了值元信息的描述处于对另外一个值元信息的描述之中的情况。
 * @version 2010-9-19
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class TagBeans_AbstractValueMetaDataDefine<T extends AbstractValueMetaData> extends TagBeans_AbstractDefine<T> {
    /**属性值元信息.*/
    public static final String ValueMetaDataDefine = "$more_Beans_ValueMetaDataDefine";
    /**创建{@link TagBeans_AbstractValueMetaDataDefine}对象*/
    public TagBeans_AbstractValueMetaDataDefine(XmlConfiguration configuration) {
        super(configuration);
    }
    /**属性的定义名称*/
    protected String getAttributeName() {
        return ValueMetaDataDefine;
    }
    /**只在当前栈中寻找*/
    protected boolean isSpanStack() {
        return false;
    }
    /**负责将解析出来的ValueMetaData设置到属性中*/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        //1.获取当前元信息描述对象，如果当前值元信息描述是否处于另外一个值信息描述之下还要获取父级值元信息描述对象。
        ValueMetaData currentMetaData = this.getDefine(context);
        ValueMetaData parentMetaData = (ValueMetaData) context.getParentStack().getAttribute(ValueMetaDataDefine);
        //
        if (parentMetaData != null && parentMetaData instanceof Collection_ValueMetaData)
            ((Collection_ValueMetaData) parentMetaData).addObject(currentMetaData);
        else {
            AbstractPropertyDefine pdefine = (AbstractPropertyDefine) context.getAttribute(TagBeans_AbstractPropertyDefine.PropertyDefine);
            if (currentMetaData != null)
                pdefine.setValueMetaData(currentMetaData);
        }
        super.endElement(context, xpath, event);
    };
}