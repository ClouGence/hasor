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
package net.hasor.core.aop;
//
//
public class ConstructorAopBean {
    private byte    byteValue;
    private short   shortValue;
    private int     intValue;
    private long    longValue;
    private float   floatValue;
    private double  doubleValue;
    private boolean booleanValue;
    private char    charValue;
    //
    public ConstructorAopBean(      //
            byte byteValue          //
            , short shortValue      //
            , int intValue          //
            , long longValue        //
            , float floatValue      //
            , double doubleValue    //
            , boolean booleanValue  //
            , char charValue) {
        //
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.intValue = intValue;
        this.longValue = longValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.booleanValue = booleanValue;
        this.charValue = charValue;
    }
    //
    //
    public byte getByteValue() {
        return byteValue;
    }
    public short getShortValue() {
        return shortValue;
    }
    public int getIntValue() {
        return intValue;
    }
    public long getLongValue() {
        return longValue;
    }
    public float getFloatValue() {
        return floatValue;
    }
    public double getDoubleValue() {
        return doubleValue;
    }
    public boolean isBooleanValue() {
        return booleanValue;
    }
    public char getCharValue() {
        return charValue;
    }
}
