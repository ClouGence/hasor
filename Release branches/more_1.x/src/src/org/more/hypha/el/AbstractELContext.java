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
package org.more.hypha.el;
import java.util.Map;
import org.more.core.ognl.OgnlException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ELContext;
import org.more.hypha.ELObject;
import org.more.hypha.EvalExpression;
import org.more.hypha.PropertyBinding;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 接口{@link ELContext}的实现类。
 * Date : 2011-4-8
 * @author 赵永春
 */
public abstract class AbstractELContext implements ELContext {
    private ApplicationContext applicationContext = null;
    private IAttribute         flash              = null;
    private InnerOgnlContext   elContext          = new InnerOgnlContext();
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
    /***/
    public AbstractELContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    public void init(IAttribute flash) throws Throwable {
        this.flash = flash;
    }
    /**返回{@link ApplicationContext}对象。*/
    protected ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
    /**返回{@link IAttribute}类型的FLASH。*/
    protected IAttribute getFlash() {
        return this.flash;
    }
    //----------------------------------------------------------------------------------------------------------
    public EvalExpression getExpression(String elString) throws OgnlException {
        return new EvalExpressionImpl(this, elString);
    };
    public Object evalExpression(String elString) throws Throwable {
        return this.getExpression(elString).eval(null);
    };
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws OgnlException {
        return new PropertyBindingImpl(this, propertyEL, object);
    };
    public void addELObject(String name, ELObject elObject) {
        elObject.init(this.getApplicationContext(), this.getFlash());
        this.getThisAttribute().setAttribute(name, elObject);
    };
    //----------------------------------------------------------------------------------------------------------
    /**获取一个{@link IAttribute}接口对象，还对象可以以{@link IAttribute}接口形式访问{@link AbstractELContext}中的属性。*/
    protected IAttribute getThisAttribute() {
        return this.elContext;
    };
    public boolean contains(String name) {
        return this.getThisAttribute().contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.getThisAttribute().setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.getThisAttribute().getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.getThisAttribute().removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.getThisAttribute().getAttributeNames();
    };
    public void clearAttribute() {
        this.getThisAttribute().clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.getThisAttribute().toMap();
    }
};