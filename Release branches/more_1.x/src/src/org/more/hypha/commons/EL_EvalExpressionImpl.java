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
package org.more.hypha.commons;
import java.util.Map;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.hypha.EvalExpression;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.ParentDecorator;
import org.more.util.attribute.TransformToMap;
/**
 * {@link EvalExpression}接口的实现类，在该类上可以自由使用{@link IAttribute}接口而不用考虑是否会影响到整体。
 * Date : 2011-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
class EL_EvalExpressionImpl implements EvalExpression {
    private String     expressionString = null;
    private Node       expressionNode   = null;
    private IAttribute attribute        = null;
    /***/
    public EL_EvalExpressionImpl(AbstractELContext abstractELContext, String expressionString) throws OgnlException {
        this.attribute = new ParentDecorator(abstractELContext);
        this.expressionString = expressionString;
        this.expressionNode = (Node) Ognl.parseExpression(expressionString);
    };
    public Object eval(Object thisObject) throws OgnlException {
        OgnlContext oc = new OgnlContext(this.toMap());
        oc.setCurrentObject(thisObject);
        return this.expressionNode.getValue(oc, thisObject);
    }
    public String getExpressionString() {
        return this.expressionString;
    }
    //--------------------------------------------------------
    public boolean contains(String name) {
        return this.attribute.contains(name);
    }
    public void setAttribute(String name, Object value) {
        this.attribute.setAttribute(name, value);
    }
    public Object getAttribute(String name) {
        return this.attribute.getAttribute(name);
    }
    public void removeAttribute(String name) {
        this.attribute.removeAttribute(name);
    }
    public String[] getAttributeNames() {
        return this.attribute.getAttributeNames();
    }
    public void clearAttribute() {
        this.attribute.clearAttribute();
    }
    public Map<String, Object> toMap() {
        return new TransformToMap(this.attribute);
    };
};