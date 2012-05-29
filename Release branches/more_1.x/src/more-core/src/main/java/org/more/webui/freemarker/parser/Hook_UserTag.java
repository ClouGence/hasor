package org.more.webui.freemarker.parser;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import org.more.webui.UIInitException;
import org.more.webui.components.UIComponent;
import org.more.webui.context.FacesConfig;
import freemarker.core.Expression;
import freemarker.core.TemplateElement;
/**
 * 负责根据标签元素创建组建对象，该类会对freemarker有强烈的版本限制要求。更换freemarker版本可能会引发问题。
 * @version : 2012-5-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class Hook_UserTag implements ElementHook {
    private FacesConfig facesConfig = null; //注册器
    //
    public Hook_UserTag(FacesConfig facesConfig) {
        if (facesConfig == null)
            throw new NullPointerException("param ‘FacesConfig’ si null.");
        this.facesConfig = facesConfig;
    }
    @Override
    public UIComponent beginAtBlcok(TemplateScanner scanner, TemplateElement e, UIComponent parent) throws UIInitException {
        //A.创建组建
        String tagName = e.getDescription().split(" ")[1];
        UIComponent componentObject = this.facesConfig.createComponent(tagName);
        if (componentObject == null)
            return null;
        //B.装载属性定义
        Map<String, Expression> namedArgs = null;
        try {
            Field field = e.getClass().getDeclaredField("namedArgs");
            field.setAccessible(true);
            namedArgs = (Map<String, Expression>) field.get(e);
            for (String key : namedArgs.keySet()) {
                Expression exp = namedArgs.get(key);
                if (exp == null)
                    continue;
                if (exp.getClass().getSimpleName().equals("StringLiteral") == true) {
                    Field valueField = exp.getClass().getDeclaredField("value");
                    valueField.setAccessible(true);
                    componentObject.setProperty(key, (String) valueField.get(exp));
                } else
                    componentObject.setPropertyEL(key, exp.getSource());
            }
        } catch (Exception e2) {
            throw new UIInitException("Freemarker兼容错误：无法读取namedArgs或value字段。建议使用建议使用freemarker 2.3.19版本。", e2);
        }
        //C.将组建和标签对象的ID值相互绑定。
        try {
            String comID = null;
            if (namedArgs.containsKey("id") == false) {
                Class<?> strV = Thread.currentThread().getContextClassLoader().loadClass("freemarker.core.StringLiteral");
                Constructor<?> cons = strV.getDeclaredConstructor(String.class);
                cons.setAccessible(true);
                comID = FacesConfig.generateID(componentObject.getClass());//生成新的ID
                Expression idExp = (Expression) cons.newInstance(comID);
                namedArgs.put("id", idExp);
            } else {
                Expression idExp = namedArgs.get("id");
                Field valueField = idExp.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                comID = (String) valueField.get(idExp);
            }
            componentObject.setId(comID);
        } catch (Exception e2) {
            throw new UIInitException("Freemarker兼容错误：无法创建StringLiteral类型对象。建议使用建议使用freemarker 2.3.19版本。", e2);
        }
        return componentObject;
    }
    @Override
    public void endAtBlcok(TemplateScanner scanner, TemplateElement e, UIComponent parent) throws UIInitException {}
}