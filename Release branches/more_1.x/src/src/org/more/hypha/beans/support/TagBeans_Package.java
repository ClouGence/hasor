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
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.configuration.Tag_Abstract;
import org.more.hypha.configuration.XmlConfiguration;
/**
 * 用于解析/beans/package标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_Package extends Tag_Abstract implements XmlElementHook {
    /**保存于上下文中的package变量名。*/
    public static final String LogicPackage = "$more_Beans_LogicPackage";
    /**创建{@link TagBeans_Package}对象*/
    public TagBeans_Package(XmlConfiguration configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        String parentPackage = (String) context.getAttribute(LogicPackage);
        //
        context.createStack();
        String logicPackage = event.getAttributeValue("package");
        if (logicPackage == null || logicPackage.equals("") == true)
            throw new NullPointerException("解析器解析到package标签未定义有效的pakcage属性。");
        if (parentPackage != null)
            logicPackage = parentPackage + "." + logicPackage;
        context.setAttribute(LogicPackage, logicPackage);
    }
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {
        context.dropStack();
    }
}