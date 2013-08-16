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
package org.hasor.context;
/**
 * 异步事件回调接口。
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public interface AsyncCallBackHook {
    /**在执行事件监听器发生异常时调用该方法。*/
    public void handleException(String eventType, Object[] objects, Throwable e);
    /**当完成异步事件处理时回调。<p>
     * 注意：无论在异步事件分发过程中{@link #handleException(String, Object[], Throwable)}方法是否被调用，该方法都会如期的被执行。*/
    public void handleComplete(String eventType, Object[] objects);
}