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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import net.hasor.plugins.resource.ResourceLoader;
import org.more.util.StringUtils;
/**
 * 用于创建一个可以从classpath中获取资源的ResourceLoader。
 * @version : 2013-6-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class ClassPathResourceLoader implements ResourceLoader {
    private String      packageName = null;
    private ClassLoader classLoader = null;
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
    public boolean canModify(String resourcePath) throws IOException {
        if (StringUtils.isBlank(resourcePath)) {
            return false;
        }
        URL url = this.classLoader.getResource(formatResourcePath(resourcePath));
        if (url.getProtocol().contains("file")) {
            return true;
        }
        return false;
    }
    public boolean exist(String resourcePath) throws IOException {
        if (StringUtils.isBlank(resourcePath)) {
            return false;
        }
        URL url = this.classLoader.getResource(formatResourcePath(resourcePath));
        return !(url == null);
    }
    public URL getResource(String resourcePath) throws IOException {
        URL url = this.classLoader.getResource(formatResourcePath(resourcePath));
        return url;
    }
}