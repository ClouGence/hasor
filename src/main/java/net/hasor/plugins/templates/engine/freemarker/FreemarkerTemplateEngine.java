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
package net.hasor.plugins.templates.engine.freemarker;
import java.io.Writer;
import freemarker.template.Configuration;
import net.hasor.core.AppContext;
import net.hasor.plugins.templates.ContextMap;
import net.hasor.plugins.templates.TemplateEngine;
import net.hasor.plugins.templates.TemplateLoader;
/**
 * 
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class FreemarkerTemplateEngine implements TemplateEngine {
    @Override
    public void initEngine(AppContext appContext) {
        String realPath = appContext.getEnvironment().envVar("HASOR_WEBROOT");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setDefaultEncoding("utf-8");
        configuration.setOutputEncoding("utf-8");
        configuration.setLocalizedLookup(true);
        configuration.setTemplateLoader(new TemplateLoaderWrap(templateLoader));
    }
    @Override
    public void process(String layoutFile, Writer writer, ContextMap dataModel, String characterEncoding) {
        configuration.getTemplate(name)
        // TODO Auto-generated method stub
    }
    @Override
    public TemplateLoader getRootLoader() {
        // TODO Auto-generated method stub
        return null;
    }
}