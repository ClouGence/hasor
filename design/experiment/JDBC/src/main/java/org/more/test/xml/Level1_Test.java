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
package org.more.test.xml;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import org.more.util.ResourcesUtils;
import org.more.xml.stream.XmlAccept;
import org.more.xml.stream.XmlReader;
import org.more.xml.stream.XmlStreamEvent;
/**
 *
 * @version 2010-9-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class Level1_Test {
    @Test
    public void reader() throws XMLStreamException, IOException {
        String url = "org/test/more/core/xml/level_1.xml";
        InputStream in = ResourcesUtils.getResourceAsStream(url);
        //
        new XmlReader(in).reader(new XmlSpan(), null);
    }
}
class XmlSpan implements XmlAccept {
    public void beginAccept() throws XMLStreamException {
        System.out.println("begin....");
    }
    public void endAccept() throws XMLStreamException {
        System.out.println("end!");
    }
    public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
        if (e.getXpath().equals("/safety/resources")) {
            e.skip();
            System.out.println("skip  \t" + e.getClass().getSimpleName() + e.getXpath());
            return;
        }
        System.out.println(e.getClass().getSimpleName() + "\t" + e.getXpath());
    }
}