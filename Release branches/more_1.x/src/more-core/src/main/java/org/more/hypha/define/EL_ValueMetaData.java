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
 * 表示EL字符串类型数据，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#EL}。
 * @version 2010-11-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class EL_ValueMetaData extends ValueMetaData {
    private String elText = null; //表示EL字符串
    /**该方法将会返回{@link PropertyMetaTypeEnum#EL}。*/
    public String getMetaDataType() {
        return PropertyMetaTypeEnum.EL;
    }
    /**获取EL字符串*/
    public String getElText() {
        return elText;
    }
    /**设置EL字符串*/
    public void setElText(String elText) {
        this.elText = elText;
    }
}