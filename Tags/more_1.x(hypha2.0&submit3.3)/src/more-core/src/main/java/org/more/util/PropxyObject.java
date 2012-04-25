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
package org.more.util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import org.more.core.error.InvokeException;
/**
 * 创建代理调用对象。通过该类提供的方法可以使调用者无视java的反射机制。直接向对象的参数栈输出参数，
 * 然后直接调用指定方法完成调用。最终在通过getResult方法返回返回值。该类是一个工具类
 * @version 2009-7-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropxyObject {
    /** 存放准备调用方法时传递的参数列表数据，有顺序 */
    private ArrayList<Object> list       = new ArrayList<Object>();
    /** 被代理的目标对象。 */
    private Object            target     = null;
    private Class<?>          targetType = null;
    /** 方法调用的返回值 */
    private Object            result     = null;
    /**
     * 创建MRMI代理调用对象。通过该类提供的方法可以使调用者无视java的反射机智。
     * 直接向对象的参数栈输出参数，然后直接调用指定方法完成调用。最终在通过getResult方法返回返回值。
     */
    public PropxyObject(Object target) {
        this.target = target;
        this.targetType = target.getClass();
    }
    /**
     * 创建MRMI代理调用对象。通过该类提供的方法可以使调用者无视java的反射机智。
     * 直接向对象的参数栈输出参数，然后直接调用指定方法完成调用。最终在通过getResult方法返回返回值。
     */
    public PropxyObject(Class<?> target) {
        this.targetType = target;
    }
    /**
     * 该方法的作用是反射的形式调用目标的方法。
     * @param methodName 要调用的反射方法名。
     * @throws InvokeException 在方法调用期间发生异常。 
     */
    public Object invokeMethod(String methodName) throws InvokeException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method invokeMethod = null;
        //反射调用方法
        Method[] ms = this.targetType.getMethods();
        for (Method m : ms) {
            //1.名字不相等的忽略
            if (m.getName().equals(methodName) == false)
                continue;
            //2.目标方法参数列表个数与types字段中存放的个数不一样的忽略。
            Class<?>[] paramTypes = m.getParameterTypes();
            if (paramTypes.length != this.list.size())
                continue;
            //3.如果有参数类型不一样的也忽略---1
            boolean isFind = true;
            for (int i = 0; i < paramTypes.length; i++) {
                Object param_object = this.list.get(i);
                if (param_object == null)
                    continue;
                //
                if (paramTypes[i].isAssignableFrom(param_object.getClass()) == false) {
                    isFind = false;
                    break;
                }
            }
            //5.如果有参数类型不一样的也忽略---2
            if (isFind == false)
                continue;
            //符合条件执行调用
            invokeMethod = m;
        }
        //
        if (invokeMethod == null)
            //调用方法为空
            throw new InvokeException("无法调用目标方法[" + methodName + "]！");
        else {
            //正常调用
            if (this.target == null) {
                //判断是否为静态的
                int mm = invokeMethod.getModifiers();
                if ((mm | Modifier.STATIC) != mm)
                    throw new InvokeException("目标方法[" + invokeMethod + "]，不是静态方法不能调用。");
                this.result = invokeMethod.invoke(null, this.list.toArray());
            } else
                this.result = invokeMethod.invoke(this.target, this.list.toArray());
        }
        return this.result;
    }
    public Object getResult() {
        return this.result;
    }
    public void put(Object value) {
        list.add(value);
    }
    public void clear() {
        list.clear();
    }
    public Object getTarget() {
        return target;
    }
}