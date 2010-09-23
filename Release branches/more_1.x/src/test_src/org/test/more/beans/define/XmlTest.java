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
package org.test.more.beans.define;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import org.more.beans.AbstractBeanDefine;
import org.more.beans.resource.AbstractXmlConfiguration;
import org.more.beans.resource.XmlBeansConfiguration;
/**
 * ≤‚ ‘¡Ànamespace∞¸,define∞¸
 * @version 2010-9-21
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlTest {
    @Test
    public void test() throws XMLStreamException {
        InputStream in = AbstractXmlConfiguration.class.getResourceAsStream("/org/test/more/beans/define/collection-test-config.xml");
        AbstractXmlConfiguration con = new XmlBeansConfiguration().passerXml(in);
        AbstractBeanDefine define = con.getBeanDefine("2.testCollection");
        System.out.println(define.getName());
    }
}