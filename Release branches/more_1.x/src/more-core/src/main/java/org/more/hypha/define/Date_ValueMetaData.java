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
 * 表示一个时间日期类型数据，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#Date}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Date_ValueMetaData extends AbstractValueMetaData {
    private String dateString   = null; //时间日期字符形式的数据
    private String formatString = null; //date属性的格式化字符串
    /**该方法将会返回{@link PropertyMetaTypeEnum#Date}。*/
    public String getMetaDataType() {
        return PropertyMetaTypeEnum.Date;
    }
    /**返回时间日期字符形式的数据*/
    public String getDateString() {
        return this.dateString;
    }
    /**设置时间日期字符形式的数据*/
    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
    /**返回date属性的格式化字符串*/
    public String getFormatString() {
        return this.formatString;
    }
    /**设置date属性的格式化字符串*/
    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }
}