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
package org.test.more.core.json;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.more.core.json.JsonUtil;
public class JsonTest {
    private JsonUtil jsonUtil = JsonUtil.getJsonUtil();
    private void println() {
        System.out.println();
    }
    private void println(Object obj) {
        // Float.POSITIVE_INFINITY 
        if (obj != null)
            System.out.println(obj + "  ---  " + obj.getClass());
        else
            System.out.println(obj + "  is null.");
    }
    @Test
    public void testBase() {
        Long.parseLong("100655350000000000");
        println(jsonUtil.toString(true));
        println(jsonUtil.toString(false));
        println();
        //
        println(jsonUtil.toObject("true"));
        println(jsonUtil.toObject("false"));
        println();
        //
        println(jsonUtil.toString((byte) 1));
        println(jsonUtil.toString((short) 2));
        println(jsonUtil.toString((int) 3));
        println(jsonUtil.toString((long) 4));
        println(jsonUtil.toString((float) 5));
        println(jsonUtil.toString((double) 6));
        println(jsonUtil.toString("ffadf\n\t\u4F60"));
        println();
        // 
        println(jsonUtil.toObject("1")); //byte 
        println(jsonUtil.toObject("323")); //short
        println(jsonUtil.toObject("10065535")); //int
        println(jsonUtil.toObject("100655350000000000")); //long
        println(jsonUtil.toObject("123.45")); //float
        println(jsonUtil.toObject("13413241234123412342104245234523450655350000000.123012313456465456")); //double
        println();
        //
        println(jsonUtil.toString("abcdefg"));
        println(jsonUtil.toString(new StringBuffer("testChars")));
        println(jsonUtil.toString('2'));
        println(jsonUtil.toObject("'abcdefg'"));
        println(jsonUtil.toObject("'testChars'"));
        println(jsonUtil.toObject("'2'"));
        println(jsonUtil.toObject("'ffadf\n\t\u4F60Äã'"));
        println();
    }
    @Test
    public void testArray() {
        ArrayList<Object> aa = new ArrayList<Object>();
        aa.add((byte) 1);
        aa.add((short) 2);
        aa.add((int) 3);
        aa.add((long) 4);
        aa.add((float) 5);
        aa.add((double) 6);
        aa.add("'testChars'");
        //
        aa.add(new User());
        aa.add(new User());
        String str = jsonUtil.toString(aa.toArray());
        println(str);
        Object obj = jsonUtil.toObject(str);
        println(obj);
    }
    @Test
    public void testCollection() {
        ArrayList<Object> aa = new ArrayList<Object>();
        aa.add((byte) 1);
        aa.add((short) 2);
        aa.add((int) 3);
        aa.add((long) 4);
        aa.add((float) 5);
        aa.add((double) 6);
        aa.add("'testChars'");
        //
        aa.add(new User());
        aa.add(new User());
        String str = jsonUtil.toString(aa);
        println(str);
        Object obj = jsonUtil.toObject(str);
        println(obj);
    }
    @Test
    public void testMap() {
        HashMap<String, Object> objs = new HashMap<String, Object>();
        ArrayList<String> list = new ArrayList<String>();
        list.add("aaaaaa");
        list.add("bbbbbb");
        list.add("cccccc");
        list.add("dddddd");
        objs.put("keu", list);
        //
        //String str = "{keu:['aaaaaa','bbbbbb','cccccc','dddddd']}";
        String str = jsonUtil.toString(objs);
        println(str);
        Object obj = jsonUtil.toObject(str);
        println(obj);
    }
    @Test
    public void testObject() {}
}