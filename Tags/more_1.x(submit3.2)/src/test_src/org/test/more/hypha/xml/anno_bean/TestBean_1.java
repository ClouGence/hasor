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
package org.test.more.hypha.xml.anno_bean;
import org.more.hypha.anno.define.Bean;
import org.more.hypha.anno.define.Property;
/**
 * 
 * @version 2011-6-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Bean(id = "anno_bean_1")
public class TestBean_1 {
    @Property()
    public Object  p_null    = new Object();
    @Property(value = "false")
    public boolean p_boolean = false;
    @Property(value = "123")
    public byte    p_byte    = 0;
    @Property(value = "12345")
    public short   p_short   = 0;
    @Property(value = "123456")
    public int     p_integer = 0;
    @Property(value = "654321")
    public long    p_long    = 0;
    @Property(value = "123.45")
    public float   p_float   = 0;
    @Property(value = "1.2345")
    public double  p_double  = 0;
    @Property(value = "{")
    public char    p_char    = 0;
    @Property(value = "≤‚ ‘◊÷∑˚¥Æ...")
    public String  p_str     = null;
}