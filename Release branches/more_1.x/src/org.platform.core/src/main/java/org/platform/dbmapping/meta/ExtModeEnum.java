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
 * 属性扩展模式。
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public enum ExtModeEnum {
    /**column将列名作为扩展属性*/
    Column("column"),
    /**row将记录作为扩展属性*/
    Row("row");
    //
    //
    //
    private String value = null;
    ExtModeEnum(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}