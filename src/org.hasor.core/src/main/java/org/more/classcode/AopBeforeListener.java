/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode;
/**
* Aop的before切面，当收到before切面的事件通知时会自动调用该接口。该接口方法会在生成aop链的第一个环节发出调用。下面这张图中Before就是这个接口的工作点。
* <br/><img width="400" src="doc-files/classcode_struct.png"/>
* @version 2010-9-2
* @author 赵永春 (zyc@hasor.net)
*/
public interface AopBeforeListener {
    /**
     * 用于接收before切面的事件的方法。
     * @param target 被调用的对象。
     * @param method 被调用的方法。
     * @param args 调用这个方法传递的参数。
     */
    public void beforeInvoke(final Object target, final Method method, final Object[] args) throws Throwable;
}