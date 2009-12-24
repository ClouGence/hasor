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
/**
 * 该类负责提供目标对象方法的ActionInvoke接口形式。
 * <br/>Date : 2009-12-1
 * @author 赵永春
 */
class PropxyActionInvoke implements ActionInvoke {
    //========================================================================================Field
    /** 被代理的目标对象。 */
    private Object target = null;
    /** 要调用的目标方法名。 */
    private String invoke = null;
    //==================================================================================Constructor
    public PropxyActionInvoke(Object target, String invoke) {
        this.target = target;
        this.invoke = invoke;
    }
    //==========================================================================================Job
    @Override
    public Object invoke(ActionStack stack) throws Throwable {
        Method method = this.target.getClass().getMethod(this.invoke, ActionStack.class);
        return method.invoke(target, stack);
    }
}