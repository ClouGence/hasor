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
package net.hasor.utils.resource.loader;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.utils.resource.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于创建一个可以从classpath中获取资源的ResourceLoader。
 * @version : 2013-6-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class ClassPathResourceLoader implements ResourceLoader {
    private String            packageName = null;
    private ClassLoader       classLoader = null;
    private Map<String, Long> sizeCache   = null;

    /***/
    public ClassPathResourceLoader(String packageName) {
        this.packageName = packageName;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    /***/
    public ClassPathResourceLoader(String packageName, ClassLoader classLoader) {
        this.packageName = packageName;
        this.classLoader = classLoader;
    }

    /**获取资源获取的包路径。*/
    public String getPackageName() {
        return this.packageName;
    }

    /**获取装载资源使用的类装载器。*/
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    private String formatResourcePath(String resourcePath) {
        String path = this.packageName + (resourcePath.charAt(0) == '/' ? resourcePath : "/" + resourcePath);
        path = path.replaceAll("/{2}", "/");
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        return path;
    }

    public InputStream getResourceAsStream(String resourcePath) {
        if (StringUtils.isBlank(resourcePath)) {
            return null;
        }
        return this.classLoader.getResourceAsStream(formatResourcePath(resourcePath));
    }

    public boolean canModify(String resourcePath) {
        if (StringUtils.isBlank(resourcePath)) {
            return false;
        }
        URL url = this.classLoader.getResource(formatResourcePath(resourcePath));
        if (url != null && url.getProtocol().contains("file")) {
            return true;
        }
        return false;
    }

    public boolean exist(String resourcePath) {
        if (StringUtils.isBlank(resourcePath)) {
            return false;
        }
        URL url = this.classLoader.getResource(formatResourcePath(resourcePath));
        return !(url == null);
    }

    @Override
    public long getResourceSize(String resourcePath) throws IOException {
        try (InputStream inputStream = getResourceAsStream(resourcePath)) {
            SizeOutputStream sizeOutputStream = new SizeOutputStream();
            IOUtils.copy(inputStream, sizeOutputStream);
            return sizeOutputStream.currentSize();
        }
    }

    public URL getResource(String resourcePath) {
        return this.classLoader.getResource(formatResourcePath(resourcePath));
    }

    private static class SizeOutputStream extends OutputStream {
        private AtomicLong atomicLong = new AtomicLong();

        public long currentSize() {
            return atomicLong.get();
        }

        @Override
        public void write(int b) throws IOException {
            this.atomicLong.incrementAndGet();
        }

        public void write(byte[] b) throws IOException {
            if (b != null) {
                this.atomicLong.addAndGet(b.length);
            }
        }
    }
}
