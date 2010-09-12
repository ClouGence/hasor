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
package org.more.core.xml.stream.event;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.more.core.xml.stream.XmlReader;
import org.more.core.xml.stream.XmlStreamEvent;
/**
 * 当遇到标签结束时。
 * @version 2010-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class EndElementEvent extends XmlStreamEvent {
    public EndElementEvent(String xpath, XmlReader xmlReader, XMLStreamReader reader) {
        super(xpath, xmlReader, reader);
    }
    /**获取元素名称{@link QName}对象。*/
    public QName getName() {
        return this.getReader().getName();
    }
    /**获取元素名(不包含命名空间前缀)。*/
    public String getElementName() {
        return this.getName().getLocalPart();
    }
    /**获取元素命名空间前缀。*/
    public String getPrefix() {
        return this.getName().getPrefix();
    }
    /**获取元素命名空间。*/
    public String getNamespaceURI() {
        return this.getName().getNamespaceURI();
    }
    /**获取在这个元素上定义的命名空间总数。*/
    public int getNamespaceCount() {
        return this.getReader().getNamespaceCount();
    }
    /**获取在这个元素上定义的指定索引的命名空间前缀。*/
    public String getNamespacePrefix(int index) {
        return this.getReader().getNamespacePrefix(index);
    }
    /**获取在这个元素上定义的指定索引的命名空间URI。*/
    public String getNamespaceURI(int index) {
        return this.getReader().getNamespaceURI(index);
    }
    /**使用指定的命名空间前缀获取命名空间URI。*/
    public String getNamespaceURI(String prefix) {
        return this.getReader().getNamespaceURI(prefix);
    }
}