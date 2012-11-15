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
package org.more.webui.render.output;
import java.io.IOException;
import java.io.Writer;
import org.more.webui.components.UIOutput;
import org.more.webui.context.ViewContext;
import org.more.webui.render.AbstractRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 将组建的值输出到span标签中。
 * <br><b>客户端模型</b>：UIOutput（UIOutput.js）
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class OutputRender<T extends UIOutput> extends AbstractRender<T> {
    @Override
    public String getClientType() {
        return "UIOutput";
    }
    @Override
    public String tagName(ViewContext viewContext, T component) {
        return "span";
    }
    @Override
    public void render(ViewContext viewContext, T component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        arg3.render(writer);
        writer.append(String.valueOf(component.getValue()));
    }
}