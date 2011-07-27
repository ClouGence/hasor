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
import java.lang.reflect.Method;
import java.util.Iterator;
/**
 * Action资源迭代器，该迭代器会扫描每一个action对象的方法。以迭代出所有可调用的action invokeString。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
class InvokeStringIterator implements Iterator<String> {
    private Iterator<String> actionNameIterator  = null;
    private ActionContext    actionContext       = null;
    //
    private String           currentActionName   = null;
    private Method[]         actionObjectMethods = null;
    private int              currentMethodIndex  = 0;
    /*----------------------------------------------*/
    public InvokeStringIterator(ActionContext actionContext) {
        this.actionContext = actionContext;
    };
    public boolean hasNext() {
        if (actionNameIterator.hasNext() == false && actionObjectMethods.length > currentMethodIndex)
            return false;
        else
            return true;
    };
    private Method readNextMethod_2() {
        if (this.actionObjectMethods == null || this.currentMethodIndex > actionObjectMethods.length)
            return null;
        this.currentMethodIndex++;
        return this.actionObjectMethods[this.currentMethodIndex];
    };
    private Method readNextMethod() {
        //1.尝试读取一次
        Method m = readNextMethod_2();
        if (m != null)
            return m;
        //2.迭代下一个Action
        if (this.actionNameIterator.hasNext() == true) {
            //如果存在则读取这个action的所有方法列表并且重置方法指针为-1。
            this.currentActionName = this.actionNameIterator.next();
            Class<?> type = this.actionContext.getActionType(this.currentActionName);
            this.actionObjectMethods = type.getMethods();
        } else {
            //如果已经迭代到最后一个action名称，则重置所有数据。
            this.currentActionName = null;
            this.actionObjectMethods = null;
        }
        this.currentMethodIndex = -1;
        //4.再次尝试
        return readNextMethod_2();
    };
    public String next() {
        while (true) {
            Method m = this.readNextMethod();
            if (m == null)
                return null;
            if (m.getParameterTypes().length != 1)
                continue;
            if (ActionStack.class.isAssignableFrom(m.getParameterTypes()[0]) == false)
                continue;
            return this.currentActionName + m.getName();
        }
    };
    public void remove() {
        throw new UnsupportedOperationException("ActionInvokeStringIterator不支持该操作。");
    };
};