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
package net.hasor.dataql.result;
import net.hasor.utils.StringUtils;

import java.util.*;
/**
 * 对象结果
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectModel extends HashMap<String, Object> implements DataModel {
    private List<String> sortList;
    //
    //
    public ObjectModel() {
        this.sortList = new ArrayList<String>();
    }
    public ObjectModel(Object dataItem) {
        this();
        if (dataItem instanceof Map) {
            Set keySet = ((Map) dataItem).keySet();
            for (Object keyName : keySet) {
                this.addField(keyName.toString());
            }
            this.putAll((Map<? extends String, ?>) dataItem);
        } else {
            InterBeanMap beanMap = new InterBeanMap(dataItem);
            this.sortList.addAll(beanMap.keySet());
            this.putAll(beanMap);
        }
    }
    public ObjectModel(Collection<String> sortList) {
        this.sortList = new ArrayList<String>(sortList);
    }
    //
    public void addField(String field) {
        if (this.sortList.contains(field)) {
            return;
        }
        this.sortList.add(field);
    }
    @Override
    public Object put(String key, Object value) {
        if (StringUtils.isBlank(key) || !this.hasField(key)) {
            return null;
        }
        return super.put(key, value);
    }
    @Override
    public void putAll(Map<? extends String, ?> m) {
        if (m == null || m.isEmpty()) {
            return;
        }
        for (Map.Entry<? extends String, ?> ent : m.entrySet()) {
            this.put(ent.getKey(), ent.getValue());
        }
    }
    //
    public int getFieldSize() {
        return this.sortList.size();
    }
    public List<String> getFieldNames() {
        return Collections.unmodifiableList(this.sortList);
    }
    public boolean hasField(String fieldName) {
        return this.sortList.contains(fieldName);
    }
    public Object getOriResult(String fieldName) {
        return super.get(fieldName);
    }
    //
    public ValueModel asValueModel(String fieldName) {
        Object dataItem = super.get(fieldName);
        if (dataItem instanceof ValueModel) {
            return (ValueModel) dataItem;
        }
        return new ValueModel(dataItem);
    }
    public ListModel asListModel(String fieldName) {
        Object dataItem = super.get(fieldName);
        if (dataItem instanceof ListModel) {
            return (ListModel) dataItem;
        }
        return new ListModel(dataItem);
    }
    public ObjectModel asObjectModel(String fieldName) {
        Object dataItem = super.get(fieldName);
        if (dataItem instanceof ObjectModel) {
            return (ObjectModel) dataItem;
        }
        return new ObjectModel(dataItem);
    }
}