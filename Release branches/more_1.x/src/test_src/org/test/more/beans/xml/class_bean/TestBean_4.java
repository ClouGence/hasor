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
package org.test.more.beans.xml.class_bean;
import java.io.File;
import java.net.URI;
import java.util.Date;
/**
 * 
 * @version 2011-6-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class TestBean_4 {
    private Object   p_null    = new Object();
    private boolean  p_boolean = false;
    private byte     p_byte    = 0;
    private short    p_short   = 0;
    private int      p_integer = 0;
    private long     p_long    = 0;
    private float    p_float   = 0;
    private double   p_double  = 0;
    private char     p_char    = 0;
    private String   p_str     = null;
    //
    private Object   p_el      = null;
    private TestEnum p_enum    = null;
    private Object   p_ref     = null;
    private File     p_file    = null;
    private File     p_dir     = null;
    private URI      p_uri     = null;
    private Date     p_date    = null;
    private String   p_bigText = null;
    //
    public TestBean_4(//
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
    public TestBean_4(//
            Object p_el,//
            TestEnum p_enum,//
            Object p_ref,//
            File p_file,//
            File p_dir,//
            URI p_uri,//
            Date p_date,//
            String p_bigText) {
        this.p_el = p_el;
        this.p_enum = p_enum;
        this.p_ref = p_ref;
        this.p_file = p_file;
        this.p_dir = p_dir;
        this.p_uri = p_uri;
        this.p_date = p_date;
        this.p_bigText = p_bigText;
    };
}