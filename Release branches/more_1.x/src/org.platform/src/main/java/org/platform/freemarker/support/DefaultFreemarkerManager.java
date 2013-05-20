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
package org.platform.freemarker.support;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import org.more.util.CommonCodeUtil;
import org.more.webui.freemarker.loader.ConfigTemplateLoader;
import org.platform.freemarker.FreemarkerManager;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-5-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultFreemarkerManager implements FreemarkerManager {
    private ConfigTemplateLoader configTemplateLoader = new ConfigTemplateLoader();
    //    private Configuration        cfg                  = null;
    //
    //
    //
    public final Configuration getFreemarker() {
        // TODO Auto-generated method stub
        return null;
        //        if (this.cfg == null) {
        //            this.cfg = createFreemarker();
        //            cfg.setDefaultEncoding(this.getEnvironment().getPageEncoding());
        //            cfg.setOutputEncoding(this.getEnvironment().getOutEncoding());
        //            cfg.setLocalizedLookup(this.getEnvironment().isLocalizedLookup());
        //            //
        //            TemplateLoader[] loaders = null;
        //            if (cfg.getTemplateLoader() != null) {
        //                loaders = new TemplateLoader[2];
        //                loaders[1] = cfg.getTemplateLoader();
        //            } else
        //                loaders = new TemplateLoader[1];
        //            loaders[0] = this.configTemplateLoader;
        //            cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
        //        }
        //        return this.cfg;
    }
    @Override
    public Template getTemplate(String templateName) throws TemplateException, IOException {
         return this.getFreemarker().getTemplate(templateName, locale, encoding, parse);
    }s
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
        Writer writerTo = (writer == null) ? new NoneWriter() : writer;
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
            hashStr = CommonCodeUtil.MD5.getMD5(templateString);
        } catch (NoSuchAlgorithmException e) {
            /*使用hashCode*/
            hashStr = String.valueOf(templateString.hashCode());
        }
        hashStr += ".temp";
        //B.将内容加入到模板加载器中。
        this.configTemplateLoader.addTemplateAsString(hashStr, templateString);
        //C.执行指纹模板
        Writer writerTo = (writer == null) ? new NoneWriter() : writer;
        Template  temp= this.getTemplate(hashStr);
        if (temp!=null)
            temp.process(rootMap, writerTo);
    }
}