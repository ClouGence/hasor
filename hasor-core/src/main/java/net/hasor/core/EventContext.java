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
package net.hasor.core;
import net.hasor.utils.future.FutureCallback;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
/**
 *
 * @version : 2014年5月22日
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EventContext {
    /**
     * pushPhaseEvent方法注册的时间监听器当收到一次事件之后会被自动删除。
     * @param eventType 事件类型
     * @param eventListener 事件监听器。
     */
    public <T> boolean pushListener(String eventType, EventListener<T> eventListener);

    /**
     * 添加一种类型事件的事件监听器。
     * @param eventType 事件类型
     * @param eventListener 事件监听器。
     */
    public <T> boolean addListener(String eventType, EventListener<T> eventListener);

    /**
     * 删除某个监听器的注册。
     * @param eventType 事件类型
     * @param eventListener 事件监听器。
     */
    public <T> boolean removeListener(String eventType, EventListener<T> eventListener);

    /**
     * 删除某个监听器的注册。
     * @param eventType 事件类型
     */
    public <T> boolean clearListener(String eventType);

    /**
     * 同步方式抛出事件。当方法返回时已经全部处理完成事件分发。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发抛出监听器异常。
     * @param eventType 事件类型
     * @param eventData 事件参数
     */
    public <T> void fireSyncEvent(String eventType, T eventData) throws Throwable;

    /**
     * 同步方式抛出事件。当方法返回时已经全部处理完成事件分发。不同于fireSyncEvent的是事件监听器的执行在一个全新的线程中完成。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发抛出监听器异常。
     * @param eventType 事件类型
     * @param eventData 事件参数
     */
    public <T> void fireSyncEventWithAlone(String eventType, T eventData) throws Throwable;

    /**
     * 异步方式抛出事件。fireAsyncEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。
     * @param eventType 事件类型
     * @param eventData 事件参数
     */
    public <T> void fireAsyncEvent(String eventType, T eventData);

    /**
     * 异步方式抛出事件。fireAsyncEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。
     * @param eventType 事件类型
     * @param eventData 事件参数
     * @param fireType 事件异常处理方式
     */
    public <T> void fireAsyncEvent(String eventType, T eventData, FireType fireType);

    /**
     * 异步方式抛出事件。fireAsyncEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发，并将程序执行权交给异常处理接口。
     * @param eventType 事件类型
     * @param eventData 事件参数
     * @param fireType 事件异常处理方式
     * @param callBack 回调方法
     */
    public <T> void fireAsyncEvent(String eventType, T eventData, FireType fireType, EventCallBackHook<T> callBack);

    /**
     * 异步方式执行任务。<p>
     * @param runnable 异步任务
     * @param callBack 回调方法
     */
    public <T> boolean asyncTask(Callable<T> runnable, FutureCallback<T> callBack);

    /**
     * 异步方式执行任务。<p>
     * @param runnable 异步任务
     * @param callBack 回调方法
     */
    public boolean asyncTask(Runnable runnable, FutureCallback<Void> callBack);

    /**
     * 异步方式执行任务。<p>
     * @param runnable 异步任务
     */
    public <T> Future<T> asyncTask(Callable<T> runnable);

    /**
     * 异步方式执行任务。<p>
     * @param runnable 异步任务
     */
    public Future<Void> asyncTask(Runnable runnable);
}