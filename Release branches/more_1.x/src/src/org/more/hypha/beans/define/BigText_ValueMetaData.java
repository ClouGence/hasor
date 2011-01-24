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

import org.more.hypha.beans.PropertyMetaTypeEnum;

/**
 * 表示一个大文本数据段，通常使用CDATA来描述对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#BigText}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class BigText_ValueMetaData extends AbstractValueMetaData {
    private String textValue = null; //CDATA内容
    /**该方法将会返回{@link PropertyMetaTypeEnum#BigText}。*/
    public String getPropertyType() {
        return PropertyMetaTypeEnum.BigText;
    }
    /**获取CDATA值*/
    public String getTextValue() {
        return this.textValue;
    }
    /**设置CDATA值*/
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    };
}