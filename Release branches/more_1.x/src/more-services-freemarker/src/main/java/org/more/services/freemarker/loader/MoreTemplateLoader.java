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
package org.more.services.freemarker.loader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import org.more.services.freemarker.FreemarkerService;
import freemarker.cache.TemplateLoader;
/**
 * 
 * @version : 2011-9-14
 * @author ’‘”¿¥∫ (zyc@byshell.org) 
 */
public class MoreTemplateLoader implements TemplateLoader {
    public MoreTemplateLoader(FreemarkerService freemarkerService) {
        // TODO Auto-generated constructor stub
    }
    public void closeTemplateSource(Object arg0) throws IOException {
        // TODO Auto-generated method stub
    }
    public Object findTemplateSource(String arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    public long getLastModified(Object arg0) {
        // TODO Auto-generated method stub
        return 0;
    }
    public Reader getReader(Object arg0, String arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    public void addTemplate(String name, String path) {
        // TODO Auto-generated method stub
    }
    public void addTemplate(String name, File path) {
        // TODO Auto-generated method stub
    }
    public void addTemplateAsString(String name, String templateString) {
        // TODO Auto-generated method stub
    }
}