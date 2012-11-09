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
import org.more.webui.context.ViewContext;
import org.more.webui.render.support.Render;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
/**
 * ∑÷“≥◊ÈΩ®.
 * @version : 2012-5-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class AbstractItemRender implements Render<AbstractItem> {
    @Override
    public void beginRender(ViewContext viewContext, AbstractItem component, TemplateBody arg3, Writer writer) throws IOException, TemplateModelException {}
    @Override
    public void render(ViewContext viewContext, AbstractItem component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        PageCom pageCom = (PageCom) component.getParent();
        if (pageCom.renderMode != component.getRenderMode())
            return;
        //int indexPage = (Integer) DeepUnwrap.permissiveUnwrap(arg3.getEnvironment().getVariable("PageIndex"));
        if (arg3 != null)
            arg3.render(writer);
    }
    @Override
    public void endRender(ViewContext viewContext, AbstractItem component, TemplateBody arg3, Writer writer) throws IOException, TemplateModelException {}
}