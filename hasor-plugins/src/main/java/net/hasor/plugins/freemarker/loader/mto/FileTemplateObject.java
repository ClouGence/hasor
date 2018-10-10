/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.plugins.freemarker.loader.mto;
import net.hasor.utils.io.AutoCloseInputStream;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 处理文件的{@link AbstractTemplateObject}实现
 * @version : 2011-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class FileTemplateObject implements AbstractTemplateObject {
    private File filePath = null;
    //
    public FileTemplateObject(File filePath) {
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
}