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
package net.hasor.restful.render.freemarker;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import net.hasor.restful.InvokerContext;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebAppContext;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
/**
 *
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class FreemarkerTemplateEngine implements RenderEngine {
    protected Configuration configuration;
    @Override
    public void initEngine(WebAppContext appContext) throws IOException {
        String realPath = appContext.getEnvironment().envVar("HASOR_WEBROOT");
        TemplateLoader templateLoader = new FileTemplateLoader(new File(realPath), true);
        configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setTemplateLoader(templateLoader);
        configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_22).build());
        //
        configuration.setDefaultEncoding("utf-8");//默认页面编码UTF-8
        configuration.setOutputEncoding("utf-8");//输出编码格式UTF-8
        configuration.setLocalizedLookup(false);//是否开启国际化false
        configuration.setClassicCompatible(true);//null值测处理配置
    }
    @Override
    public void process(InvokerContext invokerContext, Writer writer) throws Throwable {
        Template temp = configuration.getTemplate(invokerContext.getViewName());
        //
        HashMap<String, Object> data = new HashMap<String, Object>();
        for (String key : invokerContext.keySet()) {
            data.put(key, invokerContext.get(key));
        }
        data.put("rootModel", invokerContext);
        //
        temp.process(data, writer);
    }
    @Override
    public boolean exist(String template) throws IOException {
        return configuration.getTemplateLoader().findTemplateSource(template) != null;
    }
}