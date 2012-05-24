package org.more.webui.render.htmls;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.more.webui.components.AbstractValueHolder;
import org.more.webui.components.UIComponent;
import org.more.webui.context.ViewContext;
import org.more.webui.render.Render;
/**
 * 
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class HTMLButton implements Render {
    @Override
    public void beginRender(ViewContext viewContext, UIComponent component, Map params, Writer writer) throws IOException {
        writer.write("<div id='" + component.getId() + "' ");
        Map<String, AbstractValueHolder> entMap = component.getPropertys();
        for (String entKEY : entMap.keySet()) {
            AbstractValueHolder vh = entMap.get(entKEY);
            writer.write(entKEY + "='" + vh.value() + "' ");
        }
        writer.write(">");
    }
    @Override
    public void endRender(ViewContext viewContext, UIComponent component, Map params, Writer writer) throws IOException {
        writer.write("<p>aaa</p></div>");
    }
}