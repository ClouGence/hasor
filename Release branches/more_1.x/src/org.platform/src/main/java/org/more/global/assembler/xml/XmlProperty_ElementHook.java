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
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.more.xml.XmlDocumentHook;
import org.more.xml.XmlElementHook;
import org.more.xml.XmlStackDecorator;
import org.more.xml.XmlTextHook;
import org.more.xml.stream.EndDocumentEvent;
import org.more.xml.stream.EndElementEvent;
import org.more.xml.stream.StartDocumentEvent;
import org.more.xml.stream.StartElementEvent;
import org.more.xml.stream.TextEvent;
/**
 * XmlΩ‚Œˆ∆˜
 * @version : 2013-4-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class XmlProperty_ElementHook implements XmlDocumentHook, XmlElementHook, XmlTextHook {
    private static final String XmlProperty_AttName = "XmlProperty";
    private static final String XmlText_AttName     = "XmlText";
    //
    @Override
    public void beginDocument(XmlStackDecorator<Object> context, StartDocumentEvent event) throws XMLStreamException, IOException {
        context.put(XmlProperty_AttName, context.getContext());
    }
    @Override
    public void endDocument(XmlStackDecorator<Object> context, EndDocumentEvent event) throws XMLStreamException, IOException {};
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException {
        DefaultXmlProperty xmlProperty = new DefaultXmlProperty(event.getName().getLocalPart());
        DefaultXmlProperty parentXmlProperty = (DefaultXmlProperty) context.get(XmlProperty_AttName);
        parentXmlProperty.addChildren(xmlProperty);
        for (int i = 0; i < event.getAttributeCount(); i++) {
            String attName = event.getAttributeName(i).getLocalPart();
            String attValue = event.getAttributeValue(i);
            xmlProperty.addAttribute(attName, attValue);
        }
        context.put(XmlProperty_AttName, xmlProperty);
        context.put(XmlText_AttName, new StringBuffer(""));
    };
    public void text(XmlStackDecorator<Object> context, String xpath, TextEvent event) throws XMLStreamException, IOException {
        StringBuffer sb = (StringBuffer) context.get(XmlText_AttName);
        sb.append(event.getTrimText());
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException {
        DefaultXmlProperty xmlProperty = (DefaultXmlProperty) context.get(XmlProperty_AttName);
        StringBuffer sb = (StringBuffer) context.get(XmlText_AttName);
        xmlProperty.setText(sb.toString());
    };
}