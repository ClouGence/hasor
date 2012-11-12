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
 * 表示一个连接数据，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#URI}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class URI_ValueMetaData extends AbstractValueMetaData {
    private String uriObject = null; //表示连接的url字符数据
    /**该方法将会返回{@link PropertyMetaTypeEnum#URI}。*/
    public String getMetaDataType() {
        return PropertyMetaTypeEnum.URI;
    }
    public String getUriObject() {
        return this.uriObject;
    }
    /**设置表示连接的uri数据*/
    public void setUriObject(String uriObject) {
        this.uriObject = uriObject;
    }
}