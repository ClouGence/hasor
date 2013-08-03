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
package org.hasor.context.setting.xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.stream.XMLStreamException;
import org.hasor.context.Settings;
import org.hasor.context.XmlProperty;
import org.hasor.context.setting.HasorXmlParser;
import org.more.xml.XmlStackDecorator;
import org.more.xml.stream.AttributeEvent;
import org.more.xml.stream.EndElementEvent;
import org.more.xml.stream.StartElementEvent;
import org.more.xml.stream.TextEvent;
import org.more.xml.stream.XmlStreamEvent;
/**
 * 主配置文件的解析器
 * @version : 2013-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class RootSettingsHasorXmlParser implements HasorXmlParser {
    private StringBuffer    xmlText           = null;
    private XmlPropertyImpl currentXmlPropert = null;
    //
    @Override
    public void beginAccept(Settings context, Map<String, Object> dataContainer) {
        this.currentXmlPropert = new XmlPropertyImpl(null, "root");
        this.xmlText = new StringBuffer("");
    }
    @Override
    public void sendEvent(XmlStackDecorator<Object> context, String xpath, XmlStreamEvent event) throws IOException, XMLStreamException {
        if (event instanceof StartElementEvent) {
            //
            String localPart = ((StartElementEvent) event).getName().getLocalPart();
            XmlPropertyImpl xmlProperty = new XmlPropertyImpl(this.currentXmlPropert, localPart);
            this.currentXmlPropert.addChildren(xmlProperty);
            this.currentXmlPropert = xmlProperty;
        } else if (event instanceof EndElementEvent) {
            //
            this.currentXmlPropert.setText(this.xmlText.toString().trim());
            this.xmlText = new StringBuffer("");
            this.currentXmlPropert = (XmlPropertyImpl) this.currentXmlPropert.getParent();
        } else if (event instanceof AttributeEvent) {
            //
            String attName = ((AttributeEvent) event).getName().getLocalPart();
            String attValue = ((AttributeEvent) event).getValue();
            this.currentXmlPropert.addAttribute(attName, attValue);
        } else if (event instanceof TextEvent) {
            //
            this.xmlText.append(((TextEvent) event).getText());
        }
    }
    @Override
    public void endAccept(Settings context, Map<String, Object> dataContainer) {
        //1.将XmlTree转换为map映射
        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        this.convertType(dataMap, this.currentXmlPropert.getChildren(), "");
        //2.弃掉转换过程中根节点名称
        HashMap<String, Object> finalReturnData = new HashMap<String, Object>();
        for (Entry<String, Object> ent : dataMap.entrySet()) {
            String keyStr = ent.getKey();
            keyStr = keyStr.substring(keyStr.indexOf(".") + 1);
            finalReturnData.put(keyStr.toLowerCase(), ent.getValue());
        }
        dataMap = finalReturnData;
        //3.处理多值合并问题（采用覆盖和追加的策略）
        for (String key : dataMap.keySet()) {
            String $key = key.toLowerCase();
            Object $var = dataMap.get(key);
            Object $varConflict = dataContainer.get($key);
            if ($varConflict != null && $varConflict instanceof XmlProperty && $var instanceof XmlProperty) {
                XmlProperty $new = (XmlProperty) $var;
                XmlProperty $old = (XmlProperty) $varConflict;
                XmlProperty $final = ((XmlPropertyImpl) $old).clone();
                /*覆盖策略*/
                $final.getAttributeMap().putAll($new.getAttributeMap());
                ((XmlPropertyImpl) $final).setText($new.getText());
                /*追加策略*/
                List<XmlProperty> $newChildren = new ArrayList<XmlProperty>($new.getChildren());
                List<XmlProperty> $oldChildren = new ArrayList<XmlProperty>($old.getChildren());
                Collections.reverse($newChildren);
                Collections.reverse($oldChildren);
                $final.getChildren().clear();
                $final.getChildren().addAll($oldChildren);
                $final.getChildren().addAll($newChildren);
                Collections.reverse($final.getChildren());
                dataContainer.put($key, $final);
            } else
                dataContainer.put($key, $var);
        }
    }
    /**转换成Key Value形式*/
    protected void convertType(Map<String, Object> returnData, List<XmlProperty> xmlPropertyList, String parentAttName) {
        if (xmlPropertyList != null)
            for (XmlProperty xmlProperty : xmlPropertyList) {
                XmlPropertyImpl impl = (XmlPropertyImpl) xmlProperty;
                //1.put本级
                String key = ("".equals(parentAttName)) ? impl.getName() : (parentAttName + "." + impl.getName());
                returnData.put(key, impl);
                //2.put属性
                for (Entry<String, String> ent : impl.getAttributeMap().entrySet())
                    returnData.put(key + "." + ent.getKey(), ent.getValue());
                //3.put孩子
                this.convertType(returnData, xmlProperty.getChildren(), key);
            }
    }
}