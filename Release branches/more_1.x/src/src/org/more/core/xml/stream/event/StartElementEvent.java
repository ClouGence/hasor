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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.more.core.xml.stream.XmlStreamEvent;
/**
 * 当遇到一个开始标签时。
 * @version 2010-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class StartElementEvent extends XmlStreamEvent {
    public StartElementEvent(String xpath, XMLStreamReader reader) {
        super(xpath, reader);
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
    /**获取在该元素上定义的属性总数。*/
    public int getAttributeCount() {
        return this.getReader().getAttributeCount();
    }
    /**获取该元素上定义的指定属性名。*/
    public QName getAttributeName(int index) {
        return this.getReader().getAttributeName(index);
    }
    /**获取该元素上定义的指定属性值。*/
    public String getAttributeValue(int index) {
        return this.getReader().getAttributeValue(index);
    }
    /**读取纯文本元素的内容，如果不是纯文本元素，则抛出异常。*/
    public String getElementText() throws XMLStreamException {
        return this.getReader().getElementText();
    }
}