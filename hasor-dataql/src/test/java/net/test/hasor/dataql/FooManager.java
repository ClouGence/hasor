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
package net.test.hasor.dataql;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
import net.hasor.utils.json.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * UDF集合
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class FooManager {
    /** 返回一个 double 类型的 number */
    public static class Track implements UDF {
        public Object call(Object[] values, Option readOnly) {
            System.out.println("track -> params : " + JSON.toString(values));
            return null;
        }
    }
    /** 返回一个 double 类型的 number */
    public static class DoubleNumber implements UDF {
        public Object call(Object[] values, Option readOnly) {
            return 1234567.89012;
        }
    }
    /** UDF 作为参数传入 */
    public static class Filter implements UDF {
        public Object call(Object[] values, Option readOnly) throws Throwable {
            System.out.println("Filter -> params : " + JSON.toString(values));
            return ((UDF) values[1]).call(values, readOnly);
        }
    }
    /** 返回单个User Map结构 */
    public static class FindUserByID implements UDF {
        public Object call(Object[] values, Option readOnly) {
            System.out.println("FindUserByID -> params : " + JSON.toString(values));
            //
            ArrayList<Map<String, Object>> addressSet = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < 5; i++) {
                HashMap<String, Object> udfData = new HashMap<String, Object>();
                udfData.put("zip", "1234" + i);
                udfData.put("code", "c_" + i);
                udfData.put("address", "this is detail address info.");
                addressSet.add(udfData);
            }
            //
            HashMap<String, Object> udfData = new HashMap<String, Object>();
            udfData.put("name", "this is name.");
            udfData.put("name2", "this is name2.");
            udfData.put("age", 31);
            udfData.put("sex", "F");
            udfData.put("nick", "this is nick.");
            udfData.put("userID", 1111111);
            udfData.put("status", true);
            udfData.put("addressList", addressSet);
            return udfData;
        }
    }
    /** 返回 54321 */
    public static class Foo implements UDF {
        public Object call(Object[] values, Option readOnly) {
            System.out.println("Foo -> params : " + JSON.toString(values));
            return 54321;
        }
    }
    /** 返回一个List，包含订单列表 */
    public static class QueryOrder implements UDF {
        public Object call(Object[] values, Option readOnly) {
            System.out.println("QueryOrder -> params : " + JSON.toString(values));
            //
            ArrayList<Object> orderList = new ArrayList<Object>();
            for (int i = 0; i < 2; i++) {
                HashMap<String, Object> udfData = new HashMap<String, Object>();
                udfData.put("accountID", 123);
                udfData.put("orderID", 123456789);
                udfData.put("itemID", 987654321);
                udfData.put("itemName", "商品名称");
                orderList.add(udfData);
            }
            return orderList;
        }
    }
    /** 返回单个User信息 */
    public static class UserInfo implements UDF {
        public Object call(Object[] values, Option readOnly) {
            HashMap<String, Object> udfData = new HashMap<String, Object>();
            udfData.put("userID", 1234567890);
            udfData.put("age", 31);
            udfData.put("nick", "my name is nick.");
            udfData.put("name", "this is name.");
            udfData.put("status", true);
            return udfData;
        }
    }
}