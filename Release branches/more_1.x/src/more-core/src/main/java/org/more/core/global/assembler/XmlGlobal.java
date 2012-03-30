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
package org.more.core.global.assembler;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.more.core.xml.XmlAttributeHook;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.XmlTextHook;
import org.more.core.xml.register.XmlRegister;
import org.more.core.xml.stream.AttributeEvent;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.TextEvent;
import org.more.core.xml.stream.XmlReader;
import org.more.util.attribute.DecParentAttribute;
import org.more.util.attribute.IAttribute;
/**
* 解析Xml
* @version : 2011-9-3
* @author 赵永春 (zyc@byshell.org)
*/
public class XmlGlobal extends AbstractGlobalFactory {
    //    public static class XmlGlobalConfig extends Attribute<String> {
    //        /**除了默认命名空间被装载之外还需要装载的命名空间（数组形式）*/
    //        public final String LoadXml_NameSpace  = "LoadXml_NameSpace";
    //    };
    /**默认使用的命名空间。*/
    public static final String DefaultNameSpace    = "http://project.byshell.org/more/schema/global";
    private HashSet<String>    loadNameSpace       = new HashSet<String>();
    private boolean            isIgnoreRootElement = false;
    //
    public Map<String, Object> createProperties(InputStream stream, String encoding) throws IOException, XMLStreamException {
        if (loadNameSpace.contains(loadNameSpace) == false)
            loadNameSpace.add(DefaultNameSpace);
        HashMap<String, Object> xmlTree = new HashMap<String, Object>();
        XmlParserKit kit = new XmlParserKit();
        kit.regeditHook("/*", new Config_ElementHook(this.isIgnoreRootElement));
        XmlRegister xmlRegister = new XmlRegister(xmlTree);
        for (String ns : loadNameSpace)
            xmlRegister.regeditKit(ns, kit);
        new XmlReader(stream).reader(xmlRegister, encoding, null);
        return xmlTree;
    }
    /**获取要装载的命名空间集合。*/
    public HashSet<String> getLoadNameSpace() {
        return this.loadNameSpace;
    }
    /**判断在解析xml的时候是否放弃根节点。*/
    public boolean isIgnoreRootElement() {
        return isIgnoreRootElement;
    }
    /**设置在解析xml的时候是否放弃根节点。*/
    public void setIgnoreRootElement(boolean isIgnoreRootElement) {
        this.isIgnoreRootElement = isIgnoreRootElement;
    };
};
class Config_ElementHook implements XmlElementHook, XmlAttributeHook, XmlTextHook {
    private boolean isIgnoreRootElement = false;
    public Config_ElementHook(boolean isIgnoreRootElement) {
        this.isIgnoreRootElement = isIgnoreRootElement;
    }
    private String getElementPath(XmlStackDecorator<Object> context) {
        StringBuffer sb = new StringBuffer();
        int count = context.getDepth();
        int beginI = 0;
        //决定是否忽略根
        if (this.isIgnoreRootElement == true)
            beginI = 1;
        //
        for (; beginI < count; beginI++) {
            DecParentAttribute<Object> $att = (DecParentAttribute<Object>) context.getParentStack(count - beginI);
            IAttribute<Object> att = $att.getSource();//拿到Source对象。
            QName qname = (QName) att.getAttribute("QName");
            //
            sb.append(qname.getLocalPart() + ".");
            if (att.contains("id") == true)
                sb.append(att.getAttribute("id") + ".");
        }
        return sb.toString();
    }
    public void putConfig(String key, String value, XmlStackDecorator<Object> context) {
        String $value = value.trim();
        if ($value == null || $value.equals("") == true)
            return;
        String $key = key.substring(0, key.length() - 1);
        //
        HashMap<String, Object> xmlTree = (HashMap<String, Object>) context.getContext();
        xmlTree.put($key, $value);
    }
    //
    //
    //
    //
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException {
        String id = event.getAttributeValue("id");
        if (id != null)
            context.setAttribute("id", id);
        context.setAttribute("@Value", new StringBuffer(""));
        context.setAttribute("QName", event.getName());
    };
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException {
        //1.拼成属性名
        String key = getElementPath(context);
        StringBuffer value = (StringBuffer) context.getAttribute("@Value");
        //2.输出
        this.putConfig(key, value.toString(), context);
    };
    public void attribute(XmlStackDecorator<Object> context, String xpath, AttributeEvent event) throws XMLStreamException, IOException {
        context.setAttribute("QName", event.getName());
        //1.拼成属性名
        String key = getElementPath(context);
        String value = event.getValue();
        if ("id".equals(event.getElementName().toLowerCase()) == true)
            key = key + "id.";
        //2.输出
        this.putConfig(key, value.toString(), context);
    };
    public void text(XmlStackDecorator<Object> context, String xpath, TextEvent event) throws XMLStreamException, IOException {
        StringBuffer sb = (StringBuffer) context.getAttribute("@Value");
        sb.append(event.getTrimText());
    };
};