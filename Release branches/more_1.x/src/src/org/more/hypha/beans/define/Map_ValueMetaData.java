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
package org.more.hypha.beans.define;
import java.util.HashMap;
import java.util.Map;
import org.more.hypha.beans.PropertyMetaTypeEnum;
import org.more.hypha.beans.ValueMetaData;
/**
 * 表示一个{@link Map}类型的值元信息描述，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#MapCollection}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Map_ValueMetaData extends Collection_ValueMetaData<MapEntity_ValueMetaData> {
    private HashMap<ValueMetaData, ValueMetaData> valueData = new HashMap<ValueMetaData, ValueMetaData>(); //数据
    /**该方法将会返回{@link PropertyMetaTypeEnum#MapCollection}。*/
    public String getPropertyType() {
        return PropertyMetaTypeEnum.MapCollection;
    }
    /**以Map形式返回集合中的数据。*/
    public Map<ValueMetaData, ValueMetaData> getCollectionValue() {
        return this.valueData;
    }
    /**添加一个元素。*/
    public void addObject(ValueMetaData key, ValueMetaData value) {
        this.valueData.put(key, value);
    };
    /**添加一个元素。*/
    public void addObject(MapEntity_ValueMetaData mapEntity) {
        this.valueData.put(mapEntity.getKey(), mapEntity.getValue());
    };
    /**删除一个元素。*/
    public void removeObject(ValueMetaData key) {
        this.valueData.remove(key);
    };
    /**获取集合当前数据内容条数数。*/
    public int size() {
        return this.valueData.size();
    };
}