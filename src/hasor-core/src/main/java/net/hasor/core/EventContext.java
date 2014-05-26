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
/**
 * 
 * @version : 2014年5月22日
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EventContext {
    /**容器事件，在所有模块初始化之后引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Initialized = "ContextEvent_Initialized";
    /**容器事件，在所有模块 start 阶段之后引发。
     * @see net.hasor.core.context.AbstractAppContext*/
    public static final String ContextEvent_Started     = "ContextEvent_Started";
    /**模块事件。当模块收到 start 调用信号之后引发。
     * @see net.hasor.core.module.ModuleProxy*/
    public static final String ModuleEvent_Started      = "ModuleEvent_Started";
    //
    /**pushPhaseEvent方法注册的时间监听器当收到一次事件之后会被自动删除。*/
    public void pushListener(String eventType, EventListener eventListener);
    /**添加一种类型事件的事件监听器。*/
    public void addListener(String eventType, EventListener eventListener);
    /**删除某个监听器的注册。*/
    public void removeListener(String eventType, EventListener eventListener);
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发抛出监听器异常。*/
    public void fireSyncEvent(String eventType, Object... objects);
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。*/
    public void fireSyncEvent(String eventType, EventCallBackHook callBack, Object... objects);
    /**异步方式抛出事件。fireAsyncEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时该方法会吞掉异常，继续分发事件。被吞掉的异常会以一条警告的方式出现。*/
    public void fireAsyncEvent(String eventType, Object... objects);
    /**异步方式抛出事件。fireAsyncEvent方法的调用不会决定何时开始执行事件，而这一切由事件管理器决定。<p>
     * 注意：当某个时间监听器抛出异常时将中断事件分发，并将程序执行权交给异常处理接口。*/
    public void fireAsyncEvent(String eventType, EventCallBackHook callBack, Object... objects);
}