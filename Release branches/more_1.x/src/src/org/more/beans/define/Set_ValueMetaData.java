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
package org.more.beans.define;
import java.util.HashSet;
import java.util.Set;
import org.more.beans.ValueMetaData;
import org.more.beans.ValueMetaData.PropertyMetaTypeEnum;
/**
 * 表示一个{@link Set}类型的值元信息描述，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#SetCollection}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Set_ValueMetaData extends Collection_ValueMetaData<ValueMetaData> {
    private HashSet<ValueMetaData> valueData = new HashSet<ValueMetaData>(); //数据
    /**该方法将会返回{@link PropertyMetaTypeEnum#SetCollection}。*/
    public PropertyMetaTypeEnum getPropertyType() {
        return PropertyMetaTypeEnum.SetCollection;
    }
    /**以Set形式返回集合中的数据。*/
    public Set<ValueMetaData> getCollectionValue() {
        return this.valueData;
    }
    /**添加一个元素。*/
    public void addObject(ValueMetaData value) {
        this.valueData.add(value);
    };
    /**删除一个元素。*/
    public void removeObject(ValueMetaData value) {
        this.valueData.remove(value);
    };
    /**获取集合当前数据内容条数数。*/
    public int size() {
        return this.valueData.size();
    };
}