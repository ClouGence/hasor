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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.more.core.error.InvokeException;
/**
 * 创建代理调用对象。通过该类提供的方法可以使调用者无视java的反射机制。直接向对象的参数栈输出参数，
 * 然后直接调用指定方法完成调用。最终在通过getResult方法返回返回值。该类是一个工具类
 * @version 2009-7-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstructorPropxy {
    /** 存放准备调用方法时传递的参数列表数据，有顺序 */
    private ArrayList<Object> list       = new ArrayList<Object>();
    /** 被代理的目标对象。 */
    private Class<?>          targetType = null;
    /** 方法调用的返回值 */
    private Object            result     = null;
    /**
     * 创建MRMI代理调用对象。通过该类提供的方法可以使调用者无视java的反射机智。
     * 直接向对象的参数栈输出参数，然后直接调用指定方法完成调用。最终在通过getResult方法返回返回值。
     */
    public ConstructorPropxy(Class<?> target) {
        this.targetType = target;
    }
    /**
     * 该方法的作用是反射的形式调用目标的构造方法。
     * @throws InvokeException 在方法调用期间发生异常。  
     */
    public Object newInstance() throws InvokeException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> invokeConstructor = null;
        //反射调用方法
        Constructor<?>[] cs = this.targetType.getConstructors();
        for (Constructor<?> c : cs) {
            Class<?>[] cparams = c.getParameterTypes();
            if (cparams.length != list.size())
                continue;//参数长度不一致，排除
            //3.如果有参数类型不一样的也忽略---1
            boolean isFind = true;
            for (int i = 0; i < cparams.length; i++) {
                Object param_object = list.get(i);
                if (param_object == null)
                    continue;
                //
                if (this.isAssignableFrom(cparams[i], param_object) == false) {
                    try {
                        //尝试将目标数据转换为所需要的格式
                        Object obj = StringConvertUtil.changeType(param_object, cparams[i]);
                        list.set(i, obj);
                        continue;
                    } catch (Exception e) {}
                    isFind = false;
                    break;
                }
            }
            //5.如果有参数类型不一样的也忽略---2
            if (isFind == false)
                continue;
            //符合条件执行调用
            invokeConstructor = c;
        }
        //
        if (invokeConstructor == null)
            //调用方法为空
            throw new InvokeException("无法调用目标构造方法！");
        else
            //正常调用
            this.result = invokeConstructor.newInstance(this.list.toArray());
        return this.result;
    }
    private boolean isAssignableFrom(Class<?> type, Object obj) {
        if (type.isPrimitive() == false)
            return type.isAssignableFrom(obj.getClass());
        //
        Class<?> type2 = obj.getClass();
        if (type == Boolean.TYPE && type2 == Boolean.class)
            return true;
        if (type == Byte.TYPE && type2 == Byte.class)
            return true;
        if (type == Short.TYPE && type2 == Short.class)
            return true;
        if (type == Integer.TYPE && type2 == Integer.class)
            return true;
        if (type == Long.TYPE && type2 == Long.class)
            return true;
        if (type == Float.TYPE && type2 == Float.class)
            return true;
        if (type == Double.TYPE && type2 == Double.class)
            return true;
        if (type == Character.TYPE && type2 == Character.class)
            return true;
        return false;
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
}