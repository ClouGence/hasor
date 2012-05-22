package org.more.webui.render;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.components.UIComponent;
import org.more.webui.context.ViewContext;
/**
 * 组建渲染器
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Render {
    /**开始渲染组建*/
    public void beginRender(ViewContext viewContext, UIComponent component, Map params, Writer writer) throws IOException;
    /**组建渲染结束*/
    public void endRender(ViewContext viewContext, UIComponent component, Map params, Writer writer) throws IOException;
}