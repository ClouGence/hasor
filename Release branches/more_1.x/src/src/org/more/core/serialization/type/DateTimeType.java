/*
 * Copyright 2008-2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.more.core.serialization.type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.more.CastException;
import org.more.core.serialization.UserType;
/**
 * 用户自定义类型，该类型是“[More User DataTime]”
 * Date : 2009-7-8
 * @author 赵永春
 */
public class DateTimeType extends UserType {
    private String formatter = "yyyy-MM-dd hh:mm:ss";
    @Override
    protected String getUserOriginalName() {
        return "DataTime";
    }
    @Override
    protected String getType() {
        return "DataTime";
    }
    @Override
    public boolean testObject(Object object) {
        return object instanceof Date;
    }
    @Override
    protected String serialization(Object object) {
        return new SimpleDateFormat(this.formatter).format(object);
    }
    @Override
    protected Object deserialize(String date, String type) throws CastException {
        try {
            return new SimpleDateFormat(this.formatter).parse(date);
        } catch (ParseException e) {
            throw new CastException("无法格式化时间字符串");
        }
    }
    public String getFormatter() {
        return formatter;
    }
    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }
}
