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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * 
 * @version : 2013-7-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class SaxXmlParser extends DefaultHandler {
    private Settings                    dataContainer     = null;
    private Map<String, StringBuffer>   xmlText           = new HashMap<String, StringBuffer>();
    private Map<String, DefaultXmlNode> currentXmlPropert = new HashMap<String, DefaultXmlNode>();
    //
    public SaxXmlParser(final Settings dataContainer) {
        this.dataContainer = dataContainer;
    }
    private StringBuffer getText(final String xmlns) {
        if (this.xmlText.containsKey(xmlns) == false) {
            this.xmlText.put(xmlns, new StringBuffer(""));
        }
        return this.xmlText.get(xmlns);
    }
    private void cleanText(final String xmlns) {
        this.xmlText.remove(xmlns);
    }
    private DefaultXmlNode getCurrentXmlPropert(final String xmlns) {
        return this.currentXmlPropert.get(xmlns);
    }
    private void setCurrentXmlPropert(final String xmlns, final DefaultXmlNode xmlProperty) {
        this.currentXmlPropert.put(xmlns, xmlProperty);
    }
    //
    private String curXmlns = null;
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        DefaultXmlNode xmlProperty = this.getCurrentXmlPropert(uri);
        if (xmlProperty == null) {
            xmlProperty = new DefaultXmlNode(null, "root");
            this.setCurrentXmlPropert(uri, xmlProperty);
        }
        DefaultXmlNode thisXmlNode = new DefaultXmlNode(xmlProperty, localName);
        xmlProperty.addChildren(thisXmlNode);
        this.setCurrentXmlPropert(uri, thisXmlNode);
        //
        for (int i = 0; i < attributes.getLength(); i++) {
            String attName = attributes.getLocalName(i);
            String attValue = attributes.getValue(i);
            this.getCurrentXmlPropert(uri).addAttribute(attName, attValue);
        }
        this.curXmlns = uri;
    }
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        StringBuffer strBuffer = this.getText(uri);
        //
        DefaultXmlNode currentNode = this.getCurrentXmlPropert(uri);
        currentNode.setText(strBuffer.toString().trim());
        this.setCurrentXmlPropert(uri, (DefaultXmlNode) currentNode.getParent());
        //
        this.cleanText(uri);
        this.curXmlns = uri;
    }
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.curXmlns == null) {
            return;
        }
        String content = new String(ch, start, length);
        StringBuffer strBuffer = this.getText(this.curXmlns);
        strBuffer.append(content);
    }
    public void endDocument() throws SAXException {
        for (Entry<String, DefaultXmlNode> ent : this.currentXmlPropert.entrySet()) {
            String currentXmlns = ent.getKey();
            DefaultXmlNode currentXml = ent.getValue();
            //1.将XmlTree转换为map映射
            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            this.convertType(dataMap, currentXml.getChildren(), "");
            //2.弃掉转换过程中根节点名称
            HashMap<String, Object> finalReturnData = new HashMap<String, Object>();
            for (Entry<String, Object> data : dataMap.entrySet()) {
                String keyStr = data.getKey();
                Object valStr = data.getValue();
                //
                if (keyStr.indexOf(".") > 0) {
                    keyStr = keyStr.substring(keyStr.indexOf(".") + 1);
                    finalReturnData.put(keyStr.toLowerCase(), valStr);
                } else {
                    finalReturnData.put(".", valStr);
                }
            }
            dataMap = finalReturnData;
            //3.输出映射结果，并处理多值合并问题（采用覆盖和追加的策略）
            for (String key : dataMap.keySet()) {
                String $key = key.toLowerCase();
                Object $var = dataMap.get(key);
                this.dataContainer.setSettings($key, $var, currentXmlns);
            }
        }
    }
    /**转换成Key Value形式*/
    protected void convertType(final Map<String, Object> returnData, final List<XmlNode> xmlPropertyList, final String parentAttName) {
        if (xmlPropertyList != null) {
            for (XmlNode xmlNode : xmlPropertyList) {
                DefaultXmlNode impl = (DefaultXmlNode) xmlNode;
                //1.put本级
                String key = "".equals(parentAttName) ? impl.getName() : parentAttName + "." + impl.getName();
                returnData.put(key, impl);
                //2.put属性
                for (Entry<String, String> ent : impl.getAttributeMap().entrySet()) {
                    returnData.put(key + "." + ent.getKey(), ent.getValue());
                }
                //3.put孩子
                this.convertType(returnData, xmlNode.getChildren(), key);
            }
        }
    }
}