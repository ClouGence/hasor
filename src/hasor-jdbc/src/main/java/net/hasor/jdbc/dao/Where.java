/*
 * Copyright 2002-2008 the original author or authors.
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
package net.hasor.jdbc.dao;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @version : 2013-12-3
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class Where {
    private String   whereString;
    private Object[] params;
    //
    public Where(String whereString, Object[] params) {
        this.whereString = whereString;
        this.params = params;
    }
    public String getWhereString() {
        return whereString;
    }
    public void setWhereString(String whereString) {
        this.whereString = whereString;
    }
    public Object[] getParams() {
        return params;
    }
    public void setParams(Object[] params) {
        this.params = params;
    }
    //
    public String toJson() {
        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("whereString", whereString);
        jsonMap.put("values", params);
        return JSON.toString(jsonMap);
    }
    public void applyJson(String jsonData) {
        Map<String, Object> jsonMap = (Map<String, Object>) JSON.parse(jsonData);
        if (jsonMap.containsKey("whereString"))
            whereString = (String) jsonMap.get("whereString");
        if (jsonMap.containsKey("pattern"))
            params = (String[]) jsonMap.get("values");
    }
}