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
package net.test.hasor.more.xml;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import org.more.util.ResourcesUtils;
import org.more.xml.XmlNamespaceParser;
import org.more.xml.XmlParserKitManager;
import org.more.xml.XmlStackDecorator;
import org.more.xml.stream.XmlReader;
import org.more.xml.stream.XmlStreamEvent;
/**
 *
 * @version 2010-9-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class Level2_Test {
    @Test
    public void reader() throws XMLStreamException, IOException {
        String url = "/more-xml/ns-all-in-one-config.xml";
        XmlReader reader = new XmlReader(ResourcesUtils.getResourceAsStream(url));
        //reader.setIgnoreComment(true);
        //reader.setIgnoreSpace(true);
        XmlParserKitManager manager = new XmlParserKitManager();
        //manager.regeditKit("http://www.test.org/schema/beans", new XmlParserKit());
        //manager.regeditKit("http://www.test.org/schema/config", new XmlParserKit());
        manager.regeditKit("http://project.xdf.cn/program", new XmlNSP("A"));
        manager.regeditKit("http://mode1.myProject.net", new XmlNSP("B"));
        manager.regeditKit("http://mode2.myProject.net", new XmlNSP("C"));
        //http://www.test.org/schema/config
        reader.reader(manager, null);//"/beans/config:config");
    }
}
class XmlNSP implements XmlNamespaceParser {
    private String marker = null;
    public XmlNSP(String string) {
        this.marker = string + " ";
    }
    public void beginAccept() {
        System.out.println(marker + "begin....");
    }
    public void endAccept() {
        System.out.println(marker + "end!");
    }
    public void sendEvent(XmlStackDecorator<Object> context, String xpath, XmlStreamEvent event) throws IOException, XMLStreamException {
        System.out.println(marker + context.getDepth() + "\t" + event.getClass().getSimpleName() + "\t" + xpath);
    }
}