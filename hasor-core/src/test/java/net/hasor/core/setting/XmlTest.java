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
package net.hasor.core.setting;
import net.hasor.core.setting.xml.DefaultXmlNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class XmlTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void buildTest() {
        DefaultXmlNode xmlNode = new DefaultXmlNode("root");
        xmlNode.setText("<%%>");
        xmlNode.addAttribute("name", "aName");
        assert xmlNode.getXmlText().equals("<root name=\"aName\">&lt;%%&gt;</root>");
        assert xmlNode.toString().equals("<root name=\"aName\">&lt;%%&gt;</root>");
        //
        assert xmlNode.clone().getXmlText().equals("<root name=\"aName\">&lt;%%&gt;</root>");
        //
        DefaultXmlNode rootNode = new DefaultXmlNode("parent");
        rootNode.addChildren(xmlNode);
        rootNode.addChildren(xmlNode);
        assert rootNode.getXmlText().equals("<parent><root name=\"aName\">&lt;%%&gt;</root><root name=\"aName\">&lt;%%&gt;</root></parent>");
        assert rootNode.clone().getXmlText().equals("<parent><root name=\"aName\">&lt;%%&gt;</root><root name=\"aName\">&lt;%%&gt;</root></parent>");
    }
}
