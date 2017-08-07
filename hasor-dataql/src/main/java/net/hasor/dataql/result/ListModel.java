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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
/**
 * 集合类型结果集
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ListModel extends ArrayList<Object> implements DataModel {
    public ListModel() {
    }
    public ListModel(Object dataItem) {
        this.initData(dataItem);
    }
    public ListModel(Collection<Object> dataItem) {
        this.initData(dataItem);
    }
    private void initData(Object dataItem) {
        if (dataItem == null) {
            return;
        }
        if (!(dataItem instanceof Collection)) {
            if (dataItem.getClass().isArray()) {
                for (Object obj : (Object[]) dataItem) {
                    super.add(obj);
                }
            } else {
                super.addAll(Arrays.asList(dataItem));
            }
        } else {
            super.addAll((Collection<Object>) dataItem);
        }
    }
    //
    public ValueModel asValueModel(int index) {
        Object dataItem = super.get(index);
        if (dataItem instanceof ValueModel) {
            return (ValueModel) dataItem;
        }
        return new ValueModel(dataItem);
    }
    public ListModel asListModel(int index) {
        Object dataItem = super.get(index);
        if (dataItem instanceof ListModel) {
            return (ListModel) dataItem;
        }
        return new ListModel(dataItem);
    }
    public ObjectModel asObjectModel(int index) {
        Object dataItem = super.get(index);
        if (dataItem instanceof ObjectModel) {
            return (ObjectModel) dataItem;
        }
        return new ObjectModel(dataItem);
    }
}