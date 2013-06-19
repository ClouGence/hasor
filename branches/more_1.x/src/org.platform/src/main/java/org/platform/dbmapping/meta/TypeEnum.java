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
package org.platform.dbmapping.meta;
/**
 * 
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public enum TypeEnum {
    /**字符串*/
    TString("string"),
    /**浮点数*/
    TFloat("float"),
    /**整数*/
    TInteger("int"),
    /**双精度浮点数*/
    TDouble("double"),
    /**双精度整数*/
    TLong("long"),
    /**布尔值*/
    TBoolean("boolean"),
    /**时间日期类型*/
    TDatetime("datetime"),
    /**ID，类型*/
    TUUID("uuid"),
    /**字节类型*/
    TBtye("byte"),
    /**JSON数据*/
    TJson("json"),
    /**字节数组*/
    TBytes("bytes");
    //
    //
    //
    private String value = null;
    TypeEnum(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}
