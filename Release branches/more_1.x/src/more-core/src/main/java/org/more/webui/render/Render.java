package org.more.webui.render;
import java.io.IOException;
import java.io.Writer;
import org.more.webui.components.UIComponent;
import org.more.webui.context.ViewContext;
/**
 * ◊ÈΩ®‰÷»æ∆˜
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public interface Render {
    public void beginRender(ViewContext viewContext, UIComponent component, Writer writer) throws IOException;
    public void endRender(ViewContext viewContext, UIComponent component, Writer writer) throws IOException;
}