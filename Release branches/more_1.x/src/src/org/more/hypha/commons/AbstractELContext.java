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
import org.more.core.ognl.OgnlException;
import org.more.hypha.ELContext;
import org.more.hypha.ELObject;
import org.more.hypha.EvalExpression;
import org.more.hypha.PropertyBinding;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 接口{@link ELContext}的实现类。
 * Date : 2011-4-8
 * @author 赵永春
 */
public abstract class AbstractELContext implements ELContext {
    private AbstractApplicationContext applicationContext = null;
    private InnerOgnlContext           ognlContext        = new InnerOgnlContext();
    private class InnerOgnlContext extends AttBase {
        private static final long serialVersionUID = 8423446527838340104L;
        public Object get(Object key) {
            Object obj = super.get(key);
            if (obj instanceof ELObject)
                return ((ELObject) obj).getValue();
            return obj;
        };
        public Object put(String key, Object value) {
            Object obj = super.get(key);
            if (obj instanceof ELObject) {
                ((ELObject) obj).setValue(value);
                return value;
            } else
                return super.put(key, value);
        };
    };
    //----------------------------------------------------------------------------------------------------------
    public void init(AbstractApplicationContext applicationContext) throws Throwable {
        this.applicationContext = applicationContext;
    }
    /**返回{@link AbstractApplicationContext}对象。*/
    protected AbstractApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
    //----------------------------------------------------------------------------------------------------------
    public EvalExpression getExpression(String elString) throws OgnlException {
        return new EL_EvalExpressionImpl(this, elString);
    };
    public Object evalExpression(String elString) throws Throwable {
        return this.getExpression(elString).eval(null);
    };
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws OgnlException {
        return new EL_PropertyBindingImpl(this, propertyEL, object);
    };
    public void addELObject(String name, ELObject elObject) {
        elObject.init(this.getApplicationContext(), this.getApplicationContext().getBeanResource().getFlash());
        this.getOgnlContext().setAttribute(name, elObject);
    };
    //----------------------------------------------------------------------------------------------------------
    /**获取一个{@link IAttribute}接口对象，还对象可以以{@link IAttribute}接口形式访问{@link AbstractELContext}中的属性。*/
    public IAttribute getOgnlContext() {
        return this.ognlContext;
    };
    public boolean contains(String name) {
        return this.getOgnlContext().contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.getOgnlContext().setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.getOgnlContext().getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.getOgnlContext().removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.getOgnlContext().getAttributeNames();
    };
    public void clearAttribute() {
        this.getOgnlContext().clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.getOgnlContext().toMap();
    }
};