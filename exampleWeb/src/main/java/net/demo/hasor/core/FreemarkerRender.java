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
package net.demo.hasor.core;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import net.hasor.restful.Render;
import net.hasor.restful.RenderData;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebAppContext;
import org.more.util.StringEscapeUtils;
import org.more.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Set;
/**
 *
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Render({"html", "htm"})
public class FreemarkerRender implements RenderEngine {
    protected Configuration configuration;
    @Override
    public void initEngine(WebAppContext appContext) throws Throwable {
        String realPath = appContext.getEnvironment().getServletContext().getRealPath("/");
        TemplateLoader templateLoader = new FileTemplateLoader(new File(realPath), true);
        this.configuration = new Configuration(Configuration.VERSION_2_3_22);
        this.configuration.setTemplateLoader(templateLoader);
        this.configuration.setDefaultEncoding("utf-8");//默认页面编码UTF-8
        this.configuration.setOutputEncoding("utf-8");//输出编码格式UTF-8
        this.configuration.setLocalizedLookup(false);//是否开启国际化false
        this.configuration.setClassicCompatible(true);//null值测处理配置
        //
        // - 各种工具
        this.configuration.setSharedVariable("escapeHtml", new StringEscapeUtils());//HTML 转译,防止XSS使用。
        this.configuration.setSharedVariable("stringUtils", new StringUtils());
        //
        // - 系统服务
        Set<Class<?>> serviceSet = appContext.getEnvironment().findClass(Service.class);
        for (Class<?> service : serviceSet) {
            if (service == Service.class) {
                continue;
            }
            Service ser = service.getAnnotation(Service.class);
            if (ser != null && StringUtils.isNotBlank(ser.value())) {
                this.configuration.setSharedVariable(ser.value(), appContext.getInstance(service));
            }
        }
        //
        // - 环境变量
        this.configuration.setSharedVariable("ctx_path", appContext.getServletContext().getContextPath());
    }
    @Override
    public void process(RenderData renderData, Writer writer) throws Throwable {
        Template temp = this.configuration.getTemplate(renderData.renderTo());
        //
        HashMap<String, Object> data = new HashMap<String, Object>();
        for (String key : renderData.keySet()) {
            data.put(key, renderData.get(key));
        }
        //
        temp.process(data, writer);
    }
    @Override
    public boolean exist(String template) throws IOException {
        return this.configuration.getTemplateLoader().findTemplateSource(template) != null;
    }
}