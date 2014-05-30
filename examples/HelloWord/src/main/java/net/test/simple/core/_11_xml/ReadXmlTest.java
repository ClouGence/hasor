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
package net.test.simple.core._11_xml;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import net.hasor.core.Hasor;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.InputStreamSettings;
import org.junit.Test;
import org.more.util.ResourcesUtils;
/**
 * 读取一般性Xml文件
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class ReadXmlTest {
    @Test
    public void readXmlTest() throws IOException, URISyntaxException {
        System.out.println("--->>readXmlTest<<--");
        InputStream inStream = ResourcesUtils.getResourceAsStream("net/test/simple/core/_11_xml/xml-data.xml");
        InputStreamSettings settings = new InputStreamSettings(inStream);
        settings.loadSettings();//装载配置文件
        //
        //
        XmlNode xmlNoe = settings.getXmlNode("demoProject.menus");
        for (XmlNode sub : xmlNoe.getChildren("menu")) {
            String menuCode = sub.getAttribute("code");
            String menuName = sub.getAttribute("name");
            String menuURL = sub.getAttribute("url");
            Hasor.logInfo("%s[%s] to %s.", menuName, menuCode, menuURL);
        }
    }
}