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
package org.more.core.xml;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.more.StateException;
import org.more.core.xml.stream.XmlAccept;
import org.more.core.xml.stream.XmlStreamEvent;
import org.more.core.xml.stream.event.AttributeEvent;
import org.more.core.xml.stream.event.EndDocumentEvent;
import org.more.core.xml.stream.event.EndElementEvent;
import org.more.core.xml.stream.event.StartDocumentEvent;
import org.more.core.xml.stream.event.StartElementEvent;
import org.more.core.xml.stream.event.TextEvent;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.StackDecorator;
/**
 *
 * @version 2010-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlParserKitManager implements XmlAccept {
    private HashMap<String, ArrayList<XmlParserKit>> regeditXmlParserKit = new HashMap<String, ArrayList<XmlParserKit>>(); //注册的命名空间解析工具集
    private StackDecorator                           activateStack       = null;                                          //活动的前缀与命名空间映射。
    /**
     *
     * @param namespace
     * @param kit
     */
    public void regeditKit(String namespace, XmlParserKit kit) {
        ArrayList<XmlParserKit> list = null;
        if (this.regeditXmlParserKit.containsKey(namespace) == true)
            list = this.regeditXmlParserKit.get(namespace);
        else
            list = new ArrayList<XmlParserKit>();
        list.add(kit);
        this.regeditXmlParserKit.put(namespace, list);
    }
    /**
     *
     * @param namespace
     * @param kit
     */
    public void unRegeditKit(String namespace, XmlParserKit kit) {
        if (this.regeditXmlParserKit.containsKey(namespace) == false)
            return;
        ArrayList<XmlParserKit> list = this.regeditXmlParserKit.get(namespace);
        list.remove(kit);
    }
    public void reset() {
        this.activateStack = new StackDecorator(new AttBase());
    }
    /***/
    public void sendEvent(XmlStreamEvent e) {
        //1.创建堆栈，激活命名空间处理器。
        if (e instanceof StartElementEvent) {
            StartElementEvent ee = (StartElementEvent) e;
            int nsCount = ee.getNamespaceCount();
            this.activateStack.createStack();//创建一个堆栈
            for (int i = 0; i < nsCount; i++) {
                String prefix = ee.getNamespacePrefix(i);
                String uri = ee.getNamespaceURI(i);
                prefix = (prefix == null) ? "" : prefix;
                this.activateStack.setAttribute(prefix, new NameSpace(uri, "/"));
            }
        }
        //2.合成NameSpace专有的XPath
        if (e instanceof StartElementEvent) {
            StartElementEvent ee = (StartElementEvent) e;
            //(2).合成NameSpace专有的XPath
            String prefix = ee.getPrefix();
            NameSpace ns = (NameSpace) this.activateStack.getAttribute(prefix);
            ns.appendXPath(ee.getName(), false);
        } else if (e instanceof AttributeEvent) {
            AttributeEvent ee = (AttributeEvent) e;
            //(2).合成NameSpace专有的XPath
            String prefix = ee.getPrefix();
            NameSpace ns = (NameSpace) this.activateStack.getAttribute(prefix);
            ns.appendXPath(ee.getName(), true);
        }
        //3.确定事件传播的范围.
        boolean isPublic = false;
        if (e instanceof StartDocumentEvent)
            isPublic = true;
        else if (e instanceof EndDocumentEvent)
            isPublic = true;
        else if (e instanceof TextEvent) {
            TextEvent ee = (TextEvent) e;
            if (ee.isCommentEvent() == true)
                isPublic = true;
        }
        //4.分发事件--私有
        if (isPublic == false) {
            QName qname = e.getCurrentElement();
            String prefix = qname.getPrefix();
            prefix = (prefix == null) ? "" : prefix;
            NameSpace atNS = (NameSpace) this.activateStack.getAttribute(prefix);
            if (atNS != null) {
                ArrayList<XmlParserKit> kitList = this.regeditXmlParserKit.get(atNS.getUri());
                if (kitList != null)
                    this.issueEvent(e, atNS.getXpath(), kitList);
            }
        }
        //4.分发事件--共有
        if (isPublic == true) {
            for (String namespace : this.regeditXmlParserKit.keySet()) {
                QName currentElement = e.getCurrentElement();
                String xpath = null;
                if (currentElement == null)
                    xpath = "/";//必定是 开始文档或者结束文档事件。
                else {
                    String prefix = currentElement.getPrefix();
                    NameSpace atNS = (NameSpace) this.activateStack.getAttribute(prefix);
                    if (atNS == null)
                        throw new StateException("解析错误，前缀[" + prefix + "]代表的命名空间没有被激活。");
                    xpath = atNS.getXpath();
                }
                ArrayList<XmlParserKit> kitList = this.regeditXmlParserKit.get(namespace);
                this.issueEvent(e, xpath, kitList);
            }
        }
        //5.合成NameSpace专有的XPath
        String prefix = null;
        if (e instanceof EndElementEvent) {
            EndElementEvent ee = (EndElementEvent) e;
            prefix = ee.getPrefix();
        } else if (e instanceof AttributeEvent) {
            AttributeEvent ee = (AttributeEvent) e;
            prefix = ee.getPrefix();
        }
        if (prefix != null) {
            NameSpace ns = (NameSpace) this.activateStack.getAttribute(prefix);
            ns.removeXPath();
        }
        //6.销毁堆栈，钝化命名空间处理器。
        if (e instanceof EndElementEvent)
            this.activateStack.dropStack();//销毁堆栈
    }
    /**
     *
     * @param rootEvent
     * @param xpath
     * @param parserKitList
     */
    protected void issueEvent(XmlStreamEvent rootEvent, String xpath, ArrayList<XmlParserKit> parserKitList) {
        for (XmlParserKit kit : parserKitList)
            kit.sendEvent(xpath, rootEvent);
    }
}