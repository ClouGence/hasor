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
package org.more.webui.component.values;
import java.util.Map;
import org.more.ognl.Node;
import org.more.ognl.Ognl;
import org.more.ognl.OgnlException;
import org.more.util.map.DecSequenceMap;
import org.more.webui.component.UIComponent;
import org.more.webui.context.ViewContext;
/**
 * 表达式方法执行
 * @version : 2012-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class MethodExpression {
    private String expressionString = null;
    private Node   elNodeTree       = null;
    //
    public MethodExpression(String expressionString) {
        this.expressionString = expressionString;
    }
    protected Node getNodeTree() throws OgnlException {
        if (this.elNodeTree == null)
            this.elNodeTree = (Node) Ognl.parseExpression(expressionString);
        return this.elNodeTree;
    }
    public Object execute(UIComponent component, ViewContext viewContext) throws Throwable {
        return this.execute(component, viewContext, null);
    }
    public Object execute(UIComponent component, ViewContext viewContext, Map<String, Object> params) throws Throwable {
        try {
            Map<String, Object> viewEL = viewContext.getViewELContext();
            if (params != null) {
                DecSequenceMap<String, Object> decMap = new DecSequenceMap<String, Object>();
                decMap.addMap(params);
                decMap.addMap(viewEL);
                viewEL = decMap;
            }
            viewEL.put("this", component);
            return Ognl.getValue(this.getNodeTree(), viewEL);
        } catch (OgnlException e) {
            throw e.getReason();
        }
    }
}