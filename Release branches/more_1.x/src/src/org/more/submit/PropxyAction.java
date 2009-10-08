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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.more.InvokeException;
/**
 * 该类是代理Action类的代理类。该类的功能是增加一个可以通过name来决定调用哪个方法的处理对象。
 * 注意PropxyAction类不能作为最终目标类。否则当使用getFinalTarget方法时将会返回最终目标
 * PropxyAction类的target。而不是最终目标PropxyAction类。这个特性持续PropxyAction的子类。
 * Date : 2009-6-26
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class PropxyAction {
    /** 当前代理Action的名称 */
    private String       name              = null;
    /** 被代理的目标action对象 */
    protected Object     target            = null;
    /** 要被调用的目标对象方法参数签名类型列表 */
    private Class[]      invokeParamsTypes = new Class[] { ActionMethodEvent.class };
    public static String DefaultMethodName = "execute";
    /**
     * 该方法的作用是反射的形式调用目标action的方法。
     * @param methodName 要调用的反射方法名
     * @param event 传入的方法参数
     * @return 返回调用目标对象时的返回值
     * @throws InvokeException 在方法调用期间发生异常。
     */
    public Object execute(String methodName, ActionMethodEvent event) throws InvokeException {
        String method = methodName;
        Method defaultMethod = null;//默认方法
        Method invokeMethod = null;//准备调用的方法
        //如果名称不合法则直接将名称更改为默认方法的名称
        if (methodName == null || methodName.equals("") || methodName.equals("execute") == true)
            method = PropxyAction.DefaultMethodName;
        //反射调用方法
        Method[] ms = this.getFinalTarget().getClass().getMethods();
        for (Method m : ms) {
            //1.目标方法参数列表个数与types字段中存放的个数不一样的忽略。
            Class[] paramTypes = m.getParameterTypes();
            if (paramTypes.length != this.invokeParamsTypes.length)
                continue;
            //2.如果有参数类型不一样的也忽略---1
            boolean isFind = true;
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i].isAssignableFrom(this.invokeParamsTypes[i]) == false) {
                    isFind = false;
                    break;
                }
            }
            //---2.如果有参数类型不一样的也忽略---2
            if (isFind == false)
                continue;
            //3.如果这个方法是默认方法则定义为默认方法。
            if (m.getName().equals(PropxyAction.DefaultMethodName) == true)
                defaultMethod = m;
            //4.名字不相等的忽略
            if (m.getName().equals(method) == false)
                continue;
            //符合条件执行调用
            invokeMethod = m;
        }
        //调用默认方法
        if (invokeMethod == null && defaultMethod == null)
            //调用方法和默认方法都为空
            throw new InvokeException("无法调用目标方法[" + method + "]并且默认方法[" + PropxyAction.DefaultMethodName + "]也无法调用！");
        else if (invokeMethod == null)
            //调用方法为空
            return this.invoke(defaultMethod, event);
        else
            //正常调用
            return this.invoke(invokeMethod, event);
    }
    private Object invoke(Method m, ActionMethodEvent event) throws InvokeException {
        try {
            event.setInvokeMethod(m.getName());//实际调用的方法名。
            return m.invoke(this.target, event);
        } catch (Exception e) {
            Throwable ee = (e instanceof InvocationTargetException) ? e.getCause() : e;
            if (ee instanceof InvokeException)
                throw (InvokeException) ee;
            else
                throw new InvokeException(ee);
        }
    }
    /**
     * 获得被代理的目标对象。 
     * @return 返回被代理的目标Action对象。
     */
    public Object getTarget() {
        return target;
    }
    /**
     * 获得被代理的最终目标Action对象。 
     * @return 返回被代理的最终目标Action对象。
     */
    public Object getFinalTarget() {
        Object obj = this.getTarget();
        while (true)
            if (obj == null)
                return null;
            else if (obj instanceof PropxyAction)
                obj = ((PropxyAction) obj).getTarget();
            else
                return obj;
    }
    /**
     * 获得Action名。
     * @return 返回Action名。
     */
    public String getName() {
        return name;
    }
    void setTarget(Object target) {
        this.target = target;
    }
    void setName(String name) {
        this.name = name;
    }
}
