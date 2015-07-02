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
package net.hasor.plugins.resource.loader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.hasor.plugins.resource.ResourceLoader;
/**
 * 用于创建一个可以从classpath中获取资源的ResourceLoader。
 * @version : 2013-6-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class ZipResourceLoader implements ResourceLoader {
    private File        zipFile     = null;
    private Set<String> zipEntrySet = new HashSet<String>();
    public ZipResourceLoader(String zipFile) throws IOException {
        this.zipFile = new File(zipFile);
        ZipFile zipFileObj = new ZipFile(this.zipFile);
        Enumeration<? extends ZipEntry> entEnum = zipFileObj.entries();
        while (entEnum.hasMoreElements()) {
            this.zipEntrySet.add(entEnum.nextElement().getName());
        }
        zipFileObj.close();
    }
    /**获取资源获取的包路径。*/
    public String getZipFile() {
        return this.zipFile.getAbsolutePath();
    }
    private String formatResourcePath(String resourcePath) {
        if (resourcePath.charAt(0) == '/') {
            resourcePath = resourcePath.substring(1);
        }
        resourcePath = resourcePath.replaceAll("/{2}", "/");
        return resourcePath;
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        if (this.zipFile.isDirectory() == true || this.zipFile.exists() == false) {
            return null;
        }
        //
        resourcePath = formatResourcePath(resourcePath);
        if (!this.zipEntrySet.contains(resourcePath)) {
            return null;
        }
        //
        ZipFile zipFileObj = new ZipFile(this.zipFile);
        ZipEntry entry = zipFileObj.getEntry(resourcePath);
        return new ZipEntryInputStream(zipFileObj, zipFileObj.getInputStream(entry));
    }
    public boolean exist(String resourcePath) throws IOException {
        resourcePath = formatResourcePath(resourcePath);
        return this.zipEntrySet.contains(resourcePath);
    }
    private class ZipEntryInputStream extends InputStream {
        private InputStream targetInput;
        private ZipFile     zipFileObj;
        //
        public ZipEntryInputStream(ZipFile zipFileObj, InputStream targetInput) {
            this.zipFileObj = zipFileObj;
            this.targetInput = targetInput;
        }
        //
        public int read(byte[] b) throws IOException {
            return this.targetInput.read(b);
        }
        public int read(byte[] b, int off, int len) throws IOException {
            return this.targetInput.read(b, off, len);
        }
        public long skip(long n) throws IOException {
            return this.targetInput.skip(n);
        }
        public int available() throws IOException {
            return this.targetInput.available();
        }
        public void close() throws IOException {
            this.targetInput.close();
            this.zipFileObj.close();
        }
        public synchronized void mark(int readlimit) {
            this.targetInput.mark(readlimit);
        }
        public synchronized void reset() throws IOException {
            this.targetInput.reset();
        }
        public boolean markSupported() {
            return this.targetInput.markSupported();
        }
        public int read() throws IOException {
            return this.targetInput.read();
        }
    }
}