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
package net.test.project.common.plugins.freemarker.support;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.hasor.core.AppContext;
import net.test.project.common.plugins.freemarker.FreemarkerService;
import net.test.project.common.plugins.freemarker.loader.loader.StringTemplateLoader;
import org.more.util.CommonCodeUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-7-31
 * @author 赵永春 (zyc@byshell.org)
 */
public class FmService implements FreemarkerService {
    @Inject
    private AppContext            appContext;
    @Inject
    private Configuration         configuration;
    private StringTemplateLoader  stringLoader = new StringTemplateLoader();
    private Map<String, Template> templateMap  = new HashMap<String, Template>();
    //
    //
    public Configuration getFreemarker() {
        return this.configuration;
    }
    public Template getTemplate(String templateName) throws TemplateException, IOException {
        boolean bool = appContext.getSettings().getBoolean("framework.debug");
        if (bool == true)
            return this.getFreemarker().getTemplate(templateName);
        //
        Template templ = templateMap.get(templateName);
        if (templ == null) {
            templ = this.getFreemarker().getTemplate(templateName);
            templateMap.put(templateName, templ);
        }
        return templ;
    }
    //
    //
    public void processTemplate(String templateName) throws TemplateException, IOException {
        this.processTemplate(templateName, null, null);
    }
    public void processTemplate(String templateName, Object rootMap) throws TemplateException, IOException {
        this.processTemplate(templateName, rootMap, null);
    }
    public void processTemplate(String templateName, Object rootMap, Writer writer) throws TemplateException, IOException {
        Writer writerTo = (writer == null) ? new InternalNoneWriter() : writer;
        this.getTemplate(templateName).process(rootMap, writerTo);
    }
    //
    //
    public String processString(String templateString) throws TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();
        this.processString(templateString, null, stringWriter);
        return stringWriter.toString();
    }
    public String processString(String templateString, Object rootMap) throws TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();
        this.processString(templateString, rootMap, stringWriter);
        return stringWriter.toString();
    }
    //
    //
    public void processString(String templateString, Writer writer) throws TemplateException, IOException {
        this.processString(templateString, null, writer);
    }
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