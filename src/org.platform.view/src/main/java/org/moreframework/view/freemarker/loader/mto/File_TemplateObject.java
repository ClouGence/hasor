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
package org.moreframework.view.freemarker.loader.mto;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import org.more.util.io.AutoCloseInputStream;
/**
 * 处理文件的{@link AbstractTemplateObject}实现
 * @version : 2011-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class File_TemplateObject implements AbstractTemplateObject {
    private File filePath = null;
    //
    public File_TemplateObject(File filePath) {
        this.filePath = filePath;
    }
    public URL getResource(String name) throws MalformedURLException {
        return this.filePath.toURI().toURL();
    }
    public InputStream getInputStream() throws IOException {
        return new AutoCloseInputStream(new FileInputStream(filePath));
    }
    public Reader getReader(String encoding) throws IOException {
        InputStream is = this.getInputStream();
        //
        String $encoding = encoding;
        if ($encoding == null)
            $encoding = DefaultEncoding;
        return new InputStreamReader(is, $encoding);
    }
    public long lastModified() {
        return this.filePath.lastModified();
    }
    public void openObject() {
        // TODO Auto-generated method stub
    }
    public void closeObject() {
        // TODO Auto-generated method stub
    }
};