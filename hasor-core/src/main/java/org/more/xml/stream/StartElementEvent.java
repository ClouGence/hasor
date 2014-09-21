/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.xml.stream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
/**
 * 当遇到一个开始标签时。
 * @version 2010-9-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class StartElementEvent extends XmlStreamEvent {
    public StartElementEvent(final String xpath, final XMLStreamReader reader) {
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
    /**获取在这个元素上定义的命名空间总数。*/
    public int getNamespaceCount() {
        return this.getReader().getNamespaceCount();
    }
    /**获取在这个元素上定义的指定索引的命名空间前缀。*/
    public String getNamespacePrefix(final int index) {
        return this.getReader().getNamespacePrefix(index);
    }
    /**获取在这个元素上定义的指定索引的命名空间URI。*/
    public String getNamespaceURI(final int index) {
        return this.getReader().getNamespaceURI(index);
    }
    /**使用指定的命名空间前缀获取命名空间URI。*/
    public String getNamespaceURI(final String prefix) {
        return this.getReader().getNamespaceURI(prefix);
    }
    /**获取在该元素上定义的属性总数。*/
    public int getAttributeCount() {
        return this.getReader().getAttributeCount();
    }
    /**获取该元素上定义的指定属性名。*/
    public QName getAttributeName(final int index) {
        if (index > this.getAttributeCount()) {
            return null;
        }
        return this.getReader().getAttributeName(index);
    }
    /**获取该元素上定义的指定属性值。*/
    public String getAttributeValue(final int index) {
        if (index > this.getAttributeCount()) {
            return null;
        }
        return this.getReader().getAttributeValue(index);
    }
    /**获取该元素上定义的指定属性类型。*/
    public String getAttributeType(final int index) {
        if (index > this.getAttributeCount()) {
            return null;
        }
        return this.getReader().getAttributeType(index);
    }
    /**获取该元素上定义的指定属性类型。*/
    public String getAttributeType(final String name) {
        for (int i = 0; i < this.getAttributeCount(); i++) {
            if (name.equals(this.getAttributeName(i).getLocalPart()) == true) {
                return this.getAttributeType(i);
            }
        }
        return null;
    }
    /**获取该元素上定义的指定属性值。*/
    public String getAttributeValue(final String name) {
        for (int i = 0; i < this.getAttributeCount(); i++) {
            if (name.equals(this.getAttributeName(i).getLocalPart()) == true) {
                return this.getAttributeValue(i);
            }
        }
        return null;
    }
    /**读取纯文本元素的内容，如果不是纯文本元素，则抛出异常。*/
    public String getElementText() throws XMLStreamException {
        return this.getReader().getElementText();
    }
    /**该事件的拍档是{@link EndElementEvent}类型对象，被测试的对象的{@link QName}必须与当前对象的{@link QName}相同，并且要在同一个xpath下。*/
    @Override
    public boolean isPartner(final XmlStreamEvent e) {
        if (e instanceof EndElementEvent == false) {
            return false;
        }
        //
        EndElementEvent ende = (EndElementEvent) e;
        QName qnameA = this.getName();
        QName qnameB = ende.getName();
        if (qnameA.equals(qnameB) == false) {
            return false;
        }
        if (this.getXpath().equals(ende.getXpath()) == false) {
            return false;
        }
        return true;
    };
    /**元素开始事件，不是共有事件。*/
    @Override
    public boolean isPublicEvent() {
        return false;
    }
}