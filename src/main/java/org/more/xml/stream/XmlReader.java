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
package org.more.xml.stream;
import org.more.util.MatchUtils;
import org.more.xml.stream.TextEvent.Type;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import java.io.*;
/**
 * <b>Level 1</b>：数据访问策略。该类的功能是将xml数据流转换成为xml事件流。并且可以在扫描xml时执行xml的忽略策略。
 * @version 2010-9-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class XmlReader {
    private Reader  xmlReader     = null; //读取Xml数据的输入流。
    private boolean ignoreComment = true; //是否忽略Xml中的所有注释节点。
    private boolean ignoreSpace   = true; //是否忽略Xml中可忽略的空格。
    //--------------------------------------------------------------------
    /**创建一个XmlReader对象用于阅读fileName参数所表述的Xml文件。*/
    public XmlReader(final String fileName) throws FileNotFoundException {
        this.xmlReader = new FileReader(fileName);
    }
    /**创建一个XmlReader对象用于阅读file参数所表述的Xml文件。*/
    public XmlReader(final File file) throws FileNotFoundException {
        this.xmlReader = new FileReader(file);
    }
    /**创建一个XmlReader对象用于阅读xmlStrema参数所表述的Xml文件流。*/
    public XmlReader(final InputStream xmlStrema) throws UnsupportedEncodingException {
        if (xmlStrema == null) {
            throw new NullPointerException("InputStream类型参数为空。");
        }
        this.xmlReader = new InputStreamReader(xmlStrema);
    }
    /**创建一个XmlReader对象用于阅读xmlStrema参数所表述的Xml文件流。*/
    public XmlReader(final InputStream xmlStrema, final String encoding) throws UnsupportedEncodingException {
        if (xmlStrema == null) {
            throw new NullPointerException("InputStream类型参数为空。");
        }
        this.xmlReader = new InputStreamReader(xmlStrema, encoding);
    }
    /**创建一个XmlReader对象用于阅读xmlStrema参数所表述的Xml文件流。*/
    public XmlReader(final Reader xmlReader) {
        if (xmlReader == null) {
            throw new NullPointerException("Reader类型参数为空。");
        }
        this.xmlReader = xmlReader;
    }
    //--------------------------------------------------------------------
    /**返回一个boolean值，该值表示了是否忽略在读取XML期间发现的描述节点。返回true表示忽略，false表示不忽略。*/
    public boolean isIgnoreComment() {
        return this.ignoreComment;
    }
    /**设置一个boolean值，该值表示了是否忽略在读取XML期间发现的描述节点。true表示忽略，false表示不忽略。*/
    public void setIgnoreComment(final boolean ignoreComment) {
        this.ignoreComment = ignoreComment;
    }
    /**返回一个boolean值，该值表示了是否忽略在读取XML期间发现的可忽略的空格字符（参阅 [XML], 2.10 "White Space Handling"）。返回true表示忽略，false表示不忽略。*/
    public boolean isIgnoreSpace() {
        return this.ignoreSpace;
    }
    /**设置一个boolean值，该值表示了是否在读取XML期间忽略可忽略的空格字符（参阅 [XML], 2.10 "White Space Handling"）。true表示忽略，false表示不忽略。*/
    public void setIgnoreSpace(final boolean ignoreSpace) {
        this.ignoreSpace = ignoreSpace;
    }
    //--------------------------------------------------------------------
    /** 获取Stax阅读器的过滤器，子类可以通过该方法来扩展XmlReader在读取xml期间可以忽略的项目。*/
    protected StreamFilter getXmlStreamFilter() {
        return null;
    }
    ;
    /**
     * 该方法是用于决定两个XPath是否是一个包含的关系，该方法的返回值决定了解析器是否忽略这个xml条目。其子类可以重写它以完成更多的控制。
     * @param currentXPath 当前解析器扫描到的XPath。
     * @param testWild 表示打算忽略的XPath。
     * @return 返回一个boolean值，该值决定了是否忽略当前XPath条目。
     */
    protected boolean ignoreXPath(final String currentXPath, final String testWild) {
        if (testWild == null) {
            return false;
        }
        //XXX:XPath比较算法，比较currentXPath是否属于testXPath范围内的，目前使用的是?和*通配符。
        return MatchUtils.matchWild(testWild, currentXPath);
    }
    /**
     * 执行解析Xml文件，并且形成xml事件流。这些事件流被输入到{@link XmlAccept}类型对象中。
     * 如果配置了ignoreXPath参数则在形成事件流时XmlReader不会发送属于这个xpath的xml事件流。
     * @param accept 指定事件流接收对象。
     * @param ignoreXPath 指定要忽略的XPath路径。
     * @throws IOException
     */
    public synchronized void reader(final XmlAccept accept, final String ignoreXPath) throws XMLStreamException, IOException {
        if (accept == null) {
            return;
        }
        accept.beginAccept();
        //1.准备扫描的引擎。
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(this.xmlReader);
        StreamFilter filter = new NullStreamFilter(this, this.getXmlStreamFilter());
        reader = factory.createFilteredReader(reader, filter);
        //2.准备数据
        StringBuffer currentXPath = new StringBuffer("/");//XPath
        ElementTree currentElement = null;//设置当前事件所属的元素
        XmlStreamEvent currentEvent = null;
        //3.轮询推送事件流
        while (true) {
            //(1).拉出事件类型
            int xmlEvent = reader.getEventType();//当前事件对象
            //(2).生成事件对象
            switch (xmlEvent) {
            case XMLStreamConstants.START_DOCUMENT:
                //开始文档
                currentEvent = new StartDocumentEvent(currentXPath.toString(), reader);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                break;
            case XMLStreamConstants.END_DOCUMENT:
                //结束文档
                currentEvent = new EndDocumentEvent(currentXPath.toString(), reader);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                break;
            case XMLStreamConstants.START_ELEMENT:
                //开始元素
                if (currentXPath.indexOf("/") != currentXPath.length() - 1) {
                    currentXPath.append("/");
                }
                currentXPath.append(this.getName(reader.getName()));
                currentEvent = new StartElementEvent(currentXPath.toString(), reader);
                currentElement = new ElementTree(reader.getName(), currentElement);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                break;
            case XMLStreamConstants.END_ELEMENT:
                //结束元素
                currentEvent = new EndElementEvent(currentXPath.toString(), reader);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                int index = currentXPath.lastIndexOf("/");
                index = index == 0 ? 1 : index;
                currentXPath = currentXPath.delete(index, currentXPath.length());
                currentElement = currentElement.getParent();
                break;
            case XMLStreamConstants.COMMENT:
                //注释
                currentEvent = new TextEvent(currentXPath.toString(), reader, Type.Comment);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                break;
            case XMLStreamConstants.CDATA:
                //CDATA数据
                currentEvent = new TextEvent(currentXPath.toString(), reader, Type.CDATA);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                break;
            //---------------------------------------------
            case XMLStreamConstants.SPACE:
                //可以忽略的空格
                currentEvent = new TextEvent(currentXPath.toString(), reader, Type.Space);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                break;
            case XMLStreamConstants.CHARACTERS:
                //字符数据
                currentEvent = new TextEvent(currentXPath.toString(), reader, Type.Chars);
                currentEvent.setCurrentElement(currentElement);//设置当前元素
                break;
            }
            //(3).执行忽略
            if (xmlEvent == XMLStreamConstants.COMMENT && this.ignoreComment == true) {
                //执行忽略
                xmlEvent = this.readEvent(reader);
                continue;
            } else if (xmlEvent == XMLStreamConstants.SPACE && this.ignoreSpace == true) {
                //执行忽略
                xmlEvent = this.readEvent(reader);
                continue;
            }
            //(4).推送事件
            this.pushEvent(accept, currentEvent, ignoreXPath);
            if (xmlEvent == XMLStreamConstants.START_ELEMENT) {
                XmlStreamEvent elementEvent = currentEvent;
                int attCount = reader.getAttributeCount();
                for (int i = 0; i < attCount; i++) {
                    //推送属性事件
                    String namespace = reader.getAttributeNamespace(i);
                    String localName = reader.getAttributeLocalName(i);
                    String prefix = reader.getAttributePrefix(i);
                    //
                    namespace = namespace == null ? "" : namespace;
                    localName = localName == null ? "" : localName;
                    prefix = prefix == null ? "" : prefix;
                    //
                    QName qn = new QName(namespace, localName, prefix);
                    StringBuffer currentXPathTemp = new StringBuffer(currentXPath.toString());
                    currentXPathTemp.append("/@");
                    currentXPathTemp.append(this.getName(qn));
                    currentElement = new ElementTree(qn, currentElement);
                    currentEvent = new AttributeEvent(elementEvent, currentXPathTemp.toString(), reader, i);
                    currentEvent.setCurrentElement(currentElement.getParent());//将属性的当前节点设置成其所属的元素节点。
                    currentElement = currentElement.getParent();
                    this.pushEvent(accept, currentEvent, ignoreXPath);
                }
            }
            //(5).获取下一个xml文档流事件。
            xmlEvent = this.readEvent(reader);
            if (xmlEvent == 0) {
                break;
            }
        }
        //
        accept.endAccept();
    }
    private int readEvent(final XMLStreamReader reader) throws XMLStreamException {
        if (reader.hasNext() == false) {
            return 0;
        }
        return reader.next();
    }
    private String getName(final QName qname) {
        String prefix = qname.getPrefix();
        StringBuffer sb = new StringBuffer();
        if (prefix == null || prefix.equals("") == true) {
        } else {
            sb.append(prefix);
            sb.append(":");
        }
        return sb.append(qname.getLocalPart()).toString();
    }
    /**执行XPath忽略判断。 */
    private XmlStreamEvent skipEvent = null; //要跳过的事件
    private void pushEvent(final XmlAccept accept, final XmlStreamEvent e, final String ignoreXPath) throws XMLStreamException, IOException {
        //(1).XPath忽略“判断”
        boolean ignore = this.ignoreXPath(e.getXpath(), ignoreXPath);
        if (ignore == true) {
            return;
        }
        //(2).上一个标签跳过之后，接下来的所有标签都执行跳过“判断”
        if (this.skipEvent != null) {
            e.skip();
            if (this.skipEvent.isPartner(e) == true) {
                this.skipEvent = null;//如果当前标签和上一个跳过标签是兄弟关系， 那么skipEvent置空。
            }
        }
        //(3).执行跳过
        if (e.isSkip() == true) {
            return;
        }
        //(4).执行事件
        this.pushEvent(accept, e);
        //(5).接收跳过事件
        if (this.skipEvent == null) {
            if (e.isSkip() == true) {
                this.skipEvent = e;
            }
        }
    }
    /**负责推送事件的方法，子类可以通过扩展该方法在推送事件期间处理一些其他操作。 被忽略的和被跳过的事件将不会接受到该方法的调用。*/
    protected void pushEvent(final XmlAccept accept, final XmlStreamEvent e) throws XMLStreamException, IOException {
        if (accept != null) {
            accept.sendEvent(e);
        }
    }
}
/**
 * 该类的目的是可以不受空StreamFilter属性的影响。
 * @version 2010-9-8
 * @author 赵永春 (zyc@hasor.net)
 */
class NullStreamFilter implements StreamFilter {
    private StreamFilter parentFilter;
    public NullStreamFilter(final XmlReader reader, final StreamFilter parentFilter) {
        this.parentFilter = parentFilter;
    }
    @Override
    public boolean accept(final XMLStreamReader reader) {
        boolean accept = true;
        if (this.parentFilter != null) {
            accept = this.parentFilter.accept(reader);
        }
        return accept;
    }
}
class ElementTree {
    private QName       qname  = null;
    private ElementTree parent = null;
    public ElementTree(final QName qname, final ElementTree parent) {
        this.qname = qname;
        this.parent = parent;
    }
    public QName getQname() {
        return this.qname;
    }
    public ElementTree getParent() {
        return this.parent;
    }
}