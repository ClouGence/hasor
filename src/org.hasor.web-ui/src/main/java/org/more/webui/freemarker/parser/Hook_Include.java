/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.webui.freemarker.parser;
import org.more.webui.component.UIComponent;
import org.more.webui.context.ViewContext;
import freemarker.core.TemplateElement;
import freemarker.template.Template;
/**
 * 负责根据标签元素创建组建对象，该类会对freemarker有强烈的版本限制要求。更换freemarker版本可能会引发问题。
 * @version : 2012-5-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class Hook_Include implements ElementHook {
    public static String Name = "Include";
    public UIComponent beginAtBlcok(TemplateScanner scanner, TemplateElement e, UIComponent parent, ViewContext viewContext) throws ElementHookException {
        try {
            String includeName = e.getDescription().split(" ")[1];
            includeName = includeName.substring(1, includeName.length() - 1);
            /*处理include的el*/
            viewContext.processTemplateString(includeName.trim());
            Template includeTemp = e.getTemplate().getConfiguration().getTemplate(includeName);
            scanner.parser(includeTemp, parent, viewContext);
            return null;
        } catch (Exception e2) {
            throw new ElementHookException("解析异常：处理include发生错误“" + e.getDescription() + "”", e2);
        }
    }
    public void endAtBlcok(TemplateScanner scanner, TemplateElement e, UIComponent parent, ViewContext viewContext) throws ElementHookException {}
}