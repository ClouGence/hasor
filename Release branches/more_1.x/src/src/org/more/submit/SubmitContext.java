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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.FormatException;
import org.more.NoDefinitionException;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * submit3.0的核心接口，任何action的调用都是通过这个接口进行的。
 * <br/>Date : 2009-12-1
 * @author 赵永春
 */
public class SubmitContext implements IAttribute {
    //========================================================================================Field
    private static final long serialVersionUID = 3966376070855006285L;
    private ActionContext     actionContext    = null;
    private IAttribute        contextAtt       = new AttBase();
    //==================================================================================Constructor
    SubmitContext(ActionContext actionContext) {
        this.actionContext = actionContext;
    }
    //==========================================================================================Job
    /**获取SubmitContext用于保存属性的属性保存器。*/
    public IAttribute getContextAtt() {
        return contextAtt;
    }
    /**设置SubmitContext用于保存属性的属性保存器，如果设置参数为空则会引发NullPointerException异常。*/
    public void setContextAtt(IAttribute contextAtt) {
        if (contextAtt == null)
            throw new NullPointerException("SubmitContext不接受空属性保存器。");
        this.contextAtt = contextAtt;
    }
    /** 获取已经定义的Action名集合。*/
    public String[] getActionNames() {
        return actionContext.getActionNames();
    };
    /** 获取一个指定的Action类型，参数为action名。*/
    public Class<?> getActionType(String actionName) {
        return actionContext.getActionType(actionName);
    };
    /**
     * 执行调用action的处理过程，如果action配置了过滤器则装配其过滤器之后在执行。
     * @param invokeString 调用action所使用的调用字符串。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doAction(String invokeString) throws Throwable {
        return this.doAction(invokeString, null, new HashMap<String, Object>());
    };
    /**
     * 执行调用action的处理过程，如果action配置了过滤器则装配其过滤器之后在执行，该方法提供了一个向action传递参数的支持。
     * @param invokeString 调用action所使用的调用字符串。
     * @param session action调用时使用的会话，会话类似一个数据缓存器。
     * @param params 调用action目标所给action传递的参数，该参数在堆栈中保存。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doAction(String invokeString, Session session, Object... params) throws Throwable {
        HashMap<String, Object> vars = new HashMap<String, Object>();
        for (Integer i = 0; i < params.length; i++)
            vars.put(i.toString(), params[i]);
        return this.doAction(invokeString, session, vars);
    };
    /**
     * 执行调用action的处理过程，如果action配置了过滤器则装配其过滤器之后在执行，该方法提供了一个向action传递参数的支持。
     * @param invokeString 调用action所使用的调用字符串。
     * @param session action调用时使用的会话，会话类似一个数据缓存器。
     * @param params 调用action目标所给action传递的参数，该参数在堆栈中保存。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doAction(String invokeString, Session session, Map<String, ?> params) throws Throwable {
        //一、创建并获取堆栈对象。
        ActionStack stack = this.parseInvokeString(invokeString, new ActionStack(null, session, this));
        String actionName = stack.getActionName();
        if (this.actionContext.containsAction(actionName) == false)
            throw new NoDefinitionException("找不到action[" + actionName + "]的定义。");
        //
        if (params != null)
            for (String key : params.keySet())
                stack.setAttribute(key, params.get(key));
        //二、获取调用对象
        ActionInvoke invoke = this.actionContext.findAction(actionName, stack.getActionMethod());
        //三、配置私有过滤器
        invoke = this.actionContext.configPrivateFilter(actionName, invoke);
        //三、配置共有过滤器
        invoke = this.actionContext.configPublicFilter(actionName, invoke);
        //四、执行调用
        return invoke.invoke(stack);
    };
    /**
     * 调用action被调用的action是传承自event的，这包括了传承堆栈和会话信息。注意：该方法只有当在action执行期间调用才会发挥作用。
     * @param invokeString 调用action所使用的调用字符串。
     * @param event 所承接的堆栈对象。
     * @param params 调用action目标所给action传递的参数，该参数在堆栈中保存。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doActionOnStack(String invokeString, ActionStack stack, Object... params) throws Throwable {
        HashMap<String, Object> vars = new HashMap<String, Object>();
        for (Integer i = 0; i < params.length; i++)
            vars.put(i.toString(), params[i]);
        return this.doActionOnStack(invokeString, stack, vars);
    };
    /**
     * 调用action被调用的action是传承自event的，这包括了传承堆栈和会话信息。注意：该方法只有当在action执行期间调用才会发挥作用。
     * @param invokeString 调用action所使用的调用字符串。
     * @param event 所承接的堆栈对象。
     * @param params 调用action目标所给action传递的参数，该参数在堆栈中保存。
     * @return 返回处理结果。
     * @throws Throwable 如果在执行action期间发生异常。
     */
    public Object doActionOnStack(String invokeString, ActionStack stack, Map<String, ?> params) throws Throwable {
        if (stack == null)
            throw new NullPointerException("参数stack不能为空。");
        //一、创建并获取堆栈对象。
        ActionStack newStack = this.parseInvokeString(invokeString, new ActionStack(stack, stack.getSession(), this));
        String actionName = newStack.getActionName();
        if (this.actionContext.containsAction(actionName) == false)
            throw new NoDefinitionException("找不到action[" + actionName + "]的定义。");
        //
        if (params != null)
            for (String key : params.keySet())
                newStack.setAttribute(key, params.get(key));
        //二、获取调用对象
        ActionInvoke invoke = this.actionContext.findAction(actionName, newStack.getActionMethod());
        //三、配置私有过滤器
        invoke = this.actionContext.configPrivateFilter(actionName, invoke);
        //四、执行调用
        return invoke.invoke(newStack);
    };
    /***/
    private ActionStack parseInvokeString(String invokeString, ActionStack stack) throws FormatException {
        String regex = "(.*)\\.(.*)";
        if (Pattern.matches(regex, invokeString) == false)
            throw new FormatException("invokeString 格式错误！");;
        //解析action调用表达式，并且获得各个部分参数
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(invokeString);
        m.find();
        String actionName = m.group(1);
        String actionMethod = m.group(2);
        //
        stack.setInvokeString(invokeString);
        stack.setActionName(actionName);
        stack.setActionMethod(actionMethod);
        return stack;
    }
    //==========================================================================================Att
    @Override
    public void clearAttribute() {
        this.contextAtt.clearAttribute();
    }
    @Override
    public boolean contains(String name) {
        return this.contextAtt.contains(name);
    }
    @Override
    public Object getAttribute(String name) {
        return this.contextAtt.getAttribute(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.contextAtt.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.contextAtt.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.contextAtt.setAttribute(name, value);
    }
}