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
package org.more.core.copybean.type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.more.core.copybean.ConvertType;
/**
 * CopyBean处理Date类型转换的辅助类。
 * @version 2009-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class DateConvertType extends ConvertType {
    /**  */
    private static final long serialVersionUID         = -5864989968961653320L;
    /** 当遇到传入的值为空时，返回null。 */
    public final static int   DefaultType_Null         = 0;
    /** 当遇到传入的值为空时，返回staticDefault属性的时间。 */
    public final static int   DefaultType_DefaultValue = 1;
    /** 当遇到传入的值为空时，创建一个新时间返回。 */
    public final static int   DefaultType_NewDate      = 2;
    /** 默认时间格式 */
    private String            format                   = "yyyy-MM-dd hh:mm:ss";
    /** 时间格式的默认类型。 */
    private Date              staticDefault            = null;
    /** 当准备转换的时间为空时其默认值策略是什么，该策略是DateConvertType.DefaultType_制定的 */
    private int               defaultType              = DateConvertType.DefaultType_NewDate;
    /**
     * 获得当准备转换的时间为空时其默认值策略。
     * @return 返回当准备转换的时间为空时默认值换策略。
     */
    public int getDefaultType() {
        return defaultType;
    }
    /**
     * 设置当准备转换的时间为空时默认值换策略。
     * @param defaultType 默认值换策略
     */
    public void setDefaultType(int defaultType) {
        this.defaultType = defaultType;
    }
    @Override
    public boolean checkType(Object from, Class<?> to) {
        return (to == Date.class) ? true : false;
    }
    @Override
    public Object convert(Object object) {
        if (object == null)
            return getDefaultValue();
        if (object instanceof Date)
            return object;
        else
            try {
                return new SimpleDateFormat(this.format).parse(object.toString());
            } catch (ParseException e) {
                return getDefaultValue();
            }
    }
    private Date getDefaultValue() {
        switch (this.defaultType) {
        case DateConvertType.DefaultType_Null:
            return null;
        case DateConvertType.DefaultType_DefaultValue:
            return this.staticDefault;
        case DateConvertType.DefaultType_NewDate:
            return new Date();
        }
        return null;
    }
    /**
     * 获得时间日期格式。
     * @return 返回时间日期格式。
     */
    public String getFormat() {
        return format;
    }
    /**
     * 设置时间日期格式。
     * @param format 要设置的时间日期格式。
     */
    public void setFormat(String format) {
        this.format = format;
    }
}