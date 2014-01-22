package org.noe.platform.modules.freemarker;
import java.io.IOException;
import java.io.Writer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * Freemarker模板功能提供类。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public interface FreemarkerService {
    /**获取用于执行模板的Freemarker*/
    public Configuration getFreemarker();
    /**获取模板。*/
    public Template getTemplate(String templateName) throws TemplateException, IOException;
    //
    /**获取并执行模板。*/
    public void processTemplate(String templateName) throws TemplateException, IOException;
    /**获取并执行模板。*/
    public void processTemplate(String templateName, Object rootMap) throws TemplateException, IOException;
    /**获取并执行模板。*/
    public void processTemplate(String templateName, Object rootMap, Writer writer) throws TemplateException, IOException;
    //
    /**将字符串的内容作为模板执行。*/
    public String processString(String templateString) throws TemplateException, IOException;
    /**将字符串的内容作为模板执行。*/
    public String processString(String templateString, Object rootMap) throws TemplateException, IOException;
    //
    /**将字符串的内容作为模板执行。*/
    public void processString(String templateString, Writer writer) throws TemplateException, IOException;
    /**将字符串的内容作为模板执行。*/
    public void processString(String templateString, Object rootMap, Writer writer) throws TemplateException, IOException;
}