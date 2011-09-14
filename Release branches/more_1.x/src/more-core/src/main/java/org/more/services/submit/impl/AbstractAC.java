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
package org.more.services.submit.impl;
import java.lang.reflect.Method;
import java.net.URI;
import org.more.core.error.InvokeException;
import org.more.core.error.LoadException;
import org.more.services.submit.ActionContext;
import org.more.services.submit.ActionInvoke;
import org.more.services.submit.ActionStack;
/**
 * 基础AC实现。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractAC implements ActionContext {
    //
    public ActionInvoke getAction(URI uri, Method actionPath) throws Throwable {
        Object obj = this.getBean(actionPath, uri.getQuery());
        if (obj == null)
            throw new LoadException("装载action对象异常。");
        return new DefaultActionInvoke(obj, actionPath.getName()); //先从默认包中获取actio路径。
    };
    /**创建类型所指定的对象。*/
    protected abstract Object getBean(Method actionPath, String queryInfo) throws Throwable;
};
class DefaultActionInvoke implements ActionInvoke {
    private Object target = null;
    private String method = null;
    public DefaultActionInvoke(Object target, String method) {
        this.target = target;
        this.method = method;
    }
    public Object invoke(ActionStack stack) throws Throwable {
        Class<?> type = this.target.getClass();
        Method[] m = type.getMethods();
        Method method = null;
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals(this.method) == false)
                continue; //名称不一致忽略
            if (m[i].getParameterTypes().length != 1)
                continue; //参数长度不一致忽略
            if (ActionStack.class.isAssignableFrom(m[i].getParameterTypes()[0]) == true) {
                method = m[i];//符合条件
                break;
            }
        }
        if (method == null)//如果找不到方法则引发异常
            throw new InvokeException("无法执行[" + this.method + "]，找不着匹配的方法。");
        return method.invoke(target, stack);
    }
}