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
package org.more.core.classcode;
/**
* Aop的throwing切面，当收到throwing切面的事件通知时会自动调用该接口。该接口方法会在生成aop链的第一个环节发出调用。下面这张图中Throwing就是这个接口的工作点。
* 但是由于throwing是当filter_start调用完成之后在进行的。因此throwing的消息是在{@link AopInvokeFilter}过滤器链调用之后。其结果也受到过滤器链的影响。
* <br/><img width="400" src="doc-files/classcode_struct.png"/>
* @version 2010-9-2
* @author 赵永春 (zyc@byshell.org)
*/
public interface AopThrowingListener {
    /**
     * 用于接收returning切面的事件的方法。
     * @param target 被调用的对象。
     * @param method 被调用的方法。
     * @param args 调用这个方法传递的参数。
     * @param e 接受发生的异常
     */
    public void throwsException(Object target, Method method, Object[] args, Throwable e) throws Throwable;
}