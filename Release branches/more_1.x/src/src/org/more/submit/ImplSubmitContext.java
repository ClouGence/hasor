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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.more.FormatException;
import org.more.NoDefinitionException;
import org.more.util.attribute.AttBase;
/**
 * SubmitContext接口的实现类。ImplSubmitContext的createActionStack方法可以改变Action参数的具体类型。
 * <br/>Date : 2009-12-1
 * @author 赵永春
 */
public class ImplSubmitContext extends AttBase implements SubmitContext {
    //========================================================================================Field
    private static final long serialVersionUID = 3966376070855006285L;
    private ActionContext     actionContext    = null;
    //==================================================================================Constructor
    /**创建一个Submit3.0运行环境。*/
    public ImplSubmitContext(ActionContext actionContext) {
        if (actionContext == null)
            throw new NullPointerException("ActionContext类型参数不能为空。");
        this.actionContext = actionContext;
    }
    //==========================================================================================Job
    /** 获取已经定义的Action名集合。*/
    public String[] getActionNames() {
        return actionContext.getActionNames();
    };
    /** 获取一个指定的Action类型，参数为action名。*/
    public Class<?> getActionType(String actionName) {
        return actionContext.getActionType(actionName);
    };
    /**该方法负责创建并且初始化ActionStack对象，SubmitContext的子类可以通过从写该方法来扩展ActionStack的功能。*/
    protected ActionStack createActionStack(ActionStack parent, Session session, ImplSubmitContext context) {
        ActionStack as = new ActionStack(parent, session, context);
        as.init();
        return as;
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
    public Object doAction(String invokeString, Session session, Map<String, ?> params) throws Throwable {
        //一、创建并获取堆栈对象。
        ActionStack stack = this.parseInvokeString(invokeString, this.createActionStack(null, session, this));
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
        Object res = invoke.invoke(stack);
        return this.shellCallBack(stack, res);
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
        ActionStack newStack = this.parseInvokeString(invokeString, this.createActionStack(stack, stack.getSession(), this));
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
        Object res = invoke.invoke(newStack);
        return this.shellCallBack(newStack, res);
    };
    /**解析调用字符串并且将解析之后的数据注入到ActionStack参数中，最后返回。*/
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
    /** 指定调用Action返回之后的脚本处理请求。 */
    private Object shellCallBack(ActionStack stack, Object results) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        String scriptIn = "/META-INF/submit_scripts/" + stack.getResultsScript() + ".js";
        InputStream in = ImplSubmitContext.class.getResourceAsStream(scriptIn);
        if (stack.getResultsScript() == null)
            return results;
        if (in == null)
            throw new ScriptException("找不到脚本资源[" + scriptIn + "]");
        // 
        engine.eval(new InputStreamReader(in));
        Invocable inv = (Invocable) engine;
        return inv.invokeFunction("callBack", stack, results, stack.getResultsScriptParams());
    }
}