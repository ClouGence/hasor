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
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 当执行Action方法时候submit3.0自动创建一个调用堆栈，如果action在处理中再次调用了其他action那么submit将会创建另一个堆栈对象，
 * 而这个堆栈对象则成为哪个新堆栈的父级。通过ActionStack可以获取在执行doAction时候传递的参数。
 * <br/>Date : 2009-12-2
 * @author 赵永春
 */
public class ActionStack implements IAttribute {
    //========================================================================================Field
    private static final long serialVersionUID    = 5001483997344333143L;
    private IAttribute        stackAtt            = new AttBase();       //保存当前堆栈的属性集合
    private ActionStack       parent              = null;                //堆栈的父级
    private String            actionName          = null;                //调用的action名
    private String            actionMethod        = null;                //调用的action方法名
    private String            invokeString        = null;                //调用字符串
    private Session           session             = null;                //会话
    private SubmitContext     context             = null;                //上下文
    private String            resultsScript       = null;                //当完成action调用之后执行的JS脚本
    private Object[]          resultsScriptParams = null;                //当完成action调用之后执行的JS脚本
    //==================================================================================Constructor
    ActionStack(ActionStack parent, Session session, SubmitContext context) {
        this.parent = parent;
        this.session = session;
        this.context = context;
    };
    //==========================================================================================Job
    /**
     * 获得action调用字符串。
     * @return 返回action调用字符串。
     */
    public String getInvokeString() {
        return invokeString;
    };
    /**
     * 获取当前事件发生的Action名。
     * @return 返回事件发生的Action名。
     */
    public String getActionName() {
        return actionName;
    };
    /**
     * 获取当前事件发生的Action方法名。
     * @return 返回当前事件发生的Action方法名
     */
    public String getActionMethod() {
        return actionMethod;
    };
    /**
     * 先以getAttribute方式查找参数如果找不到则到父级ActionStack中getAttribute中查找属性。
     * 当查找到顶层栈中仍然没有参数时则到session中查找，如果找不到则到SubmitContext中查找。
     * 如果还找不到则返回null。
     * @param key 要获取的参数名称。
     * @return 返回获取的堆栈参数。
     */
    public Object getParam(String key) {
        Object obj = this.getParamPrivate(key);
        if (obj == null && this.session != null)
            obj = this.session.getAttribute(key);
        if (obj == null)
            obj = this.context.getAttribute(key);
        return obj;
    };
    private Object getParamPrivate(String key) {
        Object obj = this.getAttribute(key);
        if (obj == null && this.parent != null)
            obj = this.parent.getParamPrivate(key);
        return obj;
    };
    /**
     * 从当前堆栈中获取指定名称的堆栈参数，如果当前堆栈参数表中检索不到则自动向父级堆栈检索一直到顶层堆栈为止，
     * 当顶层堆栈也无法索引该参数时，查找顶层栈的session然后查找顶层栈的context直至都返回null。
     * @param key 要获取的参数名称。
     * @return 返回获取的堆栈参数。
     */
    public String getParamString(String key) {
        Object obj = this.getParam(key);
        return (obj == null) ? null : obj.toString();
    }
    /**
     * 获取当前堆栈的父堆栈。
     * @return 返回当前堆栈的父堆栈。
     */
    public ActionStack getParent() {
        return parent;
    };
    /**
     * 获取当前堆栈调用时使用的Session。
     * @return 返回当前堆栈调用时使用的Session。
     */
    public Session getSession() {
        return session;
    };
    /**
     * 获取当前堆栈调用时使用的SubmitContext。
     * @return 返回当前堆栈调用时使用的SubmitContext。
     */
    public SubmitContext getContext() {
        return context;
    }
    /** 返回当本次Action调用结束之后进行回调的脚本处理。 */
    public String getResultsScript() {
        return resultsScript;
    }
    /** 设置当本次Action调用结束之后进行回调的脚本名称。这些脚本存放在/META-INF/submit_scripts/目录中 */
    public void setResultsScript(String resultsScript, Object... params) {
        this.resultsScript = resultsScript;
        this.resultsScriptParams = params;
    }
    Object[] getResultsScriptParams() {
        return resultsScriptParams;
    }
    //==========================================================================================Job
    void setParent(ActionStack parent) {
        this.parent = parent;
    };
    void setActionName(String actionName) {
        this.actionName = actionName;
    };
    void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    };
    void setInvokeString(String invokeString) {
        this.invokeString = invokeString;
    }
    //==========================================================================================Att
    @Override
    public void clearAttribute() {
        this.stackAtt.clearAttribute();
    }
    @Override
    public boolean contains(String name) {
        return this.stackAtt.contains(name);
    }
    @Override
    public Object getAttribute(String name) {
        return this.stackAtt.getAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.stackAtt.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.stackAtt.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.stackAtt.setAttribute(name, value);
    }
}