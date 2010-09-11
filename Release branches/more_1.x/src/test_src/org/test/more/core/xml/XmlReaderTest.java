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
import org.more.core.xml.stream.XmlAccept;
import org.more.core.xml.stream.XmlReader;
import org.more.core.xml.stream.XmlStreamEvent;
import org.more.core.xml.stream.event.TextEvent;
/**
 *
 * @version 2010-9-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlReaderTest {
    @Test
    public void reader() throws FileNotFoundException, XMLStreamException {
        XmlReader reader = new XmlReader("bin/test_xml.xml");
        reader.setIgnoreComment(true);
        reader.setIgnoreSpace(true);
        reader.reader(new XmlAccept() {
            public void sendEvent(XmlStreamEvent e) {
                //                System.out.println(e.getClass() + "\t\t" + e.getXpath().toString());
                //
                if (e instanceof TextEvent)
                    System.out.print(((TextEvent) e).getTrimText());
                //                if (e instanceof AttributeEvent)
                //                    System.out.println(((AttributeEvent) e).getValue());
                //                if (e instanceof StartElementEvent) {
                //                    StartElementEvent ee = (StartElementEvent) e;
                //                    if (ee.getAttributeCount() != 0)
                //                        System.out.println(((StartElementEvent) e).getAttributeValue(0));
                //                }
            }
        }, null);//"/beans/config:config");
    }
}