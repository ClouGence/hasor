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
package org.more.hypha.define;
/**
 * 表示一个基本类型数据，对应的PropertyMetaTypeEnum类型为{@link MetaTypeEnum#SimpleType}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Simple_ValueMetaData extends ValueMetaData {
    /*值类型*/
    private String type  = PropertyType.Null.value();
    /*值*/
    private String value = null;
    /*------------------------------------------------------------------*/
    /**值类型*/
    public String getType() {
        return type;
    }
    /**值类型*/
    public void setType(String type) {
        this.type = type;
    }
    /**获取值*/
    public String getValue() {
        return value;
    }
    /**设置值*/
    public void setValue(String value) {
        this.value = value;
    }
}