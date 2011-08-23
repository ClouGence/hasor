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
package org.more.submit.result;
import java.io.Writer;
import java.util.Map;
import org.more.submit.impl.DefaultResultImpl;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * ftl
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class FTLResult extends DefaultResultImpl<Object> implements IAttribute {
    private Writer  writer       = null;
    private String  templatePath = null;
    private AttBase att          = new AttBase();
    //
    public FTLResult(String templatePath, Writer writer, Object returnValue) {
        super("ftl", returnValue);
        this.writer = writer;
        this.templatePath = templatePath;
    }
    public FTLResult(String templatePath, Writer writer) {
        super("ftl", templatePath);
        this.writer = writer;
    }
    public FTLResult(String templatePath) {
        super("ftl", templatePath);
    }
    /**获取要执行的模板。*/
    public String getTemplatePath() {
        return this.templatePath;
    }
    /**除了使用{@link FreeMarkerConfig}类对{@link Configuration}进行配置之外，也可以通过重写该方法进行配置。不过该方法配置会被最后执行。*/
    public void applyConfiguration(Configuration configuration) {};
    /**配置{@link Template}以便于提供更高级的功能。*/
    public void applyTemplate(Template template) {}
    /**获取模板执行结果输出地址。*/
    public Writer getWriter() {
        return this.writer;
    };
    //
    public boolean contains(String name) {
        return this.att.contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.att.setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.att.getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.att.removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.att.getAttributeNames();
    };
    public void clearAttribute() {
        this.att.clearAttribute();
    };
    public Map<String, Object> toMap() {
        return this.att.toMap();
    }
}