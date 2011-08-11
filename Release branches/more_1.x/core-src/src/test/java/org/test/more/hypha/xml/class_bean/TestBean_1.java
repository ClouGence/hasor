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
package org.test.more.hypha.xml.class_bean;
/**
 * 
 * @version 2011-6-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class TestBean_1 {
    public String  state     = null;
    //
    public Object  p_null    = new Object();
    public boolean p_boolean = false;
    public byte    p_byte    = 0;
    public short   p_short   = 0;
    public int     p_integer = 0;
    public long    p_long    = 0;
    public float   p_float   = 0;
    public double  p_double  = 0;
    public char    p_char    = 0;
    public String  p_str     = null;
    //
    public TestBean_1() {
        System.out.println("create By C");
    };
    public TestBean_1(//
            Object p_null,//
            boolean p_boolean, //
            byte p_byte, //
            short p_short, //
            int p_integer,//
            long p_long, //
            float p_float, //
            double p_double, //
            char p_char,//
            String p_str) {
        System.out.println("create. By C Params");
        this.p_null = p_null;
        this.p_boolean = p_boolean;
        this.p_byte = p_byte;
        this.p_short = p_short;
        this.p_integer = p_integer;
        this.p_long = p_long;
        this.p_float = p_float;
        this.p_double = p_double;
        this.p_char = p_char;
        this.p_str = p_str;
    };
}