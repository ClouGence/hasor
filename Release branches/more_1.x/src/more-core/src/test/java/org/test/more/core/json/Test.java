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
import org.more.core.json.JsonUtil;
public class Test {
    /**
     * @param args
     */
    public static void main(String[] args) {
        ArrayList<Object> aa = new ArrayList<Object>();
        aa.add("aaa");
        //        aa.add(new User());
        HashMap<Object, Object> a = new HashMap<Object, Object>();
        //        a.put(false, aa);
        a.put("aa", aa);
        //        a.put(1, aa);
        //        a.put(3, aa);
        //
        JsonUtil ju = new JsonUtil('\'');
        System.out.println(ju.toString(a));
        //String json = "{12:{sex:'дя',aaa:true}}";
        // String json = "''";
        //Map<?, ?> m = ju.toMap(json);
        // System.out.println(m);
        // TODO Auto-generated method stub
    }
}
