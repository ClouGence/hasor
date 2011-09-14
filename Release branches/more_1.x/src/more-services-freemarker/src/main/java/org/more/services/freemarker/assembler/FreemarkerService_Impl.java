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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.more.hypha.commons.AbstractService;
import org.more.services.freemarker.FreemarkerService;
import org.more.services.freemarker.TemplateBlock;
import org.more.services.freemarker.TemplateProcess;
import org.more.services.freemarker.loader.MoreTemplateLoader;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.TransformToAttribute;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
/**
 * {@link FreemarkerService}接口实现类。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class FreemarkerService_Impl extends AbstractService implements FreemarkerService {
    private Configuration        cfg          = null;
    private IAttribute<Object>   superRootMap = null;
    //
    private List<TemplateLoader> loaderList   = null;
    //
    private MoreTemplateLoader   moreLoader   = new MoreTemplateLoader(this);
    private MultiTemplateLoader  multiLoader  = null;
    /*-------------------------------------------------------------*/
    public void start() {
        this.cfg = new Configuration();
        this.loaderList = new ArrayList<TemplateLoader>();
        this.loaderList.add(this.moreLoader);
    }
    public void stop() {
        this.cfg = null;
        this.loaderList = null;
        this.moreLoader = new MoreTemplateLoader(this);
        this.multiLoader = null;
    }
    /*-------------------------------------------------------------*/
    public void setSuperRoot(IAttribute<Object> rootMap) {
        this.superRootMap = rootMap;
    }
    public void setSuperRoot(Map<?, ?> rootMap) {
        this.superRootMap = new TransformToAttribute<Object>((Map<Object, Object>) rootMap);
    }
    public void setTemplateDir(File templateDir) throws IOException {
        this.addLoader(new FileTemplateLoader(templateDir));
    }
    public void addTemplate(String name, String path) {
        this.moreLoader.addTemplate(name, path);
    }
    public void addTemplate(String name, File path) {
        this.moreLoader.addTemplate(name, path);
    }
    public void addTemplateAsString(String name, String templateString) {
        this.moreLoader.addTemplateAsString(name, templateString);
    }
    public synchronized void addLoader(TemplateLoader loader) {
        if (this.loaderList.contains(loader) == true)
            return;
        this.loaderList.add(loader);
        this.multiLoader = null;
    }
    /*-------------------------------------------------------------*/
    /**获取一个{@link Configuration}对象，该对象是克隆自cfg。*/
    private synchronized Configuration getConfiguration() {
        // FileTemplateLoader f_Loader=new FileTemplateLoader();
        // ClassTemplateLoader c_Loader=new ClassTemplateLoader(loaderClass, path);
        // WebappTemplateLoader w_Loader=new WebappTemplateLoader(servletContext);
        if (this.multiLoader != null)
            return (Configuration) this.cfg.clone();
        //
        TemplateLoader[] loaders = new TemplateLoader[this.loaderList.size()];
        this.loaderList.toArray(loaders);
        this.multiLoader = new MultiTemplateLoader(loaders);
        this.cfg.setTemplateLoader(this.multiLoader);
        //
        return (Configuration) this.cfg.clone();
    }
    public TemplateProcess getProcess() {
        return new TemplateProcess_Impl(getConfiguration());
    }
    public TemplateProcess getProcess(File templateDir) throws IOException {
        Configuration cfg = getConfiguration();
        //
        TemplateLoader[] loaders = new TemplateLoader[2];
        loaders[0] = cfg.getTemplateLoader();
        loaders[1] = new FileTemplateLoader(templateDir);
        //
        cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
        return new TemplateProcess_Impl(cfg);
    }
    /*-------------------------------------------------------------*/
    public boolean containsTemplate(String templateName) {
        return this.getProcess().containsTemplate(templateName);
    }
    public boolean containsTemplate(TemplateBlock templateBlock) {
        return this.getProcess().containsTemplate(templateBlock);
    }
    public void process(String templateName, String encoding, Map<String, ?> rootMap, Writer out) {
        this.getProcess().process(templateName, encoding, rootMap, out);
    }
    public void process(TemplateBlock templateBlock, String encoding, Map<String, ?> rootMap, Writer out) {
        this.getProcess().process(templateBlock, encoding, rootMap, out);
    }
    public void process(String templateName, Map<String, ?> rootMap, Writer out) {
        this.getProcess().process(templateName, rootMap, out);
    }
    public void process(TemplateBlock templateBlock, Map<String, ?> rootMap, Writer out) {
        this.getProcess().process(templateBlock, rootMap, out);
    }
    public void process(String templateName, Writer out) {
        this.getProcess().process(templateName, out);
    }
    public void process(TemplateBlock templateBlock, Writer out) {
        this.getProcess().process(templateBlock, out);
    }
    public String getTemplateBody(String templateName) {
        return this.getProcess().getTemplateBody(templateName);
    }
    public String getTemplateBody(TemplateBlock templateBlock) {
        return this.getProcess().getTemplateBody(templateBlock);
    }
    public InputStream getTemplateBodyAsStream(String templateName) {
        return this.getProcess().getTemplateBodyAsStream(templateName);
    }
    public InputStream getTemplateBodyAsStream(TemplateBlock templateBlock) {
        return this.getProcess().getTemplateBodyAsStream(templateBlock);
    }
    /*-------------------------------------------------------------*/
    public boolean contains(String name) {
        return this.superRootMap.contains(name);
    }
    public void setAttribute(String name, Object value) {
        this.superRootMap.setAttribute(name, value);
    }
    public Object getAttribute(String name) {
        return this.superRootMap.getAttribute(name);
    }
    public void removeAttribute(String name) {
        this.superRootMap.removeAttribute(name);
    }
    public String[] getAttributeNames() {
        return this.superRootMap.getAttributeNames();
    }
    public void clearAttribute() {
        this.superRootMap.clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.superRootMap.toMap();
    }
}