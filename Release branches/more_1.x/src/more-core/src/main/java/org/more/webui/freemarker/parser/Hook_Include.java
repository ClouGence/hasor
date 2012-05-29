package org.more.webui.freemarker.parser;
import org.more.webui.UIInitException;
import org.more.webui.components.UIComponent;
import org.more.webui.context.FacesConfig;
import freemarker.core.TemplateElement;
import freemarker.template.Template;
/**
 * 负责根据标签元素创建组建对象，该类会对freemarker有强烈的版本限制要求。更换freemarker版本可能会引发问题。
 * @version : 2012-5-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class Hook_Include implements ElementHook {
    private FacesConfig facesConfig = null; //注册器
    //
    public Hook_Include(FacesConfig facesConfig) {
        if (facesConfig == null)
            throw new NullPointerException("param ‘FacesConfig’ si null.");
        this.facesConfig = facesConfig;
    }
    @Override
    public UIComponent beginAtBlcok(TemplateScanner scanner, TemplateElement e, UIComponent parent) throws UIInitException {
        try {
            String includeName = e.getDescription().split(" ")[1];
            includeName = includeName.substring(1, includeName.length() - 1);
            Template includeTemp = e.getTemplate().getConfiguration().getTemplate(includeName);
            scanner.parser(includeTemp, parent);
            return null;
        } catch (Exception e2) {
            throw new UIInitException("解析异常：处理include发生错误“" + e.getDescription() + "”", e2);
        }
    }
    @Override
    public void endAtBlcok(TemplateScanner scanner, TemplateElement e, UIComponent parent) throws UIInitException {}
}