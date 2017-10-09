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
package net.hasor.core.setting.xml;
import net.hasor.core.Hasor;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.FieldProperty;
import net.hasor.utils.convert.ConverterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * XmlNode, GlobalProperty 接口实现类。
 * @version : 2013-4-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultXmlNode implements XmlNode, FieldProperty, Cloneable {
    private String              elementName       = null;
    private String              textString        = null;
    private Map<String, String> arrMap            = new HashMap<String, String>();
    private List<XmlNode>       children          = new ArrayList<XmlNode>();
    private XmlNode             parentXmlProperty = null;
    //
    //
    public DefaultXmlNode(final XmlNode parentXmlProperty, final String elementName) {
        this.parentXmlProperty = parentXmlProperty;
        this.elementName = elementName;
    }
    @Override
    public String getName() {
        return this.elementName;
    }
    @Override
    public String getText() {
        return this.textString;
    }
    public void setText(final String textString) {
        this.textString = textString;
    }
    //
    //
    @Override
    public List<XmlNode> getChildren() {
        return this.children;
    }
    @Override
    public List<XmlNode> getChildren(final String elementName) {
        Hasor.assertIsNotNull(elementName);
        List<XmlNode> children = new ArrayList<XmlNode>();
        for (XmlNode xmlItem : this.children) {
            if (elementName.equalsIgnoreCase(xmlItem.getName())) {
                children.add(xmlItem);
            }
        }
        return children;
    }
    @Override
    public XmlNode getOneChildren(final String elementName) {
        List<XmlNode> subItems = this.getChildren(elementName);
        return subItems.isEmpty() ? null : subItems.get(0);
    }
    public void addChildren(final DefaultXmlNode xmlProperty) {
        this.children.add(xmlProperty);
    }
    //
    //
    @Override
    public Map<String, String> getAttributeMap() {
        return this.arrMap;
    }
    @Override
    public String getAttribute(final String attName) {
        return this.getAttributeMap().get(attName);
    }
    public void addAttribute(final String attName, final String attValue) {
        this.arrMap.put(attName, attValue);
    }
    //
    //
    public XmlNode getParent() {
        return this.parentXmlProperty;
    }
    public void setParent(final XmlNode parentXmlProperty) {
        this.parentXmlProperty = parentXmlProperty;
    }
    //
    //
    @Override
    public String toString() {
        return this.getXmlText();
    }
    @Override
    public DefaultXmlNode clone() {
        DefaultXmlNode newData = new DefaultXmlNode(this.parentXmlProperty, this.elementName);
        newData.arrMap.putAll(this.arrMap);
        newData.textString = this.textString;
        if (this.children != null) {
            for (XmlNode xmlProp : this.children) {
                DefaultXmlNode newClone = ((DefaultXmlNode) xmlProp).clone();
                newClone.setParent(newData);
                newData.children.add(newClone);
            }
        }
        return newData;
    }
    //
    //
    @Override
    public <T> T getValue(final Class<T> toType, final T defaultValue) {
        if (XmlNode.class.isAssignableFrom(toType)) {
            return (T) this;
        }
        if (FieldProperty.class.isAssignableFrom(toType)) {
            return (T) this;
        }
        try {
            T returnData = (T) ConverterUtils.convert(toType, this.getText());
            return returnData == null ? defaultValue : returnData;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    @Override
    public String getXmlText() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<").append(this.elementName);
        if (this.arrMap.size() > 0) {
            strBuilder.append(" ");
            for (Entry<String, String> attEnt : this.arrMap.entrySet()) {
                strBuilder.append(attEnt.getKey()).append("=").append("\"");
                String attVal = attEnt.getValue();
                attVal = attVal.replace("<", "&lt;");//小于号
                attVal = attVal.replace(">", "&gt;");//大于号
                attVal = attVal.replace("'", "&apos;");//'单引号
                attVal = attVal.replace("\"", "&quot;");//'双引号
                attVal = attVal.replace("&", "&amp;");//& 和
                strBuilder.append(attVal).append("\" ");
            }
            strBuilder.deleteCharAt(strBuilder.length() - 1);
        }
        strBuilder.append(">");
        //
        for (XmlNode xmlEnt : this.children) {
            String xmlText = xmlEnt.getXmlText();
            xmlText = xmlText.replace("<", "&lt;");
            xmlText = xmlText.replace(">", "&gt;");
            xmlText = xmlText.replace("&", "&amp;");
            strBuilder.append(xmlText);
        }
        //
        if (this.textString != null) {
            strBuilder.append(this.getText());
        }
        //
        strBuilder.append("</").append(this.elementName).append(">");
        return strBuilder.toString();
    }
}