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
package org.hasor.freemarker.loader.mto;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Date;
import org.more.util.io.AutoCloseInputStream;
/**
 * 处理URL的{@link AbstractTemplateObject}实现
 * @version : 2011-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class URL_TemplateObject implements AbstractTemplateObject {
    private URL url = null;
    //
    public URL_TemplateObject(URL url) {
        this.url = url;
    }
    public InputStream getInputStream() throws IOException {
        return new AutoCloseInputStream(this.url.openStream());
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
        return new Date().getTime();
    }
    public void openObject() {
        // TODO Auto-generated method stub
    }
    public void closeObject() {
        // TODO Auto-generated method stub
    }
};