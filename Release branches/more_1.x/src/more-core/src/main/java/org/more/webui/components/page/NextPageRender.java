/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * 分页组建：下一页.
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
@UIRender(tagName = "ui_pNext")
public class NextPageRender implements Render<NextPageCom> {
    private boolean isRun(AbstractItemCom component) {
        PageCom page = (PageCom) component.getParent();
        return page.runMode == Mode.Next;
    }
    @Override
    public void beginRender(ViewContext viewContext, NextPageCom component, TemplateBody arg3, Writer writer) throws IOException, TemplateModelException {
        if (this.isRun(component) == false)
            return;
        writer.write("<a href='" + component.getPageLinkAsTemplate(viewContext) + "'");
        int indexPage = (Integer) DeepUnwrap.permissiveUnwrap(arg3.getEnvironment().getVariable("PageIndex"));
        writer.write(" pageIndex='" + indexPage + "'");
        writer.write(">");
    }
    @Override
    public void render(ViewContext viewContext, NextPageCom component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        if (isRun(component) == false)
            return;
        if (arg3 != null)
            arg3.render(writer);
    }
    @Override
    public void endRender(ViewContext viewContext, NextPageCom component, TemplateBody arg3, Writer writer) throws IOException {
        if (isRun(component) == false)
            return;
        writer.write("</a>");
    }
}