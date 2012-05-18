package org.more.webui.render.htmls;
import java.io.IOException;
import java.io.Writer;
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
    public void beginRender(ViewContext viewContext, UIComponent component, Writer writer) throws IOException {
        writer.write("<input class='btnLogin' id='btnLogin' name='' value='' type='submit' title='µ«¬º' />");
    }
    @Override
    public void endRender(ViewContext viewContext, UIComponent component, Writer writer) throws IOException {}
}