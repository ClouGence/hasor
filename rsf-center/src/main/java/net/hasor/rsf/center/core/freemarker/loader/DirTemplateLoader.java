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
package net.hasor.rsf.center.core.freemarker.loader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import net.hasor.plugins.resource.loader.PathResourceLoader;
import freemarker.cache.FileTemplateLoader;
/**
 * 实现了{@link IResourceLoader}接口的{@link FileTemplateLoader}类。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class DirTemplateLoader extends FileTemplateLoader implements ITemplateLoader {
    private PathResourceLoader dirResourceLoader = null;
    //
    public DirTemplateLoader(File templateDir) throws IOException {
        super(templateDir);
        this.dirResourceLoader = new PathResourceLoader(this.baseDir.getAbsolutePath());
    }
    public String getType() {
        return this.getClass().getSimpleName();
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        return this.dirResourceLoader.getResourceAsStream(resourcePath);
    }
    public URL getResource(String resourcePath) throws IOException {
        return this.dirResourceLoader.getResource(resourcePath);
    }
}