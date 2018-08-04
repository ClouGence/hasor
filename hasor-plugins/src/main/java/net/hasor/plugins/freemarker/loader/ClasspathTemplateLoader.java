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
package net.hasor.plugins.freemarker.loader;
import net.hasor.utils.resource.loader.ClassPathResourceLoader;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
/**
 * 处理Classpath中的模板。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClasspathTemplateLoader extends URLClassLoader implements ITemplateLoader {
    private String                  packageName    = null;
    private ClassPathResourceLoader resourceLoader = null;
    //
    public ClasspathTemplateLoader() {
        this("", Thread.currentThread().getContextClassLoader());
    }
    //
    public ClasspathTemplateLoader(String packageName) {
        this(packageName, Thread.currentThread().getContextClassLoader());
    }
    //
    public ClasspathTemplateLoader(String packageName, ClassLoader parent) {
        super(new URL[0], parent);
        this.packageName = (packageName == null) ? "" : packageName;
        this.packageName = this.packageName.replace(".", "/");
        if (this.packageName.length() > 0)
            if (this.packageName.charAt(0) == '/')
                this.packageName = this.packageName.substring(1);
        this.resourceLoader = new ClassPathResourceLoader(this.packageName, this);
    }
    //
    public String getType() {
        return this.getClass().getSimpleName();
    }
    //
    public Object findTemplateSource(String name) throws IOException {
        StringBuffer $name = new StringBuffer(this.packageName);
        if (name.charAt(0) != '/') {
            $name.append("/");
        }
        $name.append(name);
        if ($name.charAt(0) == '/') {
            $name = $name.deleteCharAt(0);
        }
        return this.getResource($name.toString());
    }
    //
    public long getLastModified(Object templateSource) {
        URL url = (URL) templateSource;
        if (url.getProtocol().equals("file")) {
            return new File(url.getFile()).lastModified();
        }
        return 0;
    }
    //
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        URL url = (URL) templateSource;
        InputStream in = url.openStream();
        if (encoding == null) {
            return new InputStreamReader(in);
        } else {
            return new InputStreamReader(in, encoding);
        }
    }
    //
    public void closeTemplateSource(Object templateSource) throws IOException {
    }
    //
    public InputStream getResourceAsStream(String name) {
        return this.resourceLoader.getResourceAsStream(name);
    }
}