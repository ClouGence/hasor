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
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.el.ELContext;
import org.more.hypha.el.ELException;
import org.more.hypha.el.ELObject;
import org.more.hypha.el.EvalExpression;
import org.more.hypha.el.PropertyBinding;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 接口{@link ELContext}的实现类，该类{@link IAttribute}接口实现方法所使用的属性集合是由{@link #getELAttribute()}方法提供的。
 * Date : 2011-4-8
 * @author 赵永春
 */
public class AbstractELContext implements ELContext {
    private static Log                 log                = LogFactory.getLog(AbstractELContext.class);
    private AbstractApplicationContext applicationContext = null;
    private InnerOgnlContext           elAttribute        = null;
    /*------------------------------------------------------------------------------*/
    /**该类的目的是为了支持{@link ELObject}类型对象，该对象是以{@link IAttribute}接口形式向外提供。*/
    private class InnerOgnlContext extends AttBase<Object> {
        private static final long serialVersionUID = 8423446527838340104L;
        private Log               log              = LogFactory.getLog(InnerOgnlContext.class);
        public Object get(Object key) {
            Object obj = super.get(key);
            if (obj != null) {
                //begin...
                if (obj instanceof ELObject)
                    try {
                        obj = ((ELObject) obj).getValue();
                        log.debug("get {%0} ELObject value {%1}", key, obj);
                    } catch (ELException e) {
                        obj = null;
                        log.error("invoke {%0} ELObject.getValue() an error. error = {%1}", key, e);
                    }
                //...end
            } else
                log.debug("get {%0} Object. value = {%1}", key, obj);
            return obj;
        };
        public Object put(String key, Object newValue) {
            Object obj = super.get(key);
            if (obj != null) {
                //begin...
                if (obj instanceof ELObject)
                    try {
                        ((ELObject) obj).setValue(newValue);
                        log.debug("set {%0} ELObject newValue, newValue = {%1}", key, newValue);
                    } catch (ELException e) {
                        log.error("invoke {%0} ELObject.setValue() an error. newValue = {%1} , oldValue = {%2} ,error = {%3}", key, newValue, obj, e);
                    }
                //...end
            } else {
                super.put(key, newValue);
                obj = newValue;
                log.debug("set {%0} Object. value = {%1}", key, newValue);
            }
            return obj;
        };
    };
    /*------------------------------------------------------------------------------*/
    public void init(AbstractApplicationContext applicationContext) {
        if (applicationContext != null)
            log.info("init ELContext, applicationContext = {%0}", applicationContext);
        else
            log.warning("init ELContext, applicationContext is null.");
        this.applicationContext = applicationContext;
    };
    /**返回{@link AbstractApplicationContext}对象。*/
    protected AbstractApplicationContext getApplicationContext() {
        return this.applicationContext;
    };
    /**获取一个{@link IAttribute}接口对象，还对象可以以{@link IAttribute}接口形式访问{@link AbstractELContext}中的属性。*/
    IAttribute<Object> getELAttribute() {
        if (this.elAttribute == null) {
            this.elAttribute = new InnerOgnlContext();
            log.debug("created ognlContext ,{%0}", this.elAttribute);
        }
        return this.elAttribute;
    };
    /*------------------------------------------------------------------------------*/
    public EvalExpression getExpression(String elString) throws ELException {
        if (elString == null) {
            log.warning("make expression an error elString is null.");
            return null;
        }
        log.debug("make expression EL = '{%0}'", elString);
        return new EL_EvalExpressionImpl(this, elString);
    };
    public Object evalExpression(String elString) throws ELException {
        if (elString == null) {
            log.warning("eval expression an error elString is null.");
            return null;
        }
        EvalExpression exp = this.getExpression(elString);
        Object obj = exp.eval(null);
        log.debug("eval expression EL = '{%0}' result = {%1}", elString, obj);
        return obj;
    };
    public PropertyBinding getPropertyBinding(String propertyEL, Object object) throws ELException {
        if (propertyEL == null || object == null) {
            log.warning("make propertyEL an error propertyEL or object is null.");
            return null;
        }
        log.debug("make propertyEL property= '{%0}' , propertyObject = {%1}", propertyEL, object);
        return new EL_PropertyBindingImpl(this, propertyEL, object);
    };
    public void addELObject(String name, ELObject elObject) {
        if (name == null) {
            log.warning("add el Object an error , name is null");
            return;
        }
        if (elObject == null) {
            //remove
            log.info("remove el Object name = '{%0}'", name);
            this.getELAttribute().removeAttribute(name);
        } else {
            //add
            log.info("add el Object name = '{%0}' , object = {%1}", name, elObject);
            elObject.init(this.getApplicationContext(), this.getApplicationContext().getBeanResource().getFlash());
            this.getELAttribute().setAttribute(name, elObject);
        }
    };
    /*------------------------------------------------------------------------------*/
    public boolean contains(String name) {
        return this.getELAttribute().contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.getELAttribute().setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.getELAttribute().getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.getELAttribute().removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.getELAttribute().getAttributeNames();
    };
    public void clearAttribute() {
        this.getELAttribute().clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.getELAttribute().toMap();
    }
    public int size() {
        return this.getELAttribute().size();
    }
};