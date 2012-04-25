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
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.XmlParserKitManager;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.XmlReader;
import org.more.util.ResourcesUtil;
/**
 *
 * @version 2010-9-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class Level3_Test {
    @Test
    public void reader() throws XMLStreamException, IOException {
        String url = "org/test/more/core/xml/level_3.xml";
        XmlReader reader = new XmlReader(ResourcesUtil.getResourceAsStream(url));
        XmlParserKitManager manager = new XmlParserKitManager();
        XmlParserKit kit = new XmlParserKit();
        //-----------------------
        kit.regeditHook("/program", new Tag_programe());
        kit.regeditHook("*/echoPath", new Tag_echoPath());
        kit.regeditHook("*/if", new Tag_if());
        kit.regeditHook("*/function", new Tag_function());
        //-----------------------
        manager.regeditKit("http://project.xdf.cn/program", kit);
        reader.reader(manager, null);//"/beans/config:config");
    }
}
class Tag_programe implements XmlElementHook {
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException {
        System.out.println("programe begin...");
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException {
        System.out.println("programe stop!");
    }
}
class Tag_echoPath implements XmlElementHook {
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException {
        System.out.println("\t-----" + xpath);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException {}
}
class Tag_if implements XmlElementHook {
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException {
        String str = event.getAttributeValue("enable");
        if (str.equals("true") == true)
            event.skip();
        else
            System.out.println("begin if \t" + xpath);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException {
        System.out.println("end if \t" + xpath);
    }
}
class Tag_function implements XmlElementHook {
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException {
        System.out.println("callFun\t" + xpath);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException {}
}
