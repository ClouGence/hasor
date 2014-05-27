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
import javax.xml.stream.XMLStreamException;
import org.more.xml.XmlElementHook;
import org.more.xml.XmlStackDecorator;
import org.more.xml.stream.EndElementEvent;
import org.more.xml.stream.StartElementEvent;
/**
 * 
 * @version : 2011-11-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class C_Tag_A implements XmlElementHook {
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) throws XMLStreamException, IOException {
        System.out.println("C Begin\t" + xpath);
    }
    public void endElement(XmlStackDecorator<Object> context, String xpath, EndElementEvent event) throws XMLStreamException, IOException {
        System.out.println("C End\t" + xpath);
    }
}