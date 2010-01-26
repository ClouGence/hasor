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
package org.more.beans.resource.xml.core;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamReader;
import org.more.beans.info.BeanProperty;
import org.more.beans.resource.xml.XmlContextStack;
/**
 * 该类负责解析methodParam标签。
 * @version 2009-11-22
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class Tag_MethodParam extends Tag_Property {
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) {
        this.tagName = "methodParam";
        super.doStartEvent(xPath, xmlReader, context);
    }
    @Override
    public void doEndEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) {
        //一、获取堆栈的父堆栈的父堆栈，bean标签堆栈。
        XmlContextStack parent = context.getParent().getParent();
        ArrayList propList = (ArrayList) parent.get("tag_MethodParam");
        if (propList == null) {
            propList = new ArrayList();
            parent.put("tag_MethodParam", propList);
        }
        //二、添加属性
        propList.add((BeanProperty) context.context);
    }
}