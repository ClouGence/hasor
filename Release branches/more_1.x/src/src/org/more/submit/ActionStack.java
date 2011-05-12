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
package org.more.submit;
import java.util.HashMap;
import java.util.Map;
import org.more.NoDefinitionException;
import org.more.NotFoundException;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 代表一个action执行时的参数堆栈，每当请求执行Action方法时候submit都会自动创建一个新的堆栈，此外ActionStack对象提供了属性作用域的支持。
 * 其子类可以通过受保护的putScope方法来注册其他作用域。受保护createStackScope方法可以替换当前stack堆栈对象的数据存储器。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class ActionStack implements IAttribute, ScopeEnum {
    //========================================================================================Field
    private static final long       serialVersionUID    = 5001483997344333143L;
    private String                  actionName          = null;                             //调用的action名
    private String                  actionMethod        = null;                             //调用的action方法名
    private String                  invokeString        = null;                             //调用字符串
    //作用域数据
    private String                  currentMark         = null;                             //当前作用域标记。
    private IAttribute              currentScope;                                           //当前堆栈对象的数据存储器。
    private Map<String, IAttribute> scopeMap            = new HashMap<String, IAttribute>();
    //返回脚本处理器
    private String                  resultsScriptEngine = "JavaScript";                     //执行脚本引擎名。
    private String                  resultsScript       = null;                             //返回action结果之前执行的JS脚本。
    private Object[]                resultsScriptParams = null;                             //返回action结果之前执行的JS脚本所传递的参数。
    //作用域操作相关字段
    //==================================================================================Constructor
    public ActionStack(String actionName, String actionMethod, ActionStack parent, Session moreSessionScope, SubmitContext moreContextScope) {
        this.invokeString = actionName + "." + actionMethod;
        this.actionName = actionName;
        this.actionMethod = actionMethod;
        this.putScope(Scope_Stack, this.createStackScope());//Stack作用域
        this.putScope(Scope_Parent, parent);//Parent作用域
        this.putScope(Scope_Session, moreSessionScope);//Session作用域
        this.putScope(Scope_Context, moreContextScope);//Context作用域
        this.setScope(Scope_Stack);
    };
    //==========================================================================================Job
    /**获得action调用字符串。*/
    public String getInvokeString() {
        return invokeString;
    };
    /**获取当前事件发生的Action名。*/
    public String getActionName() {
        return actionName;
    };
    /**获取当前事件发生的Action方法名。*/
    public String getActionMethod() {
        return actionMethod;
    };
    /*--------------------------------------------------------------------*/
    /**获取当前堆栈的父堆栈。*/
    public ActionStack getParent() {
        return (ActionStack) this.scopeMap.get(Scope_Parent);
    };
    /**获取当前堆栈调用时使用的Session。*/
    public Session getSession() {
        return (Session) this.scopeMap.get(Scope_Session);
    };
    /**获取当前堆栈调用时使用的SubmitContext。*/
    public SubmitContext getContext() {
        return (SubmitContext) this.scopeMap.get(Scope_Context);
    };
    /**获取这个栈的作用域操作接口，该方法不受到切换作用域的影响。*/
    public IAttribute getThisStack() {
        return this.scopeMap.get(Scope_Stack);
    };
    /**获取当前正在使用的参数作用域的操作接口，如果切换了作用域则该方法的返回值也会跟着切换。*/
    public IAttribute getCurrentScopeAtt() {
        return this.currentScope;
    };
    /**
     * 获取指定的作用域的操作接口。不同于getCurrentScopeAtt方法这个方法可以获取到你想要的作用域并且无需切换作用域。
     * 如果企图获取不存在的作用域则会引发{@link NotFoundException}异常。
     */
    public IAttribute getScopeAttribute(String scope) {
        if (this.scopeMap.containsKey(scope) == false)
            throw new NotFoundException("无法获得“" + scope + "”作用域，这个作用域可能没有注册。");
        return this.scopeMap.get(scope);
    };
    /*--------------------------------------------------------------------*/
    /**从stack作用域树中查找属性，getByStackTree方法会根据stack堆栈一层一层查找。并不会涉及到session、context这两个作用域*/
    protected Object getByStackTree(String key) {
        Object obj = this.getThisStack().getAttribute(key);
        if (obj == null && this.getParent() != null)
            obj = this.getParent().getByStackTree(key);
        return obj;
    };
    /**getParam方法的字符串形式返回，如果查找不到属性则返回null。*/
    public String getParamString(String key) {
        Object obj = this.getParam(key);
        return (obj == null) ? null : obj.toString();
    };
    /** 根据stack->parent->session->context这个顺序依次查找属性，在stack中查找时是在整个stack树中查找。*/
    public Object getParam(String key) {
        Object obj = this.getByStackTree(key);
        if (obj == null && this.getSession() != null)
            obj = this.getSession().getAttribute(key);
        if (obj == null)
            obj = this.getContext().getAttribute(key);
        return obj;
    };
    /*--------------------------------------------------------------------*/
    /**获取当执行回调脚本时使用的脚本引擎名。*/
    public String getResultsScriptEngine() {
        return resultsScriptEngine;
    }
    /**设置当执行回调脚本时使用的脚本引擎名。*/
    public void setResultsScriptEngine(String resultsScriptEngine) {
        this.resultsScriptEngine = resultsScriptEngine;
    }
    /** 返回当本次Action调用结束之后进行回调的脚本处理。 */
    public String getResultsScript() {
        return resultsScript;
    };
    /** 设置当本次Action调用结束之后进行回调的脚本名称，第二个参数表明了调用脚本时传递的参数。这些脚本存放在/META-INF/submit_scripts/目录中 */
    public void setResultsScript(String resultsScript, Object... params) {
        this.resultsScript = resultsScript;
        this.resultsScriptParams = params;
    };
    Object[] getResultsScriptParams() {
        return resultsScriptParams;
    };
    /*--------------------------------------------------------------------*/
    /**获取当前操作作用域。*/
    public String getScope() {
        return this.currentMark;
    };
    /**设置当前操作的作用域，如果企图设置为未定义的作用域则会引发{@link NotFoundException}异常。注意scope参数可以是ScopeEnum接口的常量定义。*/
    public void setScope(String scope) throws NoDefinitionException {
        if (this.scopeMap.containsKey(scope) == false)
            throw new NotFoundException("不能设置到未定义的作用域，这个作用域可能没有注册。");
        this.currentScope = this.scopeMap.get(scope);
        this.currentMark = scope;
    };
    /**将一个作用域注册或者替换到scopeMap中。*/
    protected void putScope(String scopeKEY, IAttribute scope) {
        if (scope != null)
            this.scopeMap.put(scopeKEY, scope);
    };
    /**从作用域Map中移除一个作用域的注册。*/
    protected void removeScope(String scopeKEY) {
        if (scopeKEY != null)
            this.scopeMap.remove(scopeKEY);
    };
    /**测试是否存在某个作用的注册。如果存在返回true否则返回false。*/
    public boolean containsScopeKEY(String scopeKEY) {
        return this.scopeMap.containsKey(scopeKEY);
    };
    /**子类可以通过重写该方法以达到替换Stack作用域的目的。*/
    protected IAttribute createStackScope() {
        return new AttBase();
    };
    /*--------------------------------------------------------------------*/
    /**从当前参数作用域中查找一个属性是否存在。*/
    public boolean contains(String name) {
        return this.currentScope.contains(name);
    };
    /**向当前参数作用域中设置一个属性。*/
    public void setAttribute(String name, Object value) {
        this.currentScope.setAttribute(name, value);
    };
    /**向当前参数作用域中设置一个属性。*/
    public Object getAttribute(String name) {
        return this.currentScope.getAttribute(name);
    };
    /**从当前参数作用域中删除属性。*/
    public void removeAttribute(String name) {
        this.currentScope.removeAttribute(name);
    };
    /**从当前参数作用域中返回所有属性名。*/
    public String[] getAttributeNames() {
        return this.currentScope.getAttributeNames();
    };
    /**清空当前参数作用域中的属性。*/
    public void clearAttribute() {
        this.currentScope.clearAttribute();
    };
    public Map<String, Object> toMap() {
        return this.currentScope.toMap();
    };
    /**将Map中所有参数输出到当前参数作用域中。*/
    public void putALL(Map<String, ?> params) {
        if (params == null)
            return;
        IAttribute att = this.getCurrentScopeAtt();
        for (String key : params.keySet())
            att.setAttribute(key, params.get(key));
    };
    /*--------*/
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
    }
};