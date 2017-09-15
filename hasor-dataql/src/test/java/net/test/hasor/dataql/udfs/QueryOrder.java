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
package net.test.hasor.dataql.udfs;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
import net.hasor.utils.json.JSON;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class QueryOrder implements UDF {
    @Override
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