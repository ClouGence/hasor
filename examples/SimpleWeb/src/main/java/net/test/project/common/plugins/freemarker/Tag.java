package org.noe.platform.modules.freemarker;
import java.io.IOException;
import java.util.Map;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
/***
 * 自定义标签
 * @version : 2013-5-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Tag {
    /**准备开始执行标签*/
    public boolean beforeTag(Map<String, Object> propxy, Environment environment) throws TemplateException;
    /**执行标签*/
    public void doTag(Map<String, Object> propxy, TemplateBody body) throws TemplateException, IOException;
    /**标签执行完毕*/
    public void afterTag(Map<String, Object> propxy, Environment environment) throws TemplateException;
}