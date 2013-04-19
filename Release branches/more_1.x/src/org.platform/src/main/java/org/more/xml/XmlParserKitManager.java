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
package org.more.xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.more.core.error.MoreStateException;
import org.more.core.error.RepeateException;
import org.more.xml.stream.AttributeEvent;
import org.more.xml.stream.EndElementEvent;
import org.more.xml.stream.StartElementEvent;
import org.more.xml.stream.XmlAccept;
import org.more.xml.stream.XmlStreamEvent;
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
    private XmlStackDecorator<Object>                      activateStack       = null;
    //
    //
    private XmlStackDecorator<Object> getXmlStack() {
        if (this.activateStack == null)
            this.activateStack = new XmlStackDecorator<Object>();
        return this.activateStack;
    }
    //    /**获取环境对象，的{@link IAttribute}属性接口。*/
    //    public IAttribute<Object> getAttContext() {
    //        return getXmlStack().get.getSource();
    //    }
    /**设置绑定的上下文对象*/
    public void setContext(Object context) {
        getXmlStack().setContext(context);
    }
    /**获取绑定的上下文对象*/
    public Object getContext() {
        return getXmlStack().getContext();
    }
    /**检测一个命名空间处理器是否已经绑定到某个命名空间上。*/
    public boolean isRegeditKit(String namespace, XmlNamespaceParser kit) {
        if (this.regeditXmlParserKit.containsKey(namespace) == false)
            return false;
        ArrayList<XmlNamespaceParser> parserList = this.regeditXmlParserKit.get(namespace);
        return parserList.contains(kit);
    }
    /**
     * 绑定某个命名空间处理器到一个解析器上。绑定的解析器可以用于监听到xml事件流信息。
     * 如果企图重复关联某个解析器与命名空间的对应关系则会引发{@link RepeateException}异常。
     * @param namespace 要绑定的命名空间。
     * @param kit 要关联的解析器。
     */
    public void regeditKit(String namespace, XmlNamespaceParser kit) {
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
    /**获取注册的命名空间集合*/
    public Set<String> getNamespace() {
        return Collections.unmodifiableSet(this.regeditXmlParserKit.keySet());
    }
    /**获取指定命名空间下已注册的解析器集合。*/
    public List<XmlNamespaceParser> getXmlNamespaceParser(String namespace) {
        List<XmlNamespaceParser> parserList = this.regeditXmlParserKit.get(namespace);
        if (parserList == null)
            return Collections.unmodifiableList(new ArrayList<XmlNamespaceParser>());
        else
            return Collections.unmodifiableList(parserList);
    }
    /**开始{@link XmlAccept}接口的调用，该方法主要用于重置状态。*/
    public void beginAccept() {
        this.getXmlStack();
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
    /*--------------------------------------------------------------*/
    /**分发事件到{@link XmlParserKit}列表*/
    private void issueEvent(XmlStreamEvent e, XmlStackDecorator<Object> activateStack) throws XMLStreamException, IOException {
        //公共事件
        if (e.isPublicEvent() == true)
            for (String namespace : this.regeditXmlParserKit.keySet()) {
                QName currentElement = e.getCurrentElement();
                String xpath = null;
                if (currentElement == null)
                    xpath = "/";//必定是 开始文档或者结束文档事件。
                else {
                    String prefix = currentElement.getPrefix();
                    NameSpace ns = (NameSpace) this.activateStack.get(prefix);
                    if (ns == null)
                        throw new MoreStateException("解析错误，前缀[" + prefix + "]代表的命名空间没有被激活。");
                    xpath = ns.getXpath();
                }
                ArrayList<XmlNamespaceParser> alList = this.regeditXmlParserKit.get(namespace);
                if (alList != null)
                    for (XmlNamespaceParser kit : alList)
                        kit.sendEvent(this.getXmlStack(), xpath, e);
            }
        else {
            //处理私有事件
            String prefix = e.getCurrentElement().getPrefix();
            prefix = (prefix == null) ? "" : prefix;
            NameSpace ns = activateStack.getNameSpace(prefix);
            ArrayList<XmlNamespaceParser> alList = this.regeditXmlParserKit.get(ns.getUri());
            if (alList != null)
                for (XmlNamespaceParser kit : alList)
                    kit.sendEvent(this.getXmlStack(), ns.getXpath(), e);
        }
    }
    /**实现{@link XmlAccept}接口用于接受事件的方法。*/
    public synchronized void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
        //1.处理StartElementEvent
        if (e instanceof StartElementEvent) {
            this.activateStack.createStack();
            StartElementEvent ee = (StartElementEvent) e;
            //
            int nsCount = ee.getNamespaceCount();
            for (int i = 0; i < nsCount; i++) {
                String prefix = ee.getNamespacePrefix(i);
                String uri = ee.getNamespaceURI(i);
                prefix = (prefix == null) ? "" : prefix;
                NameSpace ns = this.activateStack.getNameSpace(prefix);
                if (ns == null)
                    //激活新的命名空间
                    this.activateStack.addNameSpace(prefix, new NameSpace(uri, "/"));
            }
            //生成当前节点的xpath（专属命名空间下的xpath）
            String prefix = ee.getPrefix();
            prefix = (prefix == null) ? "" : prefix;
            NameSpace ns = this.activateStack.getNameSpace(prefix);
            ns.appendXPath(ee.getElementName(), false);
            this.issueEvent(e, this.activateStack);
            //如果执行的是跳过则删除其加入的xpath，因为reader不会再发送其end事件。
            if (e.isSkip() == true)
                ns.removeXPath();
            return;
        } else
        //2.处理EndElementEvent
        if (e instanceof EndElementEvent) {
            EndElementEvent ee = (EndElementEvent) e;
            NameSpace ns = this.activateStack.getNameSpace(ee.getPrefix());
            this.issueEvent(e, this.activateStack);
            ns.removeXPath();
            this.activateStack.dropStack();
            return;
        } else
        //3.处理AttributeEvent
        if (e instanceof AttributeEvent) {
            this.activateStack.createStack();
            AttributeEvent ee = (AttributeEvent) e;
            String prefix = ee.getName().getPrefix();
            prefix = (prefix == null) ? "" : prefix;
            if (prefix.equals("") == true)
                prefix = ee.getCurrentElement().getPrefix();
            prefix = (prefix == null) ? "" : prefix;
            //
            NameSpace ns = this.activateStack.getNameSpace(prefix);
            ns.appendXPath(ee.getElementName(), true);
            this.issueEvent(e, this.activateStack);
            ns.removeXPath();
            this.activateStack.dropStack();
            return;
        } else
            //4.分发事件
            this.issueEvent(e, this.activateStack);
    }
}