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
package org.hasor.freemarker.loader.resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 将一个File对象所代表的路径作为根路径，资源获取相对于该路径下。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class DirResourceLoader implements IResourceLoader {
    private File baseDir = null;
    public DirResourceLoader(File baseDir) throws IOException {
        this.baseDir = baseDir;
    }
    public URL getResource(String resourcePath) throws MalformedURLException {
        File resource = new File(this.baseDir, resourcePath);
        if (resource.canRead() == true)
            return resource.toURI().toURL();
        return null;
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        File resource = new File(this.baseDir, resourcePath);
        if (resource.canRead() == true)
            return new FileInputStream(resource);
        return null;
    }
}