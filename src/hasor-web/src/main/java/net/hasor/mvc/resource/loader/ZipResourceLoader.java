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
package net.hasor.mvc.resource.loader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.hasor.mvc.resource.ResourceLoader;
/**
 * 用于创建一个可以从classpath中获取资源的ResourceLoader。
 * @version : 2013-6-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class ZipResourceLoader implements ResourceLoader {
    private File zipFile = null;
    public ZipResourceLoader(String zipFile) throws IOException {
        this.zipFile = new File(zipFile);
    }
    /**获取资源获取的包路径。*/
    public String getZipFile() {
        return this.zipFile.getAbsolutePath();
    }
    public InputStream getResourceAsStream(String name) throws IOException {
        if (this.zipFile.isDirectory() == true || this.zipFile.exists() == false)
            return null;
        //
        if (name.charAt(0) == '/')
            name = name.substring(1);
        ZipFile zipFileObj = new ZipFile(this.zipFile);
        ZipEntry entry = zipFileObj.getEntry(name);
        if (entry == null)
            return null;
        return zipFileObj.getInputStream(entry);
    }
}