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
package org.more.services.freemarker.assembler;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import org.more.services.freemarker.FreemarkerService;
import org.more.services.freemarker.ResourceLoader;
import org.more.services.freemarker.TemplateBlock;
import org.more.services.freemarker.TemplateProcess;
import org.more.util.attribute.SequenceStack;
import org.more.util.attribute.TransformToAttribute;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 负责执行模板的接口。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class TemplateProcess_Impl implements TemplateProcess {
    private Configuration     cfg     = null;
    private FreemarkerService service = null;
    //
    public TemplateProcess_Impl(FreemarkerService service, Configuration cfg) {
        this.cfg = cfg;
        this.service = service;
    }
    public boolean containsTemplate(String templateName) throws IOException {
        Object obj = cfg.getTemplateLoader().findTemplateSource(templateName);
        return (obj == null) ? false : true;
    }
    public boolean containsTemplate(String templateName, Locale locale) throws IOException {
        Object obj = cfg.getTemplateLoader().findTemplateSource(templateName + locale.getDisplayName());
        return (obj == null) ? false : true;
    }
    public boolean containsTemplate(TemplateBlock templateBlock) throws IOException {
        Object obj = cfg.getTemplateLoader().findTemplateSource(templateBlock.getTemplateName());
        return (obj == null) ? false : true;
    }
    public void process(TemplateBlock templateBlock, Writer out) throws IOException, TemplateException {
        Template temp = this.getTemplate(templateBlock);
        SequenceStack<Object> stack = new SequenceStack<Object>();
        stack.putStack(templateBlock);
        stack.putStack(this.service);
        temp.process(stack.toMap(), new NoneWriter(out));
    }
    public void process(String templateName, String encoding, Map<String, ?> rootMap, Writer out) throws TemplateException, IOException {
        Template temp = this.getTemplate(templateName, encoding, this.service.getDefaultLocale());
        SequenceStack<Object> stack = new SequenceStack<Object>();
        if (rootMap != null)
            stack.putStack(new TransformToAttribute<Object>(rootMap));
        stack.putStack(this.service);
        temp.process(stack.toMap(), new NoneWriter(out));
    }
    public void process(String templateName, Map<String, ?> rootMap, Writer out) throws TemplateException, IOException {
        this.process(templateName, this.service.getInEncoding(), rootMap, out);
    }
    public void process(String templateName, Writer out) throws TemplateException, IOException {
        this.process(templateName, this.service.getInEncoding(), null, out);
    }
    public String getTemplateBody(String templateName) throws IOException {
        return this.getTemplateBody(templateName, this.service.getInEncoding(), this.service.getDefaultLocale());
    }
    public String getTemplateBody(String templateName, String encoding) throws IOException {
        return this.getTemplateBody(templateName, encoding, this.service.getDefaultLocale());
    }
    public String getTemplateBody(String templateName, String encoding, Locale locale) throws IOException {
        StringWriter sw = new StringWriter();
        Reader sr = this.getTemplateBodyAsReader(templateName, encoding, locale);
        char[] charArray = new char[1024];
        int length = 0;
        while ((length = sr.read(charArray)) > 0)
            sw.write(charArray, 0, length);
        sw.flush();
        return sw.toString();
    }
    public String getTemplateBody(TemplateBlock templateBlock) throws IOException {
        StringWriter sw = new StringWriter();
        Reader sr = this.getTemplateBodyAsReader(templateBlock);
        char[] charArray = new char[1024];
        int length = 0;
        while ((length = sr.read(charArray)) > 0)
            sw.write(charArray, 0, length);
        sw.flush();
        return sw.toString();
    }
    public Reader getTemplateBodyAsReader(TemplateBlock templateBlock) throws IOException {
        return this.getTemplateBodyAsReader(templateBlock.getTemplateName(), templateBlock.getInEncoding(), templateBlock.getLocale());
    }
    public Template getTemplate(TemplateBlock templateBlock) throws IOException {
        return this.cfg.getTemplate(templateBlock.getTemplateName(), templateBlock.getLocale(), templateBlock.getInEncoding());
    }
    public Reader getTemplateBodyAsReader(String templateName, Locale locale) throws IOException {
        return this.getTemplateBodyAsReader(templateName, null, locale);
    }
    public Reader getTemplateBodyAsReader(String templateName, String encoding, Locale locale) throws IOException {
        //1.参数检查
        String $encoding = encoding;
        Locale $locale = locale;
        if ($encoding == null)
            $encoding = this.service.getInEncoding();
        if ($locale == null)
            $locale = this.service.getDefaultLocale();
        //2.测试模板是否存在
        if (this.containsTemplate(templateName, $locale) == false)
            return null;
        //3.
        String fullName = templateName + locale.getDisplayName();
        Object tempObject = this.cfg.getTemplateLoader().findTemplateSource(fullName);
        return this.cfg.getTemplateLoader().getReader(tempObject, encoding);
    }
    public Template getTemplate(String templateName, String encoding, Locale locale) throws IOException {
        String $encoding = encoding;
        Locale $locale = locale;
        if (encoding == null)
            encoding = this.service.getInEncoding();
        if (locale == null)
            locale = this.service.getDefaultLocale();
        //
        if (locale != null)
            return this.cfg.getTemplate(templateName, $locale, $encoding);
        else
            return this.cfg.getTemplate(templateName, $encoding);
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        TemplateLoader loader = this.cfg.getTemplateLoader();
        if (loader instanceof ResourceLoader)
            return ((ResourceLoader) loader).getResourceAsStream(resourcePath);
        return null;
    }
}