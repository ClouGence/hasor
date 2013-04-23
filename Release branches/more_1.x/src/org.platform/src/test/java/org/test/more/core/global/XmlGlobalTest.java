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
package org.test.more.core.global;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import org.more.core.error.LoadException;
import org.more.global.Global;
import org.more.global.assembler.xml.XmlPropertyGlobalFactory;
import org.more.ognl.OgnlException;
/**
 * 
 * @version : 2011-9-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlGlobalTest {
    @Test
    public void testBase() throws IOException, ClassNotFoundException, OgnlException, LoadException, XMLStreamException {
        XmlPropertyGlobalFactory globalFactory = new XmlPropertyGlobalFactory();
        globalFactory.setIgnoreRootElement(true);//在解析XML的时候忽略根节点。
        Global global = globalFactory.createGlobal("utf-8", new Object[] { "org/test/more/core/global/global.xml" });
        //
        System.out.println(global.getString("jdbc.driver"));
    }
}