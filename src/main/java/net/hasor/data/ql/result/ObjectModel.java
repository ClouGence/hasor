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
import net.hasor.data.ql.ListResult;
import net.hasor.data.ql.ObjectResult;
import net.hasor.data.ql.ValueResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectModel extends HashMap<String, Object> implements ObjectResult {
    private List<String>        sortList;
    private Map<String, Object> objectData;
    public ObjectModel(List<String> sortList) {
        this.sortList = sortList;
    }
    @Override
    public int getFieldSize() {
        return this.sortList.size();
    }
    @Override
    public List<String> getFieldNames() {
        return null;
    }
    @Override
    public boolean hasField(String fieldName) {
        return false;
    }
    @Override
    public Object getOriResult(String fieldName) {
        return this.objectData.get(fieldName);
    }
    @Override
    public ValueResult getValueResult(String fieldName) {
        return null;
    }
    @Override
    public ListResult getListResult(String fieldName) {
        return null;
    }
    @Override
    public ObjectResult getObjectResult(String fieldName) {
        return null;
    }
}