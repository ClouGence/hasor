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
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.more.core.global.Global;
import org.more.core.ognl.OgnlException;
import org.test.more.core.json.User;
/**
 * 
 * @version : 2011-9-5
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class GlobalTest {
    @Test
    public void testBase() throws IOException, ClassNotFoundException, OgnlException {
        //        HashMap root = new HashMap();
        //        root.put("name", "adf");
        //        HashMap context = new HashMap();
        //        context.put("root", root);
        //        System.out.println(Ognl.getValue("root['name']", context));
        Global global = Global.newInstance("properties", "org/test/more/core/global/global.properties", "global.properties");
        System.out.println(global.evalName("_global.enableJson"));
        System.out.println(global.evalName("_global.enableEL"));
        System.out.println(global.evalName("_global.groupCount"));
        System.out.println(global.evalName("_global.count"));
        //
        //
        System.out.println();
        System.out.println(global.evalName("_global['global.properties'].String"));
        System.out.println(global.evalName("_global['global.properties'].String"));
        System.out.println(global.evalName("_global['global.properties'].count"));
        //
        System.out.println();
        System.out.println(global.evalName("_global['global.properties'][0].count"));
        System.out.println(global.evalName("_global['global.properties'][0].String"));
        System.out.println(global.evalName("_global['global.properties'][1].count"));
        System.out.println(global.evalName("_global['global.properties'][1].String"));
        //
        System.out.println(global.getObject("JsonData"));
        System.out.println(global.evalName("_global['global.properties'].JsonData"));
        //
        System.out.println("----------------");
        System.out.println(global.getObject(Element.DocPath));
        for (Element e : Element.values())
            System.out.println(global.getObject(e));
        //
        System.out.println("----------------1");
        global.setAttribute("testParam", new User());
        System.out.println(global.evalName("testParam.name + _global['global.properties'][1].DocPath"));
    }
    //    public void test() throws IOException {
    //        Global global = Global.createForFile("org/test/more/core/global/global.properties");
    //        System.out.println(global.getObject(Element.DocPath));
    //        for (Element e : Element.values())
    //            System.out.println(global.getObject(e));
    //    }
    //    //@Test
    //    public void testDB() throws IOException, SQLException, OgnlException {
    //        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "");
    //        c.createStatement().execute("create table varTable (key varchar(50) not null primary key, var varchar(600) not null );");
    //        PreparedStatement ps = c.prepareStatement("insert into varTable values('DocPath',?)");
    //        ps.setString(1, "${userID + (1+2*3-6) + \"${{{{{aaa}}}}'}}'}}\\\"}\\\"}}}\" + \"sfs\"}start");
    //        ps.execute();
    //        Global global = new DataBaseGlobal(c, "varTable", "key", "var");
    //        System.out.println(global.getObject("DocPath"));
    //        //for (Element e : Element.values())
    //        //  System.out.println(global.getObject(e));
    //    }
}
enum Element {
    JsonData, StringArray, String, Context, Str, DocPath, elTest0//, elTest1
}
//   ${userID + (1+2*3-6) + "${{{{{aaa}}}}'}}'}}\"}\"}}}" + "sfs"}start
//  ${userID + (1+2*3-6) + \"${{{{{aaa}}}}'}}'}}\\\"}\\\"}}}\" + \"sfs\"}start