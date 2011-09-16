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
package org.more.services.freemarker.loader.mto;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import org.more.util.ResourcesUtil;
/**
 * 装载ClassPath中的模板
 * @version : 2011-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassPath_TemplateObject implements AbstractTemplateObject {
    private String classPath = null;
    private String encoding  = null;
    //
    public ClassPath_TemplateObject(String classPath, String encoding) {
        this.classPath = classPath;
        this.encoding = encoding;
    }
    public long lastModified() {
        return new Date().getTime();
    }
    public Reader getReader(String encoding) throws IOException {
        InputStream is = ResourcesUtil.getResourceAsStream(this.classPath);
        //
        String $encoding = encoding;
        if ($encoding == null)
            $encoding = this.encoding;
        if ($encoding == null)
            return new InputStreamReader(is);
        else
            return new InputStreamReader(is, encoding);
    }
    public void openObject() throws IOException {}
    public void closeObject() throws IOException {}
};