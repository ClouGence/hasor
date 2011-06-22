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
public class TestBean_3 extends TestBean_2 {
    private String   p_el      = null;
    private TestEnum p_enum    = null;
    private Object   p_ref     = null;
    private File     p_file    = null;
    private File     p_dir     = null;
    private URI      p_uri     = null;
    private Date     p_date    = null;
    private String   p_bigText = null;
    //
    public String getP_el() {
        return p_el;
    }
    public void setP_el(String p_el) {
        this.p_el = p_el;
    }
    public TestEnum getP_enum() {
        return p_enum;
    }
    public void setP_enum(TestEnum p_enum) {
        this.p_enum = p_enum;
    }
    public Object getP_ref() {
        return p_ref;
    }
    public void setP_ref(Object p_ref) {
        this.p_ref = p_ref;
    }
    public File getP_file() {
        return p_file;
    }
    public void setP_file(File p_file) {
        this.p_file = p_file;
    }
    public File getP_dir() {
        return p_dir;
    }
    public void setP_dir(File p_dir) {
        this.p_dir = p_dir;
    }
    public URI getP_uri() {
        return p_uri;
    }
    public void setP_uri(URI p_uri) {
        this.p_uri = p_uri;
    }
    public Date getP_date() {
        return p_date;
    }
    public void setP_date(Date p_date) {
        this.p_date = p_date;
    }
    public String getP_bigText() {
        return p_bigText;
    }
    public void setP_bigText(String p_bigText) {
        this.p_bigText = p_bigText;
    }
}