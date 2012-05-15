package org.more.webui.freemarker.parser;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.more.webui.components.UIComponent;
import org.more.webui.components.UIViewRoot;
import freemarker.core.TemplateElement;
import freemarker.template.Template;
/**
 * 负责递归扫描模板元素以创建{@link UIViewRoot}对象。
 * @version : 2012-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class TemplateScanner {
    private Map<String, ElementHook> blockRegister = new HashMap<String, ElementHook>();
    //
    public void addElementHook(String itemType, ElementHook hook) {
        this.blockRegister.put(itemType, hook);
    }
    /**解析模板用于生成{@link UIViewRoot}*/
    public UIViewRoot parser(Template template, UIViewRoot uiViewRoot) throws Throwable {
        TemplateElement rootNode = template.getRootTreeNode();
        return (UIViewRoot) parserElement(rootNode, uiViewRoot);
    }
    /**element要解析的元素，componentParent当前所处组件*/
    private UIComponent parserElement(TemplateElement element, UIComponent componentParent) throws Throwable {
        Enumeration<TemplateElement> enumItems = element.children();
        while (enumItems.hasMoreElements() == true) {
            //递归扫描所有模板节点。
            TemplateElement e = enumItems.nextElement();
            Class<?> blockType = e.getClass();
            ElementHook hook = this.blockRegister.get(blockType.getSimpleName());
            //componentItem这个变量会保证每次调用ElementHook传入的UIComponent都是TemplateElement标签所处的父级UIComponent。
            //同时它也保证在递归调用parserElement方法的过程中element参数永远是componentParent所处组件下的标签。
            UIComponent componentItem = null;
            if (hook != null)
                componentItem = hook.beginAtBlcok(e);//在解析元素时如果返回了一个UIComponent则将这个UIComponent加入到componentParent
            if (componentItem != null)
                componentParent.getChildren().add(componentItem);
            this.parserElement(e, (componentItem != null) ? componentItem : componentParent);//递归解析
            if (hook != null)
                hook.endAtBlcok(e);
        }
        return componentParent;
    }
}