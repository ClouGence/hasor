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
package org.more.beans.resource.xml;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamReader;
import org.more.NoDefinitionException;
import org.more.beans.info.BeanInterface;
/**
 * 负责处理addImpl标签，单标记标签。<br/>
 * id="1" type="java.util.List" delegate-refBean="a" refType="refType"
 * Date : 2009-11-21
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
class Tag_AddImpl extends DoTagEvent {
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        // id="1" type="java.util.List" delegate-refBean="a" refType="refType"
        BeanInterface bi = new BeanInterface();
        int attCount = xmlReader.getAttributeCount();
        for (int i = 0; i < attCount; i++) {
            String key = xmlReader.getAttributeLocalName(i);
            String var = xmlReader.getAttributeValue(i);
            if (key.equals("id") == true)
                bi.setId(var);
            else if (key.equals("type") == true)
                bi.setPropType(var);
            else if (key.equals("delegate-refBean") == true)
                bi.setDelegateRefBean(var);
            else if (key.equals("refType") == true)
                bi.setRefType(var);
            else
                throw new NoDefinitionException("addImpl标签出现未定义属性[" + key + "]");
        }
        context.context = bi;
    }
    @Override
    public void doEndEvent(String xPath, XMLStreamReader xmlReader, ContextStack context) {
        //一、获取堆栈的父堆栈，bean标签堆栈。
        ContextStack parent = context.getParent();
        ArrayList propList = (ArrayList) parent.get("tag_AddImpl");
        if (propList == null) {
            propList = new ArrayList();
            parent.put("tag_AddImpl", propList);
        }
        //二、添加属性
        propList.add((BeanInterface) context.context);
    }
}