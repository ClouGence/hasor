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
package net.hasor.core.binder.schema;
/**
 * 表示一个数组集合类型的值元信息描述。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Array_ValueMetaData extends Collection_ValueMetaData<ValueMetaData> {
    /*集合初始化大小*/
    private int initSize = 0;
    /*------------------------------------------------------------------*/
    /**返回{@link PropertyType#Array}*/
    @Override
    public String getType() {
        return PropertyType.Array.value();
    }
    /**获取集合初始化大小*/
    public int getInitSize() {
        return initSize;
    }
    /**设置集合初始化大小*/
    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }
}