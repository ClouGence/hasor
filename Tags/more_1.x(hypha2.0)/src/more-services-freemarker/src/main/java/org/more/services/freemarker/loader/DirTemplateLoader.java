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
package org.more.services.freemarker.loader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.more.services.freemarker.ResourceLoader;
import freemarker.cache.FileTemplateLoader;
/**
 * 扩展了{@link ResourceLoader}接口的{@link FileTemplateLoader}类。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class DirTemplateLoader extends FileTemplateLoader implements ResourceLoader {
    public DirTemplateLoader(File templateDir) throws IOException {
        super(templateDir);
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        File resource = new File(this.baseDir, resourcePath);
        if (resource.canRead() == true)
            return new FileInputStream(resource);
        return null;
    }
}