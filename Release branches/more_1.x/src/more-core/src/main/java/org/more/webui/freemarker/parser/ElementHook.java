package org.more.webui.freemarker.parser;
import org.more.webui.UIInitException;
import org.more.webui.components.UIComponent;
import freemarker.core.TemplateElement;
/**
 * freemarker模板元素块钩子。
 * @version : 2012-5-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ElementHook {
    /**开始处理遇到的模板标签*/
    public UIComponent beginAtBlcok(TemplateElement e) throws UIInitException;
    /**处理遇到的模板标签结束*/
    public void endAtBlcok(TemplateElement e) throws UIInitException;
}