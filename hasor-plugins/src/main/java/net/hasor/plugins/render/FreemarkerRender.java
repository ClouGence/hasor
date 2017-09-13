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
package net.hasor.plugins.render;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.utils.StringUtils;
import net.hasor.web.RenderEngine;
import net.hasor.web.RenderInvoker;
import net.hasor.web.startup.RuntimeFilter;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
/**
 * Freemarker 渲染器，您可以通过 apiBinder.bind(Configuration.class).... 来设置您自定义的。
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class FreemarkerRender implements RenderEngine {
    protected Configuration freemarker;
    //
    /** 内置创建 Freemarker 对象的方法，您也可以通过 apiBinder.bind(Configuration.class).... 来设置您自定义的。 */
    protected Configuration newConfiguration(AppContext appContext, ServletContext servletContext) throws IOException {
        //
        String realPath = servletContext.getRealPath("/");
        TemplateLoader templateLoader = new FileTemplateLoader(new File(realPath), true);
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setTemplateLoader(templateLoader);
        //
        String responseEncoding = appContext.findBindingBean(RuntimeFilter.HTTP_RESPONSE_ENCODING_KEY, String.class);
        if (StringUtils.isBlank(responseEncoding)) {
            responseEncoding = Settings.DefaultCharset;
        }
        configuration.setDefaultEncoding(responseEncoding);
        configuration.setOutputEncoding(responseEncoding);
        configuration.setLocalizedLookup(false);//是否开启国际化false
        configuration.setClassicCompatible(true);//null值测处理配置
        //
        return configuration;
    }
    /** 各种工具&变量 */
    protected void configSharedVariable(AppContext appContext, ServletContext servletContext, Configuration freemarker) throws TemplateModelException {
        freemarker.setSharedVariable("stringUtils", new StringUtils());
        freemarker.setSharedVariable("ctx_path", servletContext.getContextPath());
    }
    //
    //
    @Override
    public void initEngine(AppContext appContext) throws Throwable {
        ServletContext servletContext = Hasor.assertIsNotNull(appContext.getInstance(ServletContext.class));
        BindInfo<Configuration> bindInfo = appContext.getBindInfo(Configuration.class);
        if (bindInfo == null) {
            this.freemarker = this.newConfiguration(appContext, servletContext);
        } else {
            this.freemarker = appContext.getInstance(bindInfo);
        }
        //
        this.configSharedVariable(appContext, servletContext, Hasor.assertIsNotNull(this.freemarker));
    }
    @Override
    public boolean exist(String template) throws IOException {
        return this.freemarker.getTemplateLoader().findTemplateSource(template) != null;
    }
    @Override
    public void process(RenderInvoker renderData, Writer writer) throws Throwable {
        Template temp = this.freemarker.getTemplate(renderData.renderTo());
        if (temp == null)
            return;
        //
        HashMap<String, Object> data = new HashMap<String, Object>();
        for (String key : renderData.keySet()) {
            data.put(key, renderData.get(key));
        }
        temp.process(data, writer);
    }
}