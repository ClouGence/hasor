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
 * 提供事件监听器的注册和同步事件异步事件的触发操作。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@hasor.net)
 */
public interface EventManager {
    /**pushPhaseEvent方法注册的时间监听器当收到一次事件之后会被自动删除。*/
    public void pushEventListener(String eventType, HasorEventListener hasorEventListener);
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(String eventType, HasorEventListener hasorEventListener);
    /**删除某个监听器的注册。*/
    public void removeEventListener(String eventType, HasorEventListener hasorEventListener);
    //
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。*/
    public void doSyncEventIgnoreThrow(String eventType, Object... objects);
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发抛出监听器异常。*/
    public void doSyncEvent(String eventType, Object... objects) throws Throwable;
    /**异步方式抛出事件。asynEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。*/
    public void doAsynEventIgnoreThrow(String eventType, Object... objects);
    /**异步方式抛出事件。asynEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发，并将程序执行权交给异常处理接口。*/
    public void doAsynEvent(String eventType, AsyncCallBackHook callBack, Object... objects);
    //
    /**清空未完成的事件等待执行队列*/
    public void clean();
}