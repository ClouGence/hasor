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
package org.more.core.xml.stream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
/**
 * 当遇到一个属性声明时。
 * @version 2010-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class AttributeEvent extends XmlStreamEvent {
    public AttributeEvent(String xpath, XmlReader xmlReader, XMLStreamReader reader, int index) {
        super(xpath, xmlReader, reader);
        this.attQName = reader.getAttributeName(index);
        this.index = index;
    }
    private QName attQName = null;
    private int   index    = 0;
    /**获取属性名称{@link QName}对象。*/
    public QName getName() {
        return this.attQName;
    }
    /**获取属性名(不包含命名空间前缀)。*/
    public String getElementName() {
        return this.getName().getLocalPart();
    }
    /**获取属性命名空间前缀。*/
    public String getPrefix() {
        return this.getName().getPrefix();
    }
    /**获取属性命名空间。*/
    public String getNamespaceURI() {
        return this.getName().getNamespaceURI();
    }
    /**获取属性命名空间。*/
    public String getValue() {
        return this.getReader().getAttributeValue(this.index);
    }
    /**该事件没有拍档。*/
    public boolean isPartner(XmlStreamEvent e) {
        return false;
    }
    /**属性事件，不是共有事件。*/
    public boolean isPublicEvent() {
        return false;
    }
}