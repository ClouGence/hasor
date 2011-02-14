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
import java.util.ArrayList;
import org.more.hypha.beans.PropertyMetaTypeEnum;
/**
 * 表示一个数组集合类型的值元信息描述，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#ArrayCollection}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Array_ValueMetaData extends Collection_ValueMetaData<AbstractValueMetaData> {
    private ArrayList<AbstractValueMetaData> valueData = new ArrayList<AbstractValueMetaData>(); //数据
    /**该方法将会返回{@link PropertyMetaTypeEnum#ArrayCollection}。*/
    public String getMetaDataType() {
        return ArrayCollection;
    }
    /**以数组形式返回集合中的数据。*/
    public AbstractValueMetaData[] getCollectionValue() {
        AbstractValueMetaData[] values = new AbstractValueMetaData[this.valueData.size()];
        this.valueData.toArray(values);
        return values;
    }
    /**添加一个元素。*/
    public void addObject(AbstractValueMetaData value) {
        this.valueData.add(value);
    };
    /**删除一个元素。*/
    public void removeObject(AbstractValueMetaData value) {
        this.valueData.remove(value);
    };
    /**获取集合当前数据内容条数数。*/
    public int size() {
        return this.valueData.size();
    };
}