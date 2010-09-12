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
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
/**
 * xml事件流的基类。
 * @version 2010-9-7
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class XmlStreamEvent {
    private String          xpath          = null; //当前事件所处的xpath
    private XMLStreamReader reader         = null; //底层的XMLStreamReader
    private XmlReader       xmlReader      = null;
    private QName           currentElement = null;
    //-----------------------------------------------------
    public XmlStreamEvent(String xpath, XmlReader xmlReader, XMLStreamReader reader) {
        this.xpath = xpath;
        this.reader = reader;
        this.xmlReader = xmlReader;
    }
    //-----------------------------------------------------
    /**获取当前事件发生时所处的元素。*/
    public QName getCurrentElement() {
        return currentElement;
    }
    void setCurrentElement(ElementTree currentElementTree) {
        if (currentElementTree != null)
            this.currentElement = currentElementTree.getQname();
    }
    /**获取当前事件所处的xpath。*/
    public String getXpath() {
        return this.xpath;
    }
    /**获取{@link XMLStreamReader}对象。*/
    protected XMLStreamReader getReader() {
        return this.reader;
    }
    /**提供有关事件位置的信息。Location 提供的所有信息都是可选的。例如，应用程序可以只报告行号。*/
    public Location getLocation() {
        return this.reader.getLocation();
    }
    /**获取产生该事件的{@link XmlReader}对象*/
    public XmlReader getXmlReader() {
        return xmlReader;
    }
    /**获取{@link NamespaceContext}对象。*/
    public NamespaceContext getNamespaceContext() {
        return this.reader.getNamespaceContext();
    }
}