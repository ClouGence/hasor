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
package org.more.webui.freemarker.loader.template;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.more.webui.freemarker.loader.ResourceLoader;
import org.more.webui.freemarker.loader.resource.MultiResourceLoader;
import freemarker.cache.TemplateLoader;
/**
* 实现{@link ResourceLoader}接口的{@link freemarker.cache.MultiTemplateLoader}类。
* 不润许继承的原因是当
* @version : 2011-9-17
* @author 赵永春 (zyc@byshell.org)
*/
public final class MultiTemplateLoader extends freemarker.cache.MultiTemplateLoader implements ResourceLoader {
    private MultiResourceLoader resourceLoader = new MultiResourceLoader();
    public MultiTemplateLoader(TemplateLoader[] loaders) {
        super(loaders);
        for (int i = 0; i < loaders.length; i++) {
            TemplateLoader loader = loaders[i];
            if (loader instanceof ResourceLoader == true)
                resourceLoader.addResourceLoader((ResourceLoader) loader);
        }
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        return this.resourceLoader.getResourceAsStream(resourcePath);
    }
    public URL getResource(String resourcePath) throws IOException {
        return this.resourceLoader.getResource(resourcePath);
    }
}