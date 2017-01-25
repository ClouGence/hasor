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
package org.more.bizcommon.resource.loader;
import org.more.bizcommon.resource.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
/**
 * 将一个File对象所代表的路径作为根路径，资源获取相对于该路径下。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class PathResourceLoader implements ResourceLoader {
    private String dirPath = null;
    public PathResourceLoader(String dirPath) {
        this.dirPath = dirPath;
    }
    private String formatResourcePath(String resourcePath) {
        String $resourcePath = this.dirPath + "/" + resourcePath;
        $resourcePath = $resourcePath.replaceAll("/{2}", "/");
        return $resourcePath;
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        resourcePath = formatResourcePath(resourcePath);
        File file = new File(resourcePath);
        if (file.exists() && file.isFile()) {
            return new FileInputStream(file);
        }
        return null;
    }
    public boolean exist(String resourcePath) {
        resourcePath = formatResourcePath(resourcePath);
        File file = new File(resourcePath);
        return (file.exists() && file.isFile());
    }
    public URL getResource(String resourcePath) throws IOException {
        File file = new File(resourcePath);
        if (file.exists() && file.isFile()) {
            return file.toURI().toURL();
        }
        return null;
    }
}