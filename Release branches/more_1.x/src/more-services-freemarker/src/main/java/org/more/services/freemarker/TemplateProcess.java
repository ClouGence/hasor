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
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * 负责执行模板的接口。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface TemplateProcess {
    /**根据模板名称判断是否存在该模板文件。*/
    public boolean containsTemplate(String templateName) throws IOException;
    /**根据模板名称判断是否存在该模板文件。*/
    public boolean containsTemplate(String templateName, Locale locale) throws IOException;
    /**根据模板名称判断是否存在该模板文件。*/
    public boolean containsTemplate(TemplateBlock templateBlock) throws IOException;
    /**获取模板*/
    public Template getTemplate(TemplateBlock templateBlock) throws IOException;
    /**获取模板*/
    public Template getTemplate(String templateName, String encoding, Locale locale) throws IOException;
    /**
     * 执行模板文件。
     * @param templateBlock 模板块
     * @param out 模板执行输出位置
     */
    public void process(TemplateBlock templateBlock, Writer out) throws IOException, TemplateException;
    /**
     * 执行模板文件。
     * @param templateName 模板名
     * @param encoding 模板文件字符编码
     * @param rootMap root对象，该Map中的对象可以在模板中访问到。
     * @param out 模板执行输出位置
     */
    public void process(String templateName, String encoding, Map<String, ?> rootMap, Writer out) throws IOException, TemplateException;
    /**
     * 执行模板文件。
     * @param templateName 模板名
     * @param rootMap root对象，该Map中的对象可以在模板中访问到。
     * @param out 模板执行输出位置
     */
    public void process(String templateName, Map<String, ?> rootMap, Writer out) throws IOException, TemplateException;
    /**
     * 执行模板文件。
     * @param templateName 模板名
     * @param out 模板执行输出位置
     */
    public void process(String templateName, Writer out) throws IOException, TemplateException;
    /**
     * 根据模板名获取模板内容。
     * @param templateName 模板名
     * @return 返回模板数据
     */
    public String getTemplateBody(String templateName) throws IOException;
    /**
     * 根据模板名获取模板内容。
     * @param templateName 模板名
     * @return 返回模板数据
     */
    public String getTemplateBody(String templateName, String encoding) throws IOException;
    /**
     * 根据模板名获取模板内容。
     * @param templateName 模板名
     * @return 返回模板数据
     */
    public String getTemplateBody(String templateName, String encoding, Locale locale) throws IOException;
    /**
     * 根据模板获取模板内容。
     * @param templateBlock
     * @return 返回模板数据
     */
    public String getTemplateBody(TemplateBlock templateBlock) throws IOException;
    /**
     * 根据模板名获取模板内容。
     * @param templateName 模板名
     * @return 返回模板数据的输入流
     */
    public Reader getTemplateBodyAsReader(String templateName, Locale locale) throws IOException;
    /**
     * 根据模板名获取模板内容。
     * @param templateName 模板名
     * @return 返回模板数据的输入流
     */
    public Reader getTemplateBodyAsReader(String templateName, String encoding, Locale locale) throws IOException;
    /**
     * 根据模板获取模板内容。
     * @param templateBlock
     * @return 返回模板数据的输入流
     */
    public Reader getTemplateBodyAsReader(TemplateBlock templateBlock) throws IOException;
}