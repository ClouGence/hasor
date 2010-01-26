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
import org.more.NoDefinitionException;
import org.more.StateException;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 代表一个action执行时的参数堆栈，每当请求执行Action方法时候submit都会自动创建一个新的堆栈，
 * 此外ActionStack对象提供了属性作用域的支持。通过扩展该类还可以自定义属性作用域。
 * @version 2009-12-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class ActionStack implements IAttribute, ScopeEnum {
    //========================================================================================Field
    private static final long     serialVersionUID = 5001483997344333143L;
    private String                actionName;                             //调用的action名
    private String                actionMethod;                           //调用的action方法名
    private String                invokeString;                           //调用字符串
    //作用域数据
    private final ActionStack     parent;                                 //堆栈父级，只读
    protected final IAttribute    moreStackScope;                         //Stack作用域，只读
    protected final Session       moreSessionScope;                       //Session作用域，只读
    protected final SubmitContext moreContextScope;                       //Context作用域，只读
    //返回脚本处理器
    private String                resultsScript;                          //返回action结果之前执行的JS脚本。
    private Object[]              resultsScriptParams;                    //返回action结果之前执行的JS脚本所传递的参数。
    //作用域操作相关字段
    private String                currentScope;                           //当前属性作用域标记。
    private IAttribute            attributeForScope;                      //当前属性作用域操作接口。
    private boolean               synchronizeStack = true;                //如果当前作用域不是Stack是否同步作用域操作到Stack，默认启用(true)。
    //==================================================================================Constructor
    public ActionStack(ActionStack parent, Session moreSessionScope, SubmitContext moreContextScope) {
        this.parent = parent;
        this.moreStackScope = this.createStackScope();
        this.moreSessionScope = moreSessionScope;
        this.moreContextScope = moreContextScope;
    };
    /**初始化ActionStack对象。*/
    public void init() {
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
    /**获取当前堆栈的父堆栈。*/
    public ActionStack getParent() {
        return parent;
    };
    /**获取当前堆栈调用时使用的Session。*/
    public Session getSession() {
        return moreSessionScope;
    };
    /**获取当前堆栈调用时使用的SubmitContext。*/
    public SubmitContext getContext() {
        return moreContextScope;
    };
    /** 返回当本次Action调用结束之后进行回调的脚本处理。 */
    public String getResultsScript() {
        return resultsScript;
    };
    /*----------------------------------*/
    void setActionName(String actionName) {
        this.actionName = actionName;
    };
    void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    };
    void setInvokeString(String invokeString) {
        this.invokeString = invokeString;
    };
    /*----------------------------------*/
    /**从stack作用域树中查找属性，getByStackTree方法会根据stack堆栈一层一层查找。*/
    protected Object getByStackTree(String key) {
        Object obj = this.moreStackScope.getAttribute(key);
        if (obj == null && this.parent != null)
            obj = this.parent.getByStackTree(key);
        return obj;
    };
    /**getParam方法的字符串形式返回，如果查找不到属性则返回null。*/
    public String getParamString(String key) {
        Object obj = this.getParam(key);
        return (obj == null) ? null : obj.toString();
    };
    /** 根据stack->session->context这个顺序依次查找属性，在stack中查找时是在整个stack树中查找。*/
    public Object getParam(String key) {
        Object obj = this.getByStackTree(key);
        if (obj == null && this.moreSessionScope != null)
            obj = this.moreSessionScope.getAttribute(key);
        if (obj == null)
            obj = this.moreContextScope.getAttribute(key);
        return obj;
    };
    /*----------------------------------*/
    /** 设置当本次Action调用结束之后进行回调的脚本名称，第二个参数表明了调用脚本时传递的参数。这些脚本存放在/META-INF/submit_scripts/目录中 */
    public void setResultsScript(String resultsScript, Object... params) {
        this.resultsScript = resultsScript;
        this.resultsScriptParams = params;
    };
    Object[] getResultsScriptParams() {
        return resultsScriptParams;
    };
    /*----------------------------------*/
    /**获取一个boolean值，该值决定了是否同步当前参数作用域的操作到ActionStack作用域中。默认值是true。*/
    public boolean isSynchronizeStack() {
        return synchronizeStack;
    };
    /**设置一个boolean值，该值决定了是否同步当前参数作用域的操作到ActionStack作用域中。*/
    public void setSynchronizeStack(boolean synchronizeStack) {
        this.synchronizeStack = synchronizeStack;
    };
    /**获取当前操作作用域。*/
    public String getScope() {
        return currentScope;
    };
    /**设置当前操作的作用域，如果企图设置为未定义的作用域则会引发NoDefinitionException异常。注意currentScope参数必须使用ScopeEnum接口的常量定义。*/
    public void setScope(String currentScope) {
        this.attributeForScope = getScopeAttribute(currentScope);
        this.currentScope = currentScope;
    };
    /*----------------------------------*/
    /**子类可以通过重写该方法以达到替换Stack作用域的目的。*/
    protected IAttribute createStackScope() {
        return new AttBase();
    };
    /**获取当前参数作用域的操作接口*/
    protected IAttribute getCurrentScopeAtt() {
        return this.attributeForScope;
    }
    /*----------------------------------*/
    /**从当前参数作用域中查找一个属性是否存在，该方法不会跨作用域。*/
    public boolean contains(String name) {
        return this.attributeForScope.contains(name);
    };
    /**向当前参数作用域中设置一个属性，如果synchronizeStack属性配置为true则对于作用域的操作会在stack范围内重复一次。但是对于stack范围仅进行一次。*/
    public void setAttribute(String name, Object value) {
        this.attributeForScope.setAttribute(name, value);
        if (synchronizeStack == true && this.currentScope.equals(Scope_Stack) == false)
            this.moreStackScope.setAttribute(name, value);
    };
    /**向当前参数作用域中设置一个属性，如果synchronizeStack属性配置为true则对于作用域的操作会在stack范围内重复一次。但是对于stack范围仅进行一次。*/
    public Object getAttribute(String name) {
        return this.attributeForScope.getAttribute(name);
    };
    /**从当前参数作用域中删除属性，如果synchronizeStack属性配置为true则对于作用域的操作会在stack范围内重复一次。但是对于stack范围仅进行一次。*/
    public void removeAttribute(String name) {
        this.attributeForScope.removeAttribute(name);
        if (synchronizeStack == true && this.currentScope.equals(Scope_Stack) == false)
            this.moreStackScope.removeAttribute(name);
    };
    /**从当前参数作用域中返回所有属性名。*/
    public String[] getAttributeNames() {
        return this.attributeForScope.getAttributeNames();
    };
    /**清空当前参数作用域中的属性，如果synchronizeStack属性配置为true则对于作用域的操作会在stack范围内重复一次。但是对于stack范围仅进行一次。*/
    public void clearAttribute() {
        this.attributeForScope.clearAttribute();
        if (synchronizeStack == true && this.currentScope.equals(Scope_Stack) == false)
            this.moreStackScope.clearAttribute();
    };
    /*--------*/
    protected IAttribute getScopeAttribute(String scope) {
        if (Scope_Stack.equals(scope) == true)
            return this.moreStackScope;
        else if (Scope_Session.equals(scope) == true)
            if (this.moreSessionScope != null)
                return this.moreSessionScope;
            else
                throw new StateException("当前状态不支持session作用域。");
        else if (Scope_Context.equals(scope) == true)
            return this.moreContextScope;
        else
            throw new NoDefinitionException("无法切换到未定义的作用域。");
    }
    /**在指定参数作用域中查找一个属性是否存在，scope参数由ScopeEnum接口的常量定义。*/
    public boolean contains(String name, String scope) {
        return this.getScopeAttribute(scope).contains(name);
    };
    /**向指定参数作用域中设置一个属性，scope参数由ScopeEnum接口的常量定义。*/
    public void setAttribute(String name, Object value, String scope) {
        this.getScopeAttribute(scope).setAttribute(name, value);
        if (synchronizeStack == true && this.currentScope.equals(Scope_Stack) == false)
            this.moreStackScope.setAttribute(name, value);
    };
    /**从指定参数作用域中返回指定属性，scope参数由ScopeEnum接口的常量定义。*/
    public Object getAttribute(String name, String scope) {
        return this.getScopeAttribute(scope).getAttribute(name);
    };
    /**从指定参数作用域中删除属性，scope参数由ScopeEnum接口的常量定义。*/
    public void removeAttribute(String name, String scope) {
        this.getScopeAttribute(scope).removeAttribute(name);
        if (synchronizeStack == true && this.currentScope.equals(Scope_Stack) == false)
            this.moreStackScope.removeAttribute(name);
    };
    /**从指定参数作用域中返回所有属性名，scope参数由ScopeEnum接口的常量定义。*/
    public String[] getAttributeNames(String scope) {
        return this.getScopeAttribute(scope).getAttributeNames();
    };
    /**清空指定参数作用域中的属性，scope参数由ScopeEnum接口的常量定义。*/
    public void clearAttribute(String scope) {
        this.getScopeAttribute(scope).clearAttribute();
        if (synchronizeStack == true && this.currentScope.equals(Scope_Stack) == false)
            this.moreStackScope.clearAttribute();
    };
}