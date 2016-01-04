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
package net.hasor.plugins.templates.engine.velocity;
import java.io.IOException;
import java.io.Reader;
import freemarker.cache.FileTemplateLoader;
import net.hasor.plugins.templates.TemplateLoader;
/**
 * 实现了{@link IResourceLoader}接口的{@link FileTemplateLoader}类。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class TemplateLoaderWrap implements freemarker.cache.TemplateLoader, freemarker.cache.StatefulTemplateLoader {
    private TemplateLoader templateLoader = null;
    //
    public TemplateLoaderWrap(TemplateLoader templateLoader) throws IOException {
        this.templateLoader = templateLoader;
    }
    @Override
    public Object findTemplateSource(String name) throws IOException {
        return templateLoader.findTemplateSource(name);
    }
    @Override
    public long getLastModified(Object templateSource) {
        return templateLoader.getLastModified(templateSource);
    }
    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        templateLoader.closeTemplateSource(templateSource);
    }
    @Override
    public void resetState() {
        templateLoader.resetState();
    }
}