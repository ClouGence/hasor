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
import org.more.core.ognl.OgnlException;
import org.more.util.StringConvertUtil;
import org.more.webui.context.ViewContext;
import org.more.webui.support.UIComponent;
/**
 * 属性值操作类，该类使用线程隔离的方式操作每一个属性。
 * @version : 2012-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractValueHolder {
    private Object metaValue = null;
    public static class Value {
        boolean needUpdate = false;
        Object  newValue   = null;
    }
    /**该方式是为了避免不同线程之间的干扰*/
    private ThreadLocal<Value> newValue = new ThreadLocal<Value>();
    @Override
    public String toString() {
        Object var = this.value();
        return (var == null) ? "null" : var.toString();
    }
    public <T> T valueTo(Class<T> toType) {
        return StringConvertUtil.changeType(this.value(), toType);
    }
    /**返回模型上的属性值。*/
    public Object value() {
        Value v = this.newValue.get();
        return (v != null) ? v.newValue : null;
    }
    /**写入属性值，被写入的属性值会在调用{@link #updateModule(ViewContext)}被写入到Bean中。*/
    public void value(Object newValue) {
        Value v = this.newValue.get();
        if (v == null) {
            v = new Value();
            this.newValue.set(v);
        }
        v.newValue = newValue;
        v.needUpdate = true;
    }
    /**返回原始信息值（每个线程都有一个独立的value，当某一个线程调用了reset方法时，该值会被恢复到那个线程上。）*/
    public Object getMetaValue() {
        return this.metaValue;
    }
    /**设置原始信息值（每个线程都有一个独立的value，当某一个线程调用了reset方法时，该值会被恢复到那个线程上。）*/
    public void setMetaValue(Object metaValue) {
        this.metaValue = metaValue;
    }
    /**返回一个boolean，该值决定该属性是否为只读模式。*/
    public abstract boolean isReadOnly();
    /**返回一个boolean，该值决定是否需要执行updateModule更新操作。*/
    public boolean isUpdate() {
        Value v = this.newValue.get();
        return (v != null) ? v.needUpdate : false;
    };
    protected Value getValue() {
        return this.newValue.get();
    };
    /**将属性重置为初始化状态*/
    public void reset() {
        Value v = this.newValue.get();
        if (v == null) {
            v = new Value();
            this.newValue.set(v);
        }
        v.newValue = this.metaValue;
        v.needUpdate = false;
    };
    /**将写入{@link AbstractValueHolder}的属性的值更新到模型中。*/
    public abstract void updateModule(UIComponent component, ViewContext viewContext) throws OgnlException;
}