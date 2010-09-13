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
package org.test.more.core.xml;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import org.more.core.xml.XmlDocumentHook;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.XmlParserKitManager;
import org.more.core.xml.stream.EndDocumentEvent;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartDocumentEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.XmlReader;
import org.more.util.attribute.StackDecorator;
/**
 *
 * @version 2010-9-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class Level3_Test {
    @Test
    public void reader() throws FileNotFoundException, XMLStreamException {
        XmlReader reader = new XmlReader("bin/test_xml.xml");
        XmlParserKitManager manager = new XmlParserKitManager();
        XmlParserKit kit = new XmlParserKit();
        //-----------------------
        kit.regeditHook("/", new XmlDocumentHook() {
            public void endDocument(StackDecorator context, EndDocumentEvent event) {
                System.out.println(event);
            }
            public void beginDocument(StackDecorator context, StartDocumentEvent event) {
                System.out.println(event);
            }
        });
        kit.regeditHook("/beans/bean", new XmlElementHook() {
            public void endElement(StackDecorator context, String xpath, EndElementEvent event) {
                System.out.println(event.getName());
            }
            public void beginElement(StackDecorator context, String xpath, StartElementEvent event) {
                int index = event.getAttributeCount();
                if (index != 0)
                    System.out.println(event.getAttributeName(index - 1) + "=" + event.getAttributeValue(index - 1));
                System.out.println(event.getName());
            }
        });
        //-----------------------
        manager.regeditKit("http://www.test.org/schema/bb", kit);
        reader.reader(manager, null);//"/beans/config:config");
    }
}