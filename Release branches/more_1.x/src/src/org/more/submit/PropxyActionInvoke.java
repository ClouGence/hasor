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
import org.more.NotFoundException;
/**
 * 该类负责提供目标对象方法的{@link ActionInvoke ActionInvoke接口}形式。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropxyActionInvoke implements ActionInvoke {
    //========================================================================================Field
    /** 被代理的目标对象。 */
    private Object target = null;
    /** 要调用的目标方法名。 */
    private String invoke = null;
    //==================================================================================Constructor
    public PropxyActionInvoke(Object target, String invoke) {
        this.target = target;
        this.invoke = invoke;
    };
    //==========================================================================================Job
    /**该方法会查找执行名称的方法，其方法参数必须是ActionStack或者其子类类型*/
    public Object invoke(ActionStack stack) throws Throwable {
        Class<?> type = this.target.getClass();
        Method[] m = type.getMethods();
        Method method = null;
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals(invoke) == false)
                continue; //名称不一致忽略
            if (m[i].getParameterTypes().length != 1)
                continue; //参数长度不一致忽略
            if (ActionStack.class.isAssignableFrom(m[i].getParameterTypes()[0]) == true) {
                method = m[i];//符合条件
                break;
            }
        }
        if (method == null)//如果找不到方法则引发异常
            throw new NotFoundException("无法在类[" + type + "]中找到Action方法" + this.invoke);
        return method.invoke(target, stack);
    };
};