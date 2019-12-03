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

/**
 * 对象结果
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class ObjectModel extends LinkedHashMap<String, DataModel> implements DataModel {
    /** 判断是否为 ValueModel 类型值 */
    public boolean isValueModel(String fieldName) {
        return super.get(fieldName) instanceof ValueModel;
    }

    /** 将某一个元素转换为 ValueModel */
    public ValueModel asValueModel(String fieldName) {
        DataModel dataItem = super.get(fieldName);
        if (dataItem instanceof ValueModel) {
            return (ValueModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to ValueModel.");
    }

    /** 判断是否为 ListModel 类型值 */
    public boolean isListModel(String fieldName) {
        return super.get(fieldName) instanceof ListModel;
    }

    /** 将某一个元素转换为 ListModel */
    public ListModel asListModel(String fieldName) {
        DataModel dataItem = super.get(fieldName);
        if (dataItem instanceof ListModel) {
            return (ListModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to ListModel.");
    }

    /** 判断是否为 ObjectModel 类型值 */
    public boolean isObjectModel(String fieldName) {
        return super.get(fieldName) instanceof ObjectModel;
    }

    /** 将某一个元素转换为 ObjectModel */
    public ObjectModel asObjectModel(String fieldName) {
        DataModel dataItem = super.get(fieldName);
        if (dataItem instanceof ObjectModel) {
            return (ObjectModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to ObjectModel.");
    }

    /** 判断是否为 UdfModel 类型值 */
    public boolean isUdfModel(String fieldName) {
        return super.get(fieldName) instanceof UdfModel;
    }

    /** 将某一个元素转换为 UdfModel */
    public UdfModel asUdfModel(String fieldName) {
        DataModel dataItem = super.get(fieldName);
        if (dataItem instanceof UdfModel) {
            return (UdfModel) dataItem;
        }
        throw new ClassCastException(dataItem.getClass() + " not Cast to UdfModel.");
    }
}