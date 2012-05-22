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
import org.junit.Test;
import org.more.core.global.Global;
import org.more.core.global.assembler.PropertiesGlobalFactory;
import org.test.more.core.json.User;
/**
 * 
 * @version : 2011-9-5
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class GlobalTest {
    public static void main(String[] args) throws Throwable {
        Global global = new PropertiesGlobalFactory().createGlobal("utf-8", new Object[] { "org/test/more/core/global/global.properties", "global.properties" });
        long count = 0;
        int step = 50000;
        int index = 0;
        long start = System.currentTimeMillis();
        while (true) {
            index++;
            global.getFilePath("String");
            if (count * step < index) {
                count++;
                System.out.println(index + "\t" + (System.currentTimeMillis() - start));
            }
        }
    }
    @Test
    public void testBase() throws Throwable {
        //        HashMap root = new HashMap();
        //        root.put("name", "adf");
        //        HashMap context = new HashMap();
        //        context.put("root", root);
        //        System.out.println(Ognl.getValue("root['name']", context));
        Global global = new PropertiesGlobalFactory().createGlobal("utf-8", new Object[] { "org/test/more/core/global/global.properties", "global.properties" });
        //
        System.out.println(global.getObject("JsonData"));
        System.out.println("----------------");
        System.out.println(global.getObject(Element.DocPath));
        for (Element e : Element.values())
            System.out.println(global.getObject(e));
        //
        System.out.println("----------------1");
        global.setAttribute("testParam", new User());
        System.out.println("----------------1");
        global.setAttribute("testParam", new User());
        System.out.println(global.getFilePath("filePath"));
        System.out.println(global.getDirectoryPath("filePath"));
        System.out.println(global.getString("abc.efg"));
        System.out.println(global.getString("String"));
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