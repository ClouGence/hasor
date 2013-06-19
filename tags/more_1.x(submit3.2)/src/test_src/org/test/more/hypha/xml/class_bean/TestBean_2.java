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
import java.io.File;
import java.net.URI;
import java.util.Date;
/**
 * 
 * @version 2011-6-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class TestBean_2 {
    public TestEnum p_enum    = null;
    public Object   p_ref     = null;
    public File     p_file    = null;
    public File     p_dir     = null;
    public URI      p_uri     = null;
    public Date     p_date    = null;
    public Object   p_el      = null;
    //
    public String   p_bigText = null;
    //
    public String   state     = null;
    //
    public TestBean_2() {
        System.out.println("create By C");
    };
    public TestBean_2(//
            Object p_el,//
            TestEnum p_enum,//
            Object p_ref,//
            File p_file,//
            File p_dir,//
            URI p_uri,//
            Date p_date,//
            String p_bigText) {
        System.out.println("create. By C Params");
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