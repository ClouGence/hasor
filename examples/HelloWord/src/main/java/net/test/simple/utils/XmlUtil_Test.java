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
package net.test.simple.utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.xml.DomXmlAccept;
import org.more.util.ResourcesUtils;
import org.more.xml.stream.XmlReader;
/**
 * 
 * @version : 2013-11-14
 * @author 赵永春(zyc@hasor.net)
 */
public class XmlUtil_Test {
    public static void main(String[] args) throws IOException, XMLStreamException {
        /*--------------------\
        |  演示 DomXmlAccept 工具用法
        |
        |
        \--------------------*/
        InputStream stream = ResourcesUtils.getResourceAsStream("hasor-config.xml");
        DomXmlAccept xmlAccept = new DomXmlAccept();
        new XmlReader(stream).reader(xmlAccept, null);
        //
        List<XmlNode> docs = xmlAccept.getXmlDocument();
        for (XmlNode doc : docs) {
            System.out.println(doc);
        }
    }
}