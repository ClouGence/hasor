package org.more.webui.tag;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.components.UIComponent;
import org.more.webui.context.ViewContext;
import org.more.webui.render.Render;
import org.more.webui.render.RenderKit;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
/**
 * 通用标签对象。
 * @version : 2012-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagObject implements TemplateDirectiveModel {
    public void execute(Environment arg0, Map arg1, TemplateModel[] arg2, TemplateDirectiveBody arg3) throws TemplateException, IOException {
        //A.获取组建
        ViewContext viewContext = ViewContext.getCurrentViewContext();
        UIComponent component = null;
        try {
            String componentID = (String) arg1.get("id").toString();
            component = viewContext.getViewRoot().getChildByID(componentID);
        } catch (Exception e) {
            throw new TemplateException(e, arg0);
        }
        //B.判断时候需要执行渲染
        if (component.isRender() == false)
            return;
        //C.获取渲染类
        String renderKit = viewContext.getRenderKitName();
        RenderKit kit = viewContext.getUIContext().getFacesConfig().getRenderKit(renderKit);
        Render renderer = kit.getRender(component.getTagName());
        //D.进行渲染
        Writer writer = arg0.getOut();
        renderer.beginRender(viewContext, component, arg1, writer);
        if (component.isRenderChildren() == true && arg3 != null)
            arg3.render(writer);
        renderer.endRender(viewContext, component, arg1, writer);
    }
}