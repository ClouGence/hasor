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
import org.more.InvokeException;
import org.more.core.copybean.CopyBeanUtil;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 该类负责解析action调用表达试，并且执行调用如果调用的目标方法拥有返回值时通过manager。
 * 调用之后doAction会将返回值返回。manager调用的action是已经被过滤器层层拦截之后的结果。
 * Date : 2009-6-25
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class ActionManager {
    /** 负责装配拦截器的action环境 */
    private ActionContext           context            = null;                         //
    private ThreadLocal<IAttribute> paramContext       = new ThreadLocal<IAttribute>(); //当前线程中的环境参数。
    private IAttribute              publicParamContext = new AttBase();                //全局环境参数
    /** 私有化 */
    ActionManager() {}
    /**
     * 解析action调用表达试并且执行action调用，如果被调用的action有返回值则返回处理之后的返回值。
     * doAction方法在执行的目标action方法时会装配配置文件指定的action拦截器。
     * @param execActionExp 调用action所使用的action调用表达试
     * @param params 调用action目标所给action传递的参数。
     * @throws InvokeException 如果在调用过程中发生的异常。
     */
    public Object doAction(String execActionExp, Map params) throws InvokeException {
        if (context == null)
            throw new InvokeException("没有设置ActionContext不能执行该方法。");
        //
        String regex = "(.*)\\.(.*)";
        if (Pattern.matches(regex, execActionExp) == false)
            return null;
        //解析action调用表达式，并且获得各个部分参数
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(execActionExp);
        m.find();
        String actionName = m.group(1);
        String methodName = m.group(2);
        //检测Action是否存在。
        if (this.context.containsAction(actionName) == false)
            throw new InvokeException("找不到或不存在名称为[" + actionName + "]的Action定义。");
        //获得指定Action并且使用filterManager对其进行过滤器拦截。
        PropxyAction action = this.context.findAction(actionName);
        //创建事件对象
        try {
            //****** 如果向event帮定参数就在这里进行 ******
            //拷贝环境参数
            ActionMethodEvent event = new ActionMethodEvent();
            event.setContext(this);
            event.setActionName(actionName);
            event.setMethod(methodName);//要调用的目标方法名。
            event.setInvokeString(execActionExp);
            event.setParam(params);
            if (this.paramContext.get() != null) {
                CopyBeanUtil copyUtil = CopyBeanUtil.newInstance();
                copyUtil.copy(this.publicParamContext, event, "ref");//拷贝全局环境参数
                copyUtil.copy(this.paramContext.get(), event, "ref");//拷贝线城环境参数
            }
            //调用目标方法
            Object obj = action.execute(methodName, event);
            return obj;
        } catch (InvokeException e) {
            throw e;
        }
    }
    /**
     * 解析action调用表达试并且执行action调用，如果被调用的action有返回值则返回处理之后的返回值。
     * doAction方法在执行的目标action方法时会装配配置文件指定的action拦截器。
     * @param execActionExp 调用action所使用的action调用表达试
     * @param params 调用action目标所给action传递的参数。
     * @throws InvokeException 如果在调用过程中发生的异常。
     */
    public Object doAction(String execActionExp, Object... params) throws InvokeException {
        Map map = new HashMap();
        for (int i = 0; i < params.length; i++)
            map.put(String.valueOf(i), params[i]);
        return this.doAction(execActionExp, map);
    }
    //=========================================================================================================
    /**
     * 设置一个参数到ActionManager环境中，该参数与当前线程帮定如果跨线程调用action被设置的参数可能会因为线程环境不同而受到影响。
     * @param name 要设置的参数名。
     * @param value 要设置的参数值。
     */
    public void addThreadContextParams(String name, Object value) {
        IAttribute att = this.paramContext.get();
        if (att == null) {
            att = new AttBase();
            this.paramContext.set(att);
        }
        if (value != null)
            att.setAttribute(name, value);
    }
    /**
     * 删除一个已经设置的ActionManager环境参数，如果要被删除的参数不存在则该方法将不会产生任何效果。
     * @param name 要删除的参数名。
     */
    public void removeThreadContextParams(String name) {
        IAttribute att = this.paramContext.get();
        if (att == null)
            return;
        att.removeAttribute(name);
    }
    /**
     * 测试ActionManager环境参数中是否存在某个属性。如果存在这个参数则返回true否则返回false。
     * @param name 要测试的环境参数名。
     * @return 如果存在这个参数则返回true否则返回false。
     */
    public boolean containsThreadContextParams(String name) {
        IAttribute att = this.paramContext.get();
        if (att == null)
            return false;
        return att.contains(name);
    }
    /** 清空ActionManager环境中所有注册的与当前线程有关的参数，这些参数是由addThreadContextParams方法注册的。 */
    public void clearThreadContextParams() {
        this.paramContext.remove();
    }
    //=========================================================================================================
    /**
     * 获得manager的action环境，获得该环境之后可以设置这个环境的父环境。或者一些其他高级操作。
     * @return 获得manager的action环境，获得该环境之后可以设置这个环境的父环境。或者一些其他高级操作。
     */
    public ActionContext getContext() {
        return context;
    }
    /**
     * 设置manager的action环境，manager通过该环境来取得action。
     * @param context 要设置的action环境。
     */
    public void setContext(ActionContext context) {
        this.context = context;
    }
}