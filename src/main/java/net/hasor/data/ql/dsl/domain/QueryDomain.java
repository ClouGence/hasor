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
package net.hasor.data.ql.dsl.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 查询模型
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class QueryDomain {
    private String              queryName  = null;
    private DataUDF             dataUDF    = null;
    private List<String>        fieldOrder = new ArrayList<String>();
    private Map<String, DValue> fieldMap   = new HashMap<String, DValue>();
    private ReturnType          returnType = ReturnType.Object;
    //
    public void addField(String fieldName, DValue dValue) {
        if (this.fieldMap.containsKey(fieldName)) {
            throw new RuntimeException(fieldName);
        }
        this.fieldOrder.add(fieldName);
        this.fieldMap.put(fieldName, dValue);
    }
    public List<String> getFieldNames() {
        return this.fieldOrder;
    }
    public DValue getField(String fieldName) {
        return this.fieldMap.get(fieldName);
    }
    //
    public String getQueryName() {
        return queryName;
    }
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }
    public DataUDF getUDF() {
        return dataUDF;
    }
    public void setUDF(DataUDF dataUDF) {
        this.dataUDF = dataUDF;
    }
    public ReturnType getReturnType() {
        return returnType;
    }
    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }
}