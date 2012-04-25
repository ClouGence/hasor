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
package org.more.hypha.commons.xml;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.register.XmlRegister;
import org.more.hypha.context.xml.XmlNameSpaceRegister;
/**
 * 
 * @version : 2012-1-12
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractXmlRegister implements XmlNameSpaceRegister {
    /**该方法不允许继承，该方法中会将{@link #createKit(String, XmlRegister)}创建的Kit注册到manager中，
     * 如果在{@link #createKit(String, XmlRegister)}方法中已经注册则会自动忽略重复注册。*/
    public final XmlParserKit createXmlParserKit(String namespace, XmlRegister manager) {
        XmlParserKit kit = this.createKit(namespace, manager);
        if (manager.isRegeditKit(namespace, kit) == false)
            manager.regeditKit(namespace, kit);/*没有注册执行注册*/
        return kit;
    }
    /**子类可以重写该方法以达到创建自定义kit的目的。*/
    public XmlParserKit createKit(String namespace, XmlRegister manager) {
        return new XmlParserKit();
    };
}