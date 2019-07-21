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
package net.hasor.test.beans.basic.inject.property;
import net.hasor.test.beans.enums.SelectEnum;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

//
// 8 种基本类型和其包装类型，以及4种时间类型和常用的枚举、字符串
//
public class PropertyBean {
    //
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
    //
    //

    public byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte byteValue) {
        this.byteValue = byteValue;
    }

    public Byte getByteValue2() {
        return byteValue2;
    }

    public void setByteValue2(Byte byteValue2) {
        this.byteValue2 = byteValue2;
    }

    public short getShortValue() {
        return shortValue;
    }

    public void setShortValue(short shortValue) {
        this.shortValue = shortValue;
    }

    public Short getShortValue2() {
        return shortValue2;
    }

    public void setShortValue2(Short shortValue2) {
        this.shortValue2 = shortValue2;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public Integer getIntValue2() {
        return intValue2;
    }

    public void setIntValue2(Integer intValue2) {
        this.intValue2 = intValue2;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public Long getLongValue2() {
        return longValue2;
    }

    public void setLongValue2(Long longValue2) {
        this.longValue2 = longValue2;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public Float getFloatValue2() {
        return floatValue2;
    }

    public void setFloatValue2(Float floatValue2) {
        this.floatValue2 = floatValue2;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Double getDoubleValue2() {
        return doubleValue2;
    }

    public void setDoubleValue2(Double doubleValue2) {
        this.doubleValue2 = doubleValue2;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Boolean getBooleanValue2() {
        return booleanValue2;
    }

    public void setBooleanValue2(Boolean booleanValue2) {
        this.booleanValue2 = booleanValue2;
    }

    public char getCharValue() {
        return charValue;
    }

    public void setCharValue(char charValue) {
        this.charValue = charValue;
    }

    public Character getCharValue2() {
        return charValue2;
    }

    public void setCharValue2(Character charValue2) {
        this.charValue2 = charValue2;
    }

    public Date getDateValue1() {
        return dateValue1;
    }

    public void setDateValue1(Date dateValue1) {
        this.dateValue1 = dateValue1;
    }

    public java.sql.Date getDateValue2() {
        return dateValue2;
    }

    public void setDateValue2(java.sql.Date dateValue2) {
        this.dateValue2 = dateValue2;
    }

    public Time getDateValue3() {
        return dateValue3;
    }

    public void setDateValue3(Time dateValue3) {
        this.dateValue3 = dateValue3;
    }

    public Timestamp getDateValue4() {
        return dateValue4;
    }

    public void setDateValue4(Timestamp dateValue4) {
        this.dateValue4 = dateValue4;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public SelectEnum getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(SelectEnum enumValue) {
        this.enumValue = enumValue;
    }
}
