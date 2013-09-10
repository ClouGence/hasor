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
package net.hasor.core.setting.xml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.GlobalProperty;
import org.more.convert.ConverterUtils;
/**
 * XmlProperty, GlobalProperty接口实现类。
 * @version : 2013-4-22
 * @author 赵永春 (zyc@hasor.net)
 */
class DefaultXmlProperty implements XmlNode, GlobalProperty {
    private String                  elementName       = null;
    private String                  textString        = null;
    private HashMap<String, String> arrMap            = new HashMap<String, String>();
    private List<XmlNode>           children          = new ArrayList<XmlNode>();
    private XmlNode                 parentXmlProperty = null;
    //
    //
    public DefaultXmlProperty(XmlNode parentXmlProperty, String elementName) {
        this.parentXmlProperty = parentXmlProperty;
        this.elementName = elementName;
    }
    public void addAttribute(String attName, String attValue) {
        arrMap.put(attName, attValue);
    }
    public void addChildren(DefaultXmlProperty xmlProperty) {
        this.children.add(xmlProperty);
    }
    public void setText(String textString) {
        this.textString = textString;
    }
    public String getName() {
        return elementName;
    }
    public String getText() {
        return textString;
    }
    public List<XmlNode> getChildren() {
        return children;
    }
    public String getXmlText() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<" + this.elementName);
        if (arrMap.size() > 0) {
            strBuilder.append(" ");
            for (Entry<String, String> attEnt : this.arrMap.entrySet()) {
                strBuilder.append(attEnt.getKey() + "=" + "\"");
                String attVal = attEnt.getValue();
                attVal = attVal.replace("<", "&lt;");//小于号
                attVal = attVal.replace(">", "&gt;");//大于号
                attVal = attVal.replace("'", "&apos;");//'单引号
                attVal = attVal.replace("\"", "&quot;");//'双引号
                attVal = attVal.replace("&", "&amp;");//& 和
                strBuilder.append(attVal + "\" ");
            }
            strBuilder.deleteCharAt(strBuilder.length() - 1);
        }
        strBuilder.append(">");
        //
        for (XmlNode xmlEnt : this.children) {
            String xmlText = new String(xmlEnt.getXmlText());
            xmlText.replace("<", "&lt;");
            xmlText.replace(">", "&gt;");
            xmlText.replace("&", "&amp;");
            strBuilder.append(xmlText);
        }
        //
        if (this.textString != null)
            strBuilder.append(this.getText());
        //
        strBuilder.append("</" + this.elementName + ">");
        return strBuilder.toString();
    }
    public String toString() {
        return this.getXmlText();
    }
    public DefaultXmlProperty clone() {
        DefaultXmlProperty newData = new DefaultXmlProperty(this.parentXmlProperty, this.elementName);
        newData.arrMap.putAll(this.arrMap);
        newData.textString = this.textString;
        if (children != null)
            for (XmlNode xmlProp : this.children) {
                DefaultXmlProperty newClone = ((DefaultXmlProperty) xmlProp).clone();
                newClone.setParent(newData);
                newData.children.add(newClone);
            }
        return newData;
    }
    public <T> T getValue(Class<T> toType, T defaultValue) {
        if (XmlNode.class.isAssignableFrom(toType) == true)
            return (T) this;
        if (GlobalProperty.class.isAssignableFrom(toType) == true)
            return (T) this;
        try {
            T returnData = (T) ConverterUtils.convert(toType, this.getText());
            return returnData == null ? defaultValue : returnData;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public Map<String, String> getAttributeMap() {
        return this.arrMap;
    }
    public XmlNode getParent() {
        return parentXmlProperty;
    }
    public void setParent(XmlNode parentXmlProperty) {
        this.parentXmlProperty = parentXmlProperty;
    }
}