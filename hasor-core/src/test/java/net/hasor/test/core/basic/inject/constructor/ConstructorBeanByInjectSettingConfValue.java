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
package net.hasor.test.core.basic.inject.constructor;
import net.hasor.core.InjectSettings;
import net.hasor.test.core.enums.SelectEnum;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

//
// 8 种基本类型和其包装类型，以及4种时间类型和常用的枚举、字符串
//
public class ConstructorBeanByInjectSettingConfValue {
    private byte          byteValue;
    private Byte          byteValue2;
    //
    private short         shortValue;
    private Short         shortValue2;
    //
    private int           intValue;
    private Integer       intValue2;
    //
    private long          longValue;
    private Long          longValue2;
    //
    private float         floatValue;
    private Float         floatValue2;
    //
    private double        doubleValue;
    private Double        doubleValue2;
    //
    private boolean       booleanValue;
    private Boolean       booleanValue2;
    //
    private char          charValue;
    private Character     charValue2;
    //
    private Date          dateValue1;
    private java.sql.Date dateValue2;
    private Time          dateValue3;
    private Timestamp     dateValue4;
    //
    private String        stringValue;
    private SelectEnum    enumValue;

    //
    public ConstructorBeanByInjectSettingConfValue(//
            @InjectSettings("byteValue") byte byteValue             //
            , @InjectSettings("byteValue") Byte byteValue2          //
            , @InjectSettings("shortValue") short shortValue        //
            , @InjectSettings("shortValue") Short shortValue2       //
            , @InjectSettings("intValue") int intValue              //
            , @InjectSettings("intValue") Integer intValue2         //
            , @InjectSettings("longValue") long longValue           //
            , @InjectSettings("longValue") Long longValue2          //
            , @InjectSettings("floatValue") float floatValue        //
            , @InjectSettings("floatValue") Float floatValue2       //
            , @InjectSettings("doubleValue") double doubleValue     //
            , @InjectSettings("doubleValue") Double doubleValue2    //
            , @InjectSettings("booleanValue") boolean booleanValue  //
            , @InjectSettings("booleanValue") Boolean booleanValue2 //
            , @InjectSettings("charValue") char charValue           //
            , @InjectSettings("charValue") Character charValue2     //
            , @InjectSettings("dateValue") Date dateValue1          //
            , @InjectSettings("dateValue") java.sql.Date dateValue2 //
            , @InjectSettings("dateValue") Time dateValue3          //
            , @InjectSettings("dateValue") Timestamp dateValue4     //
            , @InjectSettings("stringValue") String stringValue     //
            , @InjectSettings("enumValue") SelectEnum enumValue) {
        //
        this.byteValue = byteValue;
        this.byteValue2 = byteValue2;
        this.shortValue = shortValue;
        this.shortValue2 = shortValue2;
        this.intValue = intValue;
        this.intValue2 = intValue2;
        this.longValue = longValue;
        this.longValue2 = longValue2;
        this.floatValue = floatValue;
        this.floatValue2 = floatValue2;
        this.doubleValue = doubleValue;
        this.doubleValue2 = doubleValue2;
        this.booleanValue = booleanValue;
        this.booleanValue2 = booleanValue2;
        this.charValue = charValue;
        this.charValue2 = charValue2;
        this.dateValue1 = dateValue1;
        this.dateValue2 = dateValue2;
        this.dateValue3 = dateValue3;
        this.dateValue4 = dateValue4;
        this.stringValue = stringValue;
        this.enumValue = enumValue;
    }

    //
    //
    //
    public byte getByteValue() {
        return byteValue;
    }

    public Byte getByteValue2() {
        return byteValue2;
    }

    public short getShortValue() {
        return shortValue;
    }

    public Short getShortValue2() {
        return shortValue2;
    }

    public int getIntValue() {
        return intValue;
    }

    public Integer getIntValue2() {
        return intValue2;
    }

    public long getLongValue() {
        return longValue;
    }

    public Long getLongValue2() {
        return longValue2;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public Float getFloatValue2() {
        return floatValue2;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public Double getDoubleValue2() {
        return doubleValue2;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public Boolean getBooleanValue2() {
        return booleanValue2;
    }

    public char getCharValue() {
        return charValue;
    }

    public Character getCharValue2() {
        return charValue2;
    }

    public Date getDateValue1() {
        return dateValue1;
    }

    public java.sql.Date getDateValue2() {
        return dateValue2;
    }

    public Time getDateValue3() {
        return dateValue3;
    }

    public Timestamp getDateValue4() {
        return dateValue4;
    }

    public String getStringValue() {
        return stringValue;
    }

    public SelectEnum getEnumValue() {
        return enumValue;
    }
}
