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
import net.hasor.utils.resource.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @version : 2015年7月1日
 * @author 赵永春 (zyc@byshell.org)
 */
public class MultiResourceLoader implements ResourceLoader {
    private final List<ResourceLoader>                  loaders           = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<String, ResourceLoader> lastLoaderForName = new ConcurrentHashMap<>();

    /** Creates a new empty multi resource Loader. */
    public MultiResourceLoader() {
        this(new ResourceLoader[0]);
    }

    /**
     * Creates a new multi resource Loader that will use the specified loaders.
     * @param loaders the loaders that are used to load resources.
     */
    public MultiResourceLoader(ResourceLoader[] loaders) {
        this.loaders.addAll(Arrays.asList(loaders));
    }

    /**添加一个{@link ResourceLoader}。*/
    public void addResourceLoader(ResourceLoader loader) {
        if (!loaders.contains(loader)) {
            this.loaders.add(loader);
        }
    }

    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        ResourceLoader loader = findLoader(resourcePath);
        if (loader != null) {
            return loader.getResourceAsStream(resourcePath);
        }
        return null;
    }

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

    public boolean exist(String resourcePath) throws IOException {
        return findLoader(resourcePath) != null;
    }

    @Override
    public long getResourceSize(String resourcePath) throws IOException {
        ResourceLoader loader = findLoader(resourcePath);
        if (loader == null) {
            return -1;
        }
        return loader.getResourceSize(resourcePath);
    }

    public URL getResource(String resourcePath) throws IOException {
        ResourceLoader loader = findLoader(resourcePath);
        if (loader != null) {
            return loader.getResource(resourcePath);
        }
        return null;
    }
}
