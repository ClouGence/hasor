package org.noe.platform.modules.freemarker;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2012-6-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface TemplateBody {
    /**标签属性*/
    public Map<String, Object> tagProperty();
    /**获取标签执行环境*/
    public Environment getEnvironment();
    /**渲染输出标签内容*/
    public void doBody(Writer arg0) throws TemplateException, IOException;
    /**渲染输出标签内容*/
    public void doBody() throws TemplateException, IOException;
}