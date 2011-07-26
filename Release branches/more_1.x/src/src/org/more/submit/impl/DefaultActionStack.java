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
package org.more.submit.impl;
import java.net.URI;
import java.util.Map;
import org.more.submit.ActionStack;
import org.more.submit.SubmitService;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 代表一个action执行时的参数堆栈，每当请求执行Action方法时候submit都会自动创建一个新的堆栈，此外ActionStack对象提供了属性作用域的支持。
 * 其子类可以通过受保护的putScope方法来注册其他作用域。受保护createStackScope方法可以替换当前stack堆栈对象的数据存储器。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class DefaultActionStack implements ActionStack {
    private AttBase       attBase = new AttBase();
    private ActionStack   parent  = null;
    private SubmitService service = null;
    //
    /*--------*/
    public DefaultActionStack(URI uri, ActionStack parent, SubmitService service) {
        this.parent = parent;
        this.service = service;
    };
    /*--------*/
    public ActionStack getParent() {
        return this.parent;
    };
    public String getParamString(String key) {
        Object param = this.getParam(key);
        if (param != null)
            return param.toString();
        return null;
    };
    public SubmitService getSubmitService() {
        return this.service;
    };
    public Object getParam(String key) {
        Object obj = this.attBase.get(key);
        if (obj == null && this.parent != null)
            obj = this.parent.getParam(key);
        if (obj == null)
            obj = this.service.getScopeStack().getAttribute(key);
        return obj;
    };
    /*--------*/
    /**从当前参数作用域中查找一个属性是否存在。*/
    public boolean contains(String name) {
        return this.attBase.contains(name);
    };
    /**向当前参数作用域中设置一个属性。*/
    public void setAttribute(String name, Object value) {
        this.attBase.setAttribute(name, value);
    };
    /**向当前参数作用域中设置一个属性。*/
    public Object getAttribute(String name) {
        return this.attBase.getAttribute(name);
    };
    /**从当前参数作用域中删除属性。*/
    public void removeAttribute(String name) {
        this.attBase.removeAttribute(name);
    };
    /**从当前参数作用域中返回所有属性名。*/
    public String[] getAttributeNames() {
        return this.attBase.getAttributeNames();
    };
    /**清空当前参数作用域中的属性。*/
    public void clearAttribute() {
        this.attBase.clearAttribute();
    };
    public Map<String, Object> toMap() {
        return this.attBase.toMap();
    };
    /**将Map中所有参数输出到当前参数作用域中。*/
    public void putALL(Map<String, ?> params) {
        if (params == null)
            return;
        this.attBase.putAll(params);
    };
    /*--------*/
    protected IAttribute getScopeAttribute(String scope) {
        return this.service.getScope(scope);
    }
    /**在指定参数作用域中查找一个属性是否存在，scope参数由ScopeEnum接口的常量定义。*/
    public boolean contains(String name, String scope) {
        return this.getScopeAttribute(scope).contains(name);
    };
    /**向指定参数作用域中设置一个属性，scope参数由ScopeEnum接口的常量定义。*/
    public void setAttribute(String name, Object value, String scope) {
        this.getScopeAttribute(scope).setAttribute(name, value);
    };
    /**从指定参数作用域中返回指定属性，scope参数由ScopeEnum接口的常量定义。*/
    public Object getAttribute(String name, String scope) {
        return this.getScopeAttribute(scope).getAttribute(name);
    };
    /**从指定参数作用域中删除属性，scope参数由ScopeEnum接口的常量定义。*/
    public void removeAttribute(String name, String scope) {
        this.getScopeAttribute(scope).removeAttribute(name);
    };
    /**从指定参数作用域中返回所有属性名，scope参数由ScopeEnum接口的常量定义。*/
    public String[] getAttributeNames(String scope) {
        return this.getScopeAttribute(scope).getAttributeNames();
    };
    /**清空指定参数作用域中的属性，scope参数由ScopeEnum接口的常量定义。*/
    public void clearAttribute(String scope) {
        this.getScopeAttribute(scope).clearAttribute();
    };
    /**将Map中所有参数输出到指定的参数作用域中。*/
    public void putALL(Map<String, ?> params, String scope) {
        IAttribute att = this.getScopeAttribute(scope);
        for (String key : params.keySet())
            att.setAttribute(key, params.get(key));
    };
};