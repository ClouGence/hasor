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
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
/**
 * xml事件流的基类。
 * @version 2010-9-7
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class XmlStreamEvent {
    private String          xpath          = null; //当前事件所处的xpath
    private XMLStreamReader reader         = null; //底层的XMLStreamReader
    private QName           currentElement = null;
    private boolean         skip           = false;
    //-----------------------------------------------------
    public XmlStreamEvent(final String xpath, final XMLStreamReader reader) {
        this.xpath = xpath;
        this.reader = reader;
    }
    //-----------------------------------------------------
    /**获取当前事件发生时所处的元素。*/
    public QName getCurrentElement() {
        return this.currentElement;
    }
    void setCurrentElement(final ElementTree currentElementTree) {
        if (currentElementTree != null) {
            this.currentElement = currentElementTree.getQname();
        }
    }
    /**获取当前事件所处的xpath。*/
    public String getXpath() {
        return this.xpath;
    }
    /**获取{@link XMLStreamReader}对象。*/
    public XMLStreamReader getReader() {
        return this.reader;
    }
    /**提供有关事件位置的信息。Location 提供的所有信息都是可选的。例如，应用程序可以只报告行号。*/
    public Location getLocation() {
        return this.reader.getLocation();
    }
    /**获取{@link NamespaceContext}对象。*/
    public NamespaceContext getNamespaceContext() {
        return this.reader.getNamespaceContext();
    }
    /**返回一个boolean值，该值表示是否跳过该事件的处理，如果被跳过的事件是{@link StartDocumentEvent}或{@link StartElementEvent}则也会跳过中间的所有事件流。*/
    public boolean isSkip() {
        return this.skip;
    }
    /**跳过当前事件，如果被跳过的事件是{@link StartDocumentEvent}或{@link StartElementEvent}则也会跳过中间的所有事件流。*/
    public void skip() {
        this.skip = true;
    }
    /**是否为一个全局事件，全局事件是指该事件会在所有注册的命名空间解析器上传播。*/
    public abstract boolean isPublicEvent();
    /**判断参数中的事件对象是否是与当前事件对象为一个拍档。例如：{@link StartDocumentEvent}和{@link EndDocumentEvent}是一对拍档，同一Xpath的{@link StartElementEvent}和{@link EndElementEvent}是一对拍档*/
    public abstract boolean isPartner(XmlStreamEvent e);
}