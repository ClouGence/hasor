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
package org.more.services.submit.result;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import org.more.services.freemarker.TemplateBlock;
import org.more.services.submit.impl.DefaultResultImpl;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.ParentDecorator;
import freemarker.template.Template;
/**
 * ftl
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class FTLResult extends DefaultResultImpl<Object> implements IAttribute<Object>, TemplateBlock {
    private Writer             writer       = null;
    private String             inEncoding   = null;
    private Locale             locale       = null;
    private String             templatePath = null;
    /*-------------------------------------------------------------*/
    private IAttribute<Object> parentAtt    = null;
    private AttBase<Object>    thisAtt      = new AttBase<Object>();
    //
    public FTLResult(String templatePath, String encoding, Writer writer, Object returnValue) {
        super("ftl", returnValue);
        this.templatePath = templatePath;
        this.inEncoding = encoding;
        this.writer = writer;
    }
    public FTLResult(String templatePath, String encoding, Writer writer) {
        super("ftl", templatePath);
        this.templatePath = templatePath;
        this.inEncoding = encoding;
        this.writer = writer;
    }
    public FTLResult(String templatePath) {
        super("ftl", templatePath);
    }
    /*-------------------------------------------------------------*/
    /**获取要执行的模板。*/
    public String getTemplateName() {
        return this.templatePath;
    }
    /**配置{@link Template}以便于提供更高级的功能。*/
    public Template applyTemplate(Template template) {
        return template;
    }
    /**获取区域*/
    public Locale getLocale() {
        return this.locale;
    }
    /**设置区域*/
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    /**获取模板执行结果输出地址。*/
    public Writer getWriter() {
        return this.writer;
    };
    /**设置输出流。*/
    public void setWriter(Writer writer) {
        this.writer = writer;
    };
    /**获取输入文件的字符编码。*/
    public String getInEncoding() {
        return this.inEncoding;
    };
    /**设置输入文件的字符编码。*/
    public void setInEncoding(String inEncoding) {
        this.inEncoding = inEncoding;
    };
    //
    /**设置该{@link IAttribute}接口的属性集父级属性集，该类的{@link IAttribute}接口set,remote,clean操作只会针对当前类的属性集，不会影响到父属性集。*/
    public void setParentAtt(IAttribute<Object> parent) {
        this.parentAtt = new ParentDecorator<Object>(this.thisAtt, parent);
    };
    private IAttribute<Object> getParent() {
        if (this.parentAtt == null)
            return this.thisAtt;
        return this.parentAtt;
    };
    /*-------------------------------------------------------------*/
    public boolean contains(String name) {
        return this.getParent().contains(name);
    };
    public Object getAttribute(String name) {
        return this.getParent().getAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.getParent().getAttributeNames();
    };
    public Map<String, Object> toMap() {
        return this.getParent().toMap();
    };
    public void setAttribute(String name, Object value) {
        this.thisAtt.setAttribute(name, value);
    };
    public void removeAttribute(String name) {
        this.thisAtt.removeAttribute(name);
    };
    public void clearAttribute() {
        this.thisAtt.clearAttribute();
    }
}