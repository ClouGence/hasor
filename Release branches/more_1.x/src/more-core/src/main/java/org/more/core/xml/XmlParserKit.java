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
import org.more.core.error.RepeateException;
import org.more.core.xml.stream.AttributeEvent;
import org.more.core.xml.stream.EndDocumentEvent;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartDocumentEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.TextEvent;
import org.more.core.xml.stream.XmlStreamEvent;
import org.more.util.StringUtil;
/**
 *  <b>Level 3</b>：该级别是基于级别2的增强，该级别的特点是可以将某个xpath所表示元素与其处理器{@link XmlParserHook}进行绑定。
 *  这个绑定由于是基于Level 2因此不会与其他命名空间的同名元素相混淆。
 * @version 2010-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlParserKit implements XmlNamespaceParser {
    private HashMap<String, ArrayList<XmlParserHook>> hooks = new HashMap<String, ArrayList<XmlParserHook>>();
    //----------------------------------------------------
    /**注册一组{@link XmlParserHook}接口对象到一个指定的Xpath上，如果注册的是{@link XmlDocumentHook}接口对象则务必将xpath填写为"/"否则可能导致接收不到事件的现象。*/
    public void regeditHook(String[] xpath, XmlParserHook hook) {
        if (xpath != null && hook != null)
            for (String s : xpath)
                this.regeditHook(s, hook);
    }
    /**注册一个{@link XmlParserHook}接口对象到一个指定的Xpath上，如果注册的是{@link XmlDocumentHook}接口对象则务必将xpath填写为"/"否则可能导致接收不到事件的现象。*/
    public void regeditHook(String xpath, XmlParserHook hook) {
        //2.检查是否已经存在的注册。
        ArrayList<XmlParserHook> arrayList = this.hooks.get(xpath);
        if (arrayList == null)
            arrayList = new ArrayList<XmlParserHook>();
        if (arrayList.contains(hook) == true)
            throw new RepeateException(xpath + "：路径上重复绑定同一个XmlParserHook。");
        arrayList.add(hook);
        this.hooks.put(xpath, arrayList);
    };
    /**该方法是解除使用regeditHook()方法注册的一组关联。*/
    public void unRegeditHook(String[] xpath, XmlParserHook hook) {
        for (String s : xpath)
            this.unRegeditHook(s, hook);
    }
    /**该方法是解除使用regeditHook()方法注册的关联。*/
    public void unRegeditHook(String xpath, XmlParserHook hook) {
        ArrayList<XmlParserHook> arrayList = this.hooks.get(xpath);
        if (arrayList == null)
            return;
        if (arrayList.contains(hook) == true)
            arrayList.remove(hook);
    }
    //----------------------------------------------------
    public void beginAccept() {}
    public void endAccept() {}
    private ArrayList<XmlParserHook> getHooks(String xpath) {
        String xpath2 = xpath;
        for (String xp : this.hooks.keySet())
            if (StringUtil.matchWild(xp, xpath2) == true) {
                xpath2 = xp;
                break;
            }
        return this.hooks.get(xpath2);
    };
    /** */
    public void sendEvent(XmlStackDecorator<Object> context, String xpath, XmlStreamEvent event) {
        ArrayList<XmlParserHook> hooks = this.getHooks(xpath);
        if (hooks == null)
            return;
        //-----------
        if (event instanceof StartDocumentEvent) {
            //分发文档开始事件
            for (XmlParserHook hook : hooks)
                if (hook instanceof XmlDocumentHook)
                    ((XmlDocumentHook) hook).beginDocument(context, (StartDocumentEvent) event);
        } else if (event instanceof EndDocumentEvent) {
            //分发文档结束事件
            for (XmlParserHook hook : hooks)
                if (hook instanceof XmlDocumentHook)
                    ((XmlDocumentHook) hook).endDocument(context, (EndDocumentEvent) event);
        } else if (event instanceof StartElementEvent) {
            //分发元素开始事件
            for (XmlParserHook hook : hooks)
                if (hook instanceof XmlElementHook)
                    ((XmlElementHook) hook).beginElement(context, xpath, (StartElementEvent) event);
        } else if (event instanceof EndElementEvent) {
            //分发元素结束事件
            for (XmlParserHook hook : hooks)
                if (hook instanceof XmlElementHook)
                    ((XmlElementHook) hook).endElement(context, xpath, (EndElementEvent) event);
        } else if (event instanceof TextEvent) {
            //分发文本事件
            for (XmlParserHook hook : hooks)
                if (hook instanceof XmlTextHook)
                    ((XmlTextHook) hook).text(context, xpath, (TextEvent) event);
        } else if (event instanceof AttributeEvent) {
            //分发属性事件
            for (XmlParserHook hook : hooks)
                if (hook instanceof XmlAttributeHook)
                    ((XmlAttributeHook) hook).attribute(context, xpath, (AttributeEvent) event);
        }
    }
}