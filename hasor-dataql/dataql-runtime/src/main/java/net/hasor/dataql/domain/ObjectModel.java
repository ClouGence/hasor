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
package net.hasor.dataql.domain;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 对象结果
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ObjectModel implements DataModel {
    private Map<String, DataModel> dataModel = new LinkedHashMap<>();

    public ObjectModel() {
    }

    public void put(String key, Object value) {
        this.dataModel.put(key, DomainHelper.convertTo(value));
    }

    public <K, V> void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey().toString(), e.getValue());
        }
    }

    public int size() {
        return this.dataModel.size();
    }

    @Override
    public Map<String, DataModel> asOri() {
        return this.dataModel;
    }

    @Override
    public Map<String, Object> unwrap() {
        Map<String, Object> unwrap = new LinkedHashMap<>(this.dataModel.size());
        this.dataModel.forEach((key, dataModel) -> {
            unwrap.put(key, dataModel.unwrap());
        });
        return unwrap;
    }

    /** 判断是否为 ObjectModel 类型值 */
    public boolean isObject() {
        return true;
    }

    /** 获取某一个元素 */
    public DataModel get(String fieldName) {
        return this.dataModel.get(fieldName);
    }

    /** 判断是否为 ValueModel 类型值 */
    public boolean isValue(String fieldName) {
        return this.dataModel.get(fieldName) instanceof ValueModel;
    }

    /** 将某一个元素转换为 ValueModel */
    public ValueModel getValue(String fieldName) {
        DataModel dataItem = this.dataModel.get(fieldName);
        if (dataItem instanceof ValueModel) {
            return (ValueModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to ValueModel.");
    }

    /** 判断是否为 ListModel 类型值 */
    public boolean isList(String fieldName) {
        return this.dataModel.get(fieldName) instanceof ListModel;
    }

    /** 将某一个元素转换为 ListModel */
    public ListModel getList(String fieldName) {
        DataModel dataItem = this.dataModel.get(fieldName);
        if (dataItem instanceof ListModel) {
            return (ListModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to ListModel.");
    }

    /** 判断是否为 ObjectModel 类型值 */
    public boolean isObject(String fieldName) {
        return this.dataModel.get(fieldName) instanceof ObjectModel;
    }

    /** 将某一个元素转换为 ObjectModel */
    public ObjectModel getObject(String fieldName) {
        DataModel dataItem = this.dataModel.get(fieldName);
        if (dataItem instanceof ObjectModel) {
            return (ObjectModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to ObjectModel.");
    }

    /** 判断是否为 UdfModel 类型值 */
    public boolean isUdf(String fieldName) {
        return this.dataModel.get(fieldName) instanceof UdfModel;
    }

    /** 将某一个元素转换为 UdfModel */
    public UdfModel getUdf(String fieldName) {
        DataModel dataItem = this.dataModel.get(fieldName);
        if (dataItem instanceof UdfModel) {
            return (UdfModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to UdfModel.");
    }
}