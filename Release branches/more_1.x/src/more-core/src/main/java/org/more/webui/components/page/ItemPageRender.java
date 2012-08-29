package org.more.webui.components.page;
import java.io.IOException;
import java.io.Writer;
import org.more.webui.components.page.PageCom.Mode;
import org.more.webui.context.ViewContext;
import org.more.webui.render.Render;
import org.more.webui.render.UIRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
/**
 * ∑÷“≥◊ÈΩ®£¨“≥¬Î.
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@UIRender(tagName = "ui_pNum")
public class ItemPageRender implements Render<ItemPageCom> {
    private boolean isRun(AbstractItemCom component) {
        PageCom page = (PageCom) component.getParent();
        return page.runMode == Mode.Item;
    }
    @Override
    public void beginRender(ViewContext viewContext, ItemPageCom component, TemplateBody arg3, Writer writer) throws IOException, TemplateModelException {
        if (this.isRun(component) == false)
            return;
        writer.write("<a href='" + component.getPageLinkAsTemplate(viewContext) + "'");
        int indexPage = (Integer) DeepUnwrap.permissiveUnwrap(arg3.getEnvironment().getVariable("PageIndex"));
        writer.write(" pageIndex='" + indexPage + "'");
        writer.write(">");
    }
    @Override
    public void render(ViewContext viewContext, ItemPageCom component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        if (isRun(component) == false)
            return;
        if (arg3 != null)
            arg3.render(writer);
    }
    @Override
    public void endRender(ViewContext viewContext, ItemPageCom component, TemplateBody arg3, Writer writer) throws IOException {
        if (isRun(component) == false)
            return;
        writer.write("</a>");
    }
}