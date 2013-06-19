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
import org.more.core.log.ILog;
import org.more.core.log.LogFactory;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.hypha.ELException;
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
    private static ILog log              = LogFactory.getLog(EL_EvalExpressionImpl.class);
    private String      expressionString = null;
    private Node        expressionNode   = null;
    private IAttribute  attribute        = null;
    /*------------------------------------------------------------------------------*/
    public EL_EvalExpressionImpl(AbstractELContext abstractELContext, String expressionString) throws ELException {
        this.attribute = new ParentDecorator(abstractELContext.getELAttribute());//创建一个父级，以隔离来自elContext的属性。
        log.debug("init el attribute elString = {%0}, Set = {%1}", expressionString, this.attribute);
        this.expressionString = expressionString;
        try {
            this.expressionNode = (Node) Ognl.parseExpression(expressionString);
            log.debug("init expressionNode OK!");
        } catch (OgnlException e) {
            log.error("init expressionNode ERROR! , message = {%0}", e);
            throw new ELException("parseExpression " + expressionString + " error.");
        }
    };
    /*------------------------------------------------------------------------------*/
    public Object evalNode(Object thisObject) throws ELException {
        OgnlContext oc = new EL_OgnlContext(this.toMap());
        oc.setCurrentObject(thisObject);
        try {
            Object obj = this.expressionNode.getValue(oc, thisObject);
            log.debug("eval succeed! elString = {%0} , value = {%1}", expressionString, obj);
            return obj;
        } catch (OgnlException e) {
            log.error("eval error! elString = {%0} , error = {%1}", expressionString, e);
            throw new ELException("eval ‘" + expressionString + "’ error!", e);
        }
    }
    public Object eval(Object thisObject) throws ELException {
        OgnlContext oc = new EL_OgnlContext(this.toMap());
        oc.setCurrentObject(thisObject);
        try {
            Object obj = Ognl.getValue(expressionString, oc);
            log.debug("eval succeed! elString = {%0} , value = {%1}", expressionString, obj);
            return obj;
        } catch (OgnlException e) {
            log.error("eval error! elString = {%0} , error = {%1}", expressionString, e);
            throw new ELException("eval ‘" + expressionString + "’ error!", e);
        }
    }
    public String getExpressionString() {
        return this.expressionString;
    }
    /*------------------------------------------------------------------------------*/
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