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
import org.more.RepeateException;
import org.more.StateException;
import org.more.core.xml.stream.AttributeEvent;
import org.more.core.xml.stream.EndDocumentEvent;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartDocumentEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.TextEvent;
import org.more.core.xml.stream.XmlAccept;
import org.more.core.xml.stream.XmlStreamEvent;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * <b>Level 2</b>：该级别的xml访问策略关注于xml元素或属性与命名空间的对应性，使用XmlParserKitManager
 * 可以专门用于访问某个命名空间下的元素。每个命名空间的解析器都是一个{@link XmlParserKit}类型对象。
 * 在使用Level 2级别访问xml的时，需要将命名空间与解析器对应起来。并且借助Level 1的工具进行扫描xml。
 * @version 2010-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlParserKitManager implements XmlAccept {
    /**注册的命名空间解析工具集*/
    private HashMap<String, ArrayList<XmlNamespaceParser>> regeditXmlParserKit = new HashMap<String, ArrayList<XmlNamespaceParser>>();
    /**活动的前缀与命名空间映射*/
    private XmlStackDecorator                              activateStack       = null;
    /**一个在分发xml事件流过程中一致存在的环境。*/
    private XmlStackDecorator                              context             = new XmlStackDecorator(new AttBase());
    /**获取环境对象，的{@link IAttribute}属性接口。*/
    public IAttribute getContext() {
        return context.getSource();
    }
    /**
     * 绑定某个命名空间处理器到一个解析器上。绑定的解析器可以用于监听到xml事件流信息。
     * 如果企图重复关联某个解析器与命名空间的对应关系则会引发{@link RepeateException}异常。
     * @param namespace 要绑定的命名空间。
     * @param kit 要关联的解析器。
     */
    public void regeditKit(String namespace, XmlNamespaceParser kit) throws RepeateException {
        if (namespace == null || kit == null)
            throw new NullPointerException("namespace，kit参数不能为空。");
        ArrayList<XmlNamespaceParser> list = null;
        if (this.regeditXmlParserKit.containsKey(namespace) == true)
            list = this.regeditXmlParserKit.get(namespace);
        else
            list = new ArrayList<XmlNamespaceParser>();
        if (list.contains(kit) == true)
            throw new RepeateException("命名空间[" + namespace + "]与解析器" + kit + "重复注册。");
        list.add(kit);
        this.regeditXmlParserKit.put(namespace, list);
    }
    /**
     * 解除注册使用regeditKit方法注册的namespace与{@link XmlParserKit}关联关系。
     * @param namespace 要解除绑定的命名空间。
     * @param kit 要解除关联的解析器。
     */
    public void unRegeditKit(String namespace, XmlNamespaceParser kit) {
        if (namespace == null || kit == null)
            throw new NullPointerException("namespace，kit参数不能为空。");
        if (this.regeditXmlParserKit.containsKey(namespace) == false)
            return;
        ArrayList<XmlNamespaceParser> list = this.regeditXmlParserKit.get(namespace);
        if (list.contains(kit) == true)
            list.remove(kit);
    }
    /**开始{@link XmlAccept}接口的调用，该方法主要用于重置状态。*/
    public void beginAccept() {
        this.activateStack = new XmlStackDecorator(new AttBase());
        for (ArrayList<XmlNamespaceParser> alList : this.regeditXmlParserKit.values())
            for (XmlNamespaceParser xnp : alList)
                xnp.beginAccept();
    }
    /**结束{@link XmlAccept}接口的调用。*/
    public void endAccept() {
        this.activateStack = null;
        for (ArrayList<XmlNamespaceParser> alList : this.regeditXmlParserKit.values())
            for (XmlNamespaceParser xnp : alList)
                xnp.endAccept();
    }
    /**实现{@link XmlAccept}接口用于接受事件的方法。*/
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
            ns.appendXPath(ee.getElementName(), false);
        } else if (e instanceof AttributeEvent) {
            AttributeEvent ee = (AttributeEvent) e;
            //(2).合成NameSpace专有的XPath
            String prefix = ee.getPrefix();
            NameSpace ns = (NameSpace) this.activateStack.getAttribute(prefix);
            ns.appendXPath(ee.getElementName(), true);
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
                ArrayList<XmlNamespaceParser> kitList = this.regeditXmlParserKit.get(atNS.getUri());
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
                ArrayList<XmlNamespaceParser> kitList = this.regeditXmlParserKit.get(namespace);
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
    /**该方法是用于分发事件到{@link XmlParserKit}解析器上。*/
    private void issueEvent(XmlStreamEvent rootEvent, String xpath, ArrayList<XmlNamespaceParser> parserKitList) {
        for (XmlNamespaceParser kit : parserKitList)
            kit.sendEvent(this.context, xpath, rootEvent);
    }
}