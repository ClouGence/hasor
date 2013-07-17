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
package org.hasor.freemarker.loader.mto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
/**
 * ×Ö·û´®Ä£°å
 * @version : 2011-9-16
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class String_TemplateObject implements AbstractTemplateObject {
    private String templateString = null;
    //
    public String_TemplateObject(String templateString) {
        this.templateString = templateString;
    }
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.templateString.getBytes());
    }
    public Reader getReader(String encoding) throws IOException {
        InputStream in = this.getInputStream();
        if (encoding == null)
            return new InputStreamReader(in, DefaultEncoding);
        else
            return new InputStreamReader(in, encoding);
    }
    public long lastModified() {
        return new Date().getTime();
    }
    public void openObject() {
        // TODO Auto-generated method stub
    }
    public void closeObject() {
        // TODO Auto-generated method stub
    }
};