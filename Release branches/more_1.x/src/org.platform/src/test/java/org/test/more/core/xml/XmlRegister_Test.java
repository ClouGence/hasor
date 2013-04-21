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
import org.more.xml.register.XmlRegister;
/**
 * */
public class XmlRegister_Test {
    @Test
    public void reader() throws XMLStreamException, IOException {
        String url = "org/test/more/core/xml/register.xml";
        XmlRegister register = new XmlRegister();
        register.loadRegister(url);
        //-----------------------
        register.addSource("org/test/more/core/xml/level_1.xml");
        register.addSource("org/test/more/core/xml/level_2.xml");
        register.addSource("org/test/more/core/xml/level_3.xml");
        System.out.println();
        register.loadXml();
    }
}
