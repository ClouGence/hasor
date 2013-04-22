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
package org.more.global.assembler.xml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.more.global.GlobalProperty;
import org.more.util.StringConvertUtil;
/**
 * XmlProperty, GlobalProperty接口实现类。
 * @version : 2013-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class XmlPropertyImpl implements XmlProperty, GlobalProperty {
    private String                  elementName = null;
    private String                  textString  = null;
    private HashMap<String, String> arrMap      = new HashMap<String, String>();
    private List<XmlProperty>       children    = new ArrayList<XmlProperty>();
    //
    //
    public XmlPropertyImpl(String elementName) {
        this.elementName = elementName;
    }
    public void addAttribute(String attName, String attValue) {
        arrMap.put(attName, attValue);
    }
    public void addChildren(XmlPropertyImpl xmlProperty) {
        this.children.add(xmlProperty);
    }
    public void setText(String textString) {
        this.textString = textString;
    }
    @Override
    public String getName() {
        return elementName;
    }
    @Override
    public String getText() {
        return textString;
    }
    @Override
    public List<XmlProperty> getChildren() {
        return children;
    }
    @Override
    public String getXmlText() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<" + this.elementName);
        if (arrMap.size() > 0)
            strBuilder.append(" ");
        //
        for (Entry<String, String> attEnt : this.arrMap.entrySet()) {
            strBuilder.append(attEnt.getKey() + "=" + "\"" + attEnt.getValue() + "\"");
        }
        strBuilder.append(">");
        //
        for (XmlProperty xmlEnt : this.children) {
            strBuilder.append(xmlEnt.getXmlText());
        }
        //
        if (this.textString != null)
            strBuilder.append(this.textString);
        //
        strBuilder.append("</" + this.elementName + ">");
        return strBuilder.toString();
    }
    @Override
    public String toString() {
        return this.getText();
    }
    @Override
    public <T> T getValue(Class<T> toType, T defaultValue) {
        if (XmlProperty.class.isAssignableFrom(toType) == true)
            return (T) this;
        if (GlobalProperty.class.isAssignableFrom(toType) == true)
            return (T) this;
        return StringConvertUtil.changeType(this.getText(), toType, defaultValue);
    }
    @Override
    public Map<String, String> getAttributeMap() {
        return this.arrMap;
    }
}