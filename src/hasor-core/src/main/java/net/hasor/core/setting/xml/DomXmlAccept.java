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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import net.hasor.core.XmlNode;
import org.more.util.StringUtils;
import org.more.xml.stream.EndElementEvent;
import org.more.xml.stream.StartElementEvent;
import org.more.xml.stream.TextEvent;
import org.more.xml.stream.XmlAccept;
import org.more.xml.stream.XmlStreamEvent;
/**
 * 将 Xml 文件转换为  XmlNode 接口形式的 Dom 树。
 * @version : 2013-11-14
 * @author 赵永春(zyc@hasor.net)
 */
public class DomXmlAccept implements XmlAccept {
    private DefaultXmlNode currentXmlNode;
    private StringBuffer   bodyText;
    //
    public void beginAccept() throws XMLStreamException {
        this.currentXmlNode = new DefaultXmlNode(null, "");
        this.bodyText = new StringBuffer("");
    }
    public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
        if (e instanceof StartElementEvent) {
            //
            StartElementEvent event = (StartElementEvent) e;
            String prefix = event.getName().getPrefix();
            String name = event.getName().getLocalPart();
            String finalName = StringUtils.isBlank(prefix) ? name : (prefix + ":" + name);
            DefaultXmlNode thisNodes = new DefaultXmlNode(currentXmlNode, finalName);
            this.currentXmlNode.getChildren().add(thisNodes);
            for (int i = 0; i < event.getAttributeCount(); i++) {
                QName attQName = event.getAttributeName(i);
                String attPrefix = attQName.getPrefix();
                String attName = attQName.getLocalPart();
                String attFinalName = StringUtils.isBlank(attPrefix) ? attName : (attPrefix + ":" + attName);
                String attValue = event.getAttributeValue(i);
                thisNodes.addAttribute(attFinalName, attValue);
            }
            for (int i = 0; i < event.getNamespaceCount(); i++) {
                String nsPre = event.getNamespacePrefix(i);
                nsPre = StringUtils.isBlank(nsPre) ? "" : (":" + nsPre);
                String nsURI = event.getNamespaceURI(i);
                thisNodes.addAttribute("xmlns" + nsPre, nsURI);
            }
            this.currentXmlNode = thisNodes;
            //
        } else if (e instanceof EndElementEvent) {
            //
            this.currentXmlNode.setText(bodyText.toString());
            this.currentXmlNode = (DefaultXmlNode) this.currentXmlNode.getParent();
            bodyText = new StringBuffer("");
        } else if (e instanceof TextEvent) {
            //
            TextEvent event = (TextEvent) e;
            bodyText.append(event.getOriginalText());
        }
    }
    public void endAccept() throws XMLStreamException {}
    //
    public List<XmlNode> getXmlDocument() {
        List<XmlNode> document = currentXmlNode.getChildren();
        return new ArrayList<XmlNode>(document);
    }
}