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
package org.platform.view.freemarker.support;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import org.more.util.CommonCodeUtils;
import org.platform.context.AppContext;
import org.platform.context.SettingListener;
import org.platform.context.Settings;
import org.platform.view.freemarker.ConfigurationFactory;
import org.platform.view.freemarker.FreemarkerManager;
import org.platform.view.freemarker.loader.ConfigTemplateLoader;
import org.platform.view.freemarker.loader.MultiTemplateLoader;
import com.google.inject.Singleton;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-5-17
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
class InternalFreemarkerManager implements FreemarkerManager, SettingListener {
    private AppContext             appContext           = null;
    private ConfigTemplateLoader   stringLoader         = null;
    private ConfigurationFactory   configurationFactory = null;
    private volatile Configuration configuration        = null;
    //
    //
    @Override
    public void initManager(AppContext appContext) {
        if (this.configurationFactory == null)
            this.configurationFactory = appContext.getInstance(ConfigurationFactory.class);
        this.appContext = appContext;
        this.getFreemarker();
        appContext.getSettings().addSettingsListener(this);
    }
    @Override
    public void destroyManager(AppContext appContext) {
        appContext.getSettings().removeSettingsListener(this);
    }
    //
    @Override
    public void loadConfig(Settings newConfig) {
        this.configuration = null;
    }
    public final Configuration getFreemarker() {
        if (this.configuration == null) {
            this.configuration = this.configurationFactory.configuration(this.appContext);
            //
            ArrayList<TemplateLoader> loaders = new ArrayList<TemplateLoader>();
            if (this.configuration.getTemplateLoader() != null)
                loaders.add(this.configuration.getTemplateLoader());
            loaders.add(this.configurationFactory.createConfigTemplateLoader(this.appContext));
            loaders.add(this.configurationFactory.createTemplateLoader(this.appContext));
            //
            this.configuration.setTemplateLoader(new MultiTemplateLoader(loaders.toArray(new TemplateLoader[loaders.size()])));
        }
        return configuration;
    }
    @Override
    public Template getTemplate(String templateName) throws TemplateException, IOException {
        return this.getFreemarker().getTemplate(templateName);
    }
    //
    //
    @Override
    public void processTemplate(String templateName) throws TemplateException, IOException {
        this.processTemplate(templateName, null, null);
    }
    @Override
    public void processTemplate(String templateName, Object rootMap) throws TemplateException, IOException {
        this.processTemplate(templateName, rootMap, null);
    }
    @Override
    public void processTemplate(String templateName, Object rootMap, Writer writer) throws TemplateException, IOException {
        Writer writerTo = (writer == null) ? new InternalNoneWriter() : writer;
        this.getTemplate(templateName).process(rootMap, writerTo);
    }
    //
    //
    @Override
    public String processString(String templateString) throws TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();
        this.processString(templateString, null, stringWriter);
        return stringWriter.toString();
    }
    @Override
    public String processString(String templateString, Object rootMap) throws TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();
        this.processString(templateString, rootMap, stringWriter);
        return stringWriter.toString();
    }
    //
    //
    @Override
    public void processString(String templateString, Writer writer) throws TemplateException, IOException {
        this.processString(templateString, null, writer);
    }
    @Override
    public void processString(String templateString, Object rootMap, Writer writer) throws TemplateException, IOException {
        //A.取得指纹
        String hashStr = null;
        try {
            /*使用MD5加密*/
            hashStr = CommonCodeUtils.MD5.getMD5(templateString);
        } catch (NoSuchAlgorithmException e) {
            /*使用hashCode*/
            hashStr = String.valueOf(templateString.hashCode());
        }
        hashStr += ".temp";
        //B.将内容加入到模板加载器中。
        this.stringLoader.addTemplateAsString(hashStr, templateString);
        //C.执行指纹模板
        Writer writerTo = (writer == null) ? new InternalNoneWriter() : writer;
        Template temp = this.getTemplate(hashStr);
        if (temp != null)
            temp.process(rootMap, writerTo);
    }
}