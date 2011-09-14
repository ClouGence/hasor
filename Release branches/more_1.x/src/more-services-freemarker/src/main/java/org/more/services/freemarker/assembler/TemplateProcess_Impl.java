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
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;
import org.more.services.freemarker.TemplateBlock;
import org.more.services.freemarker.TemplateProcess;
import freemarker.template.Configuration;
/**
 * 负责执行模板的接口。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class TemplateProcess_Impl implements TemplateProcess {
    public TemplateProcess_Impl(Configuration cfg) {
        // TODO Auto-generated constructor stub
    }
    public boolean containsTemplate(String templateName) {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean containsTemplate(TemplateBlock templateBlock) {
        // TODO Auto-generated method stub
        return false;
    }
    public void process(String templateName, String encoding, Map<String, ?> rootMap, Writer out) {
        // TODO Auto-generated method stub
    }
    public void process(TemplateBlock templateBlock, String encoding, Map<String, ?> rootMap, Writer out) {
        // TODO Auto-generated method stub
    }
    public void process(String templateName, Map<String, ?> rootMap, Writer out) {
        // TODO Auto-generated method stub
    }
    public void process(TemplateBlock templateBlock, Map<String, ?> rootMap, Writer out) {
        // TODO Auto-generated method stub
    }
    public void process(String templateName, Writer out) {
        // TODO Auto-generated method stub
    }
    public void process(TemplateBlock templateBlock, Writer out) {
        // TODO Auto-generated method stub
    }
    public String getTemplateBody(String templateName) {
        // TODO Auto-generated method stub
        return null;
    }
    public String getTemplateBody(TemplateBlock templateBlock) {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getTemplateBodyAsStream(String templateName) {
        // TODO Auto-generated method stub
        return null;
    }
    public InputStream getTemplateBodyAsStream(TemplateBlock templateBlock) {
        // TODO Auto-generated method stub
        return null;
    }
}