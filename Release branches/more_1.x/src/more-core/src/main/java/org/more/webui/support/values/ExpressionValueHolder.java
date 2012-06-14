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
package org.more.webui.support.values;
import java.util.Map;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlException;
import org.more.webui.context.ViewContext;
import org.more.webui.support.UIComponent;
/**
 * 静态值
 * @version : 2012-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class ExpressionValueHolder extends AbstractValueHolder {
    private String readExpression  = null;
    private String writeExpression = null;
    private Node   readNodeTree    = null;
    private Node   writeNodeTree   = null;
    //
    public ExpressionValueHolder(String expressionString) {
        this.readExpression = expressionString;
        this.writeExpression = expressionString;
    }
    public ExpressionValueHolder(String readExpression, String writeExpression) {
        this.readExpression = readExpression;
        this.writeExpression = writeExpression;
    }
    /**属性读取{@link Node}*/
    protected Node getReadNode() throws OgnlException {
        if (this.readNodeTree == null)
            this.readNodeTree = (Node) Ognl.parseExpression(this.readExpression);
        return this.readNodeTree;
    }
    /**属性写入{@link Node}*/
    protected Node getWriteNode() throws OgnlException {
        if (this.writeExpression == null)
            return null;
        if (this.writeNodeTree == null)
            this.writeNodeTree = (Node) Ognl.parseExpression(this.writeExpression);
        return this.writeNodeTree;
    }
    @Override
    public void updateModule(UIComponent component, ViewContext viewContext) throws OgnlException {
        if (getWriteNode() == null)
            return;//不支持写入
        Map<String, Object> elContext = viewContext.getViewELContext();
        Ognl.setValue(this.getWriteNode(), elContext, this.value());
        this.value(null);
        this.getValue().needUpdate = false;
    }
    @Override
    public Object value() {
        Object var = super.value();
        if (var != null)
            return var;
        //
        try {
            ViewContext viewContext = ViewContext.getCurrentViewContext();
            Map<String, Object> elContext = viewContext.getViewELContext();
            return Ognl.getValue(this.getReadNode(), elContext);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean isReadOnly() {
        return (this.writeExpression == null) ? true : false;
    }
}