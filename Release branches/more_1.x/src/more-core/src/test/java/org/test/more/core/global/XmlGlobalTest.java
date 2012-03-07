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
import org.junit.Test;
import org.more.core.global.Global;
import org.more.core.ognl.OgnlException;
/**
 * 
 * @version : 2011-9-5
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlGlobalTest {
    @Test
    public void testBase() throws IOException, ClassNotFoundException, OgnlException {
        //        HashMap root = new HashMap();
        //        root.put("name", "adf");
        //        HashMap context = new HashMap();
        //        context.put("root", root);
        //        System.out.println(Ognl.getValue("root['name']", context));
        Global global = Global.newInstanceByFactory("xml", "utf-8", "org/test/more/core/global/global.xml");
//        System.out.println(global.getString("variables.variable"));
    }
}