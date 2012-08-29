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
package org.more.webui.components.swfupload;
import java.io.IOException;
import java.io.Writer;
import org.more.webui.context.ViewContext;
import org.more.webui.render.AbstractRender;
import org.more.webui.render.UIRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
@UIRender(tagName = "ui_SWFUpload")
public class SWFUploadRender extends AbstractRender<SWFUpload> {
    @Override
    protected String tagName(ViewContext viewContext, SWFUpload component) {
        return "div";
    }
    @Override
    public void render(ViewContext viewContext, SWFUpload component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        writer.write("  <div id='" + component.getInfoTargetID() + "'>");
        writer.write("    <span>Upload Queue</span>");
        writer.write("  </div>");
        writer.write("  <span id='" + component.getUploadButtonID() + "'></span>");
        writer.write("  <input id='" + component.getSelectButtonID() + "' type='button' value='Select Files' />");
        writer.write("  <input id='" + component.getCancelButtonID() + "' type='button' value='Cancel All Uploads' disabled='disabled' />");
    }
}