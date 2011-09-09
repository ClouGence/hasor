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
package org.more.hypha.beans.xml;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析/defaultPackage标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_DefaultPackage extends TagBeans_NS implements XmlElementHook {
    /**默认包位置*/
    public static String DefaultPackage = null;
    /**创建{@link TagBeans_DefaultPackage}对象*/
    public TagBeans_DefaultPackage(XmlDefineResource configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        String logicPackage = event.getAttributeValue("package");
        if (logicPackage == null || logicPackage.equals("") == true)
            DefaultPackage = null;
        else
            DefaultPackage = logicPackage;
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) {}
}