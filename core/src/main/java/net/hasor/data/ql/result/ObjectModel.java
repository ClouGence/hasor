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
package net.hasor.data.ql.result;
import net.hasor.data.ql.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 对象结果
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectModel extends HashMap<String, Object> implements QueryResult {
    private List<String>        sortList;
    private Map<String, Object> objectData;
    //
    public ObjectModel(List<String> sortList) {
        this.sortList = sortList;
    }
    public int getFieldSize() {
        return this.sortList.size();
    }
    public List<String> getFieldNames() {
        return null;
    }
    public boolean hasField(String fieldName) {
        return false;
    }
    public Object getOriResult(String fieldName) {
        return this.objectData.get(fieldName);
    }
    public ValueModel getValueResult(String fieldName) {
        return null;
    }
    public ListModel getListResult(String fieldName) {
        return null;
    }
    public ObjectModel getObjectResult(String fieldName) {
        return null;
    }
}