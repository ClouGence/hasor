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
package org.more.services.freemarker;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import org.more.hypha.Service;
import org.more.util.attribute.IAttribute;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
/**
 * Freemarker服务，该服务提供了模板解析的支持。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface FreemarkerService extends Service, IAttribute<Object>, TemplateProcess {
    /**获取全局配置对象*/
    public Configuration getPublicConfiguration();
    /**获取默认区域*/
    public Locale getDefaultLocale();
    /**设置默认区域*/
    public void setDefaultLocale(Locale defaultLocale);
    /**设置默认输入字符编码。*/
    public void setInEncoding(String inEncoding);
    /**获取默认输入字符编码。*/
    public String getInEncoding();
    /**设置默认输出字符编码。*/
    public void setOutEncoding(String outcoding);
    /**获取默认输出字符编码。*/
    public String getOutEncoding();
    /**设置最上级rootMap对象，在解析模板时rootMap中的对象可以被模板访问到。*/
    public void setSuperRoot(IAttribute<Object> rootMap);
    /**设置最上级rootMap对象，在解析模板时rootMap中的对象可以被模板访问到。*/
    public void setSuperRoot(Map<?, ?> rootMap);
    /**设置模板的根路径*/
    public void setTemplateDir(File templateDir) throws IOException;
    /**添加一个模板，被添加的模板只需要访问模板名即可。*/
    public void addTemplate(String name, String classPath);
    /**添加一个模板，被添加的模板只需要访问模板名即可。*/
    public void addTemplate(String name, File filePath);
    /**添加一个模板，被添加的模板只需要访问模板名即可。*/
    public void addTemplate(String name, URL urlPath);
    /**添加一个模板，被添加的模板只需要访问模板名即可。*/
    public void addTemplate(String name, String classPath, String encoding);
    /**添加一个模板，被添加的模板只需要访问模板名即可。*/
    public void addTemplate(String name, File filePath, String encoding);
    /**添加一个模板，被添加的模板只需要访问模板名即可。*/
    public void addTemplate(String name, URL urlPath, String encoding);
    /**添加一个模板，被添加的模板只需要访问模板名即可。*/
    public void addTemplateAsString(String name, String templateString);
    /**添加一个模板装载器*/
    public void addLoader(TemplateLoader loader);
    /**获取默认路径的模板执行对象，该方法返回的{@link TemplateProcess}只能装载到如下几个位置的模板：classpath中的模板、more配置文件中配置的模板、templateDir属性设置目录下的模板。*/
    public TemplateProcess getProcess();
    /**获取指定路径的模板执行对象，该方法返回的{@link TemplateProcess}对象除了{@link #getProcess()}所支持的之外还支持下面这个路径中的模板。*/
    public TemplateProcess getProcess(File templateDir) throws IOException;
    /**注册一个函数*/
    public void regeditObject(String name, FunctionObject function);
    /**注册一个标签*/
    public void regeditObject(String name, TagObject tag);
}