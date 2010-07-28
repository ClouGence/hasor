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
/**
 * 用于表示一个可以调用的action对象。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionInvoke {
    /**
     * 调用这个资源并且返回返回值，如果在调用期间发生异常则抛出Throwable异常。
     * @param stack 调用时传递的栈对象。
     * @return 返回调用资源之后产生的返回值。
     * @throws Throwable 如果产生异常。
     */
    public Object invoke(ActionStack stack) throws Throwable;
}