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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.hasor.plugins.resource.ResourceLoader;
/**
 * 
 * @version : 2015年7月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class MultiResourceLoader implements ResourceLoader {
    private final List<ResourceLoader>                  loaders           = new CopyOnWriteArrayList<ResourceLoader>();
    private final ConcurrentMap<String, ResourceLoader> lastLoaderForName = new ConcurrentHashMap<String, ResourceLoader>();
    /** Creates a new empty multi resource Loader. */
    public MultiResourceLoader() {
        this(new ResourceLoader[0]);
    }
    /**
     * Creates a new multi resource Loader that will use the specified loaders.
     * @param loaders the loaders that are used to load resources. 
     */
    public MultiResourceLoader(ResourceLoader[] loaders) {
        for (int i = 0; i < loaders.length; i++) {
            this.loaders.add(loaders[i]);
        }
    }
    /**添加一个{@link ResourceLoader}。*/
    public void addResourceLoader(ResourceLoader loader) {
        if (loaders.contains(loader) == false) {
            this.loaders.add(loader);
        }
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        ResourceLoader loader = findLoader(resourcePath);
        if (loader != null) {
            InputStream inStream = loader.getResourceAsStream(resourcePath);
            return inStream;
        }
        return null;
    }
    //
    protected ResourceLoader findLoader(String resourcePath) throws IOException {
        ResourceLoader loader = this.lastLoaderForName.get(resourcePath);
        if (loader == null) {
            for (ResourceLoader loads : this.loaders) {
                if (loads != null && loads.exist(resourcePath)) {
                    loader = lastLoaderForName.putIfAbsent(resourcePath, loads);
                    if (loader == null) {
                        loader = loads;
                    }
                }
            }
        }
        return loader;
    }
    //
    public boolean exist(String resourcePath) throws IOException {
        return findLoader(resourcePath) != null;
    }
    //
    public URL getResource(String resourcePath) throws IOException {
        ResourceLoader loader = findLoader(resourcePath);
        if (loader != null) {
            return loader.getResource(resourcePath);
        }
        return null;
    }
}