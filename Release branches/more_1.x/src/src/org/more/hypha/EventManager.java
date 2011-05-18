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
package org.more.hypha;
import org.more.hypha.Event.Sequence;
/**
 * 事件处理器，该接口的目的是为了处理{@link Event}类型事件。该接口提供的是一种先进先出的队列的方式处理事件。
 * 事件并不是当压入队列时就得到处理，需要明显的调用弹出事件驱动事件处理。也可以通过doEvent方法明确处理这个事件。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EventManager {
    /**设置默认异常处理器，当执行监听器方法时监听器抛出了异常将有该对象进行处理。*/
    public void setDefaultEventExceptionHandler(EventExceptionHandler<Event> handler);
    /**获取默认的异常处理器，当执行监听器方法时监听器抛出了异常将有该对象进行处理。*/
    public EventExceptionHandler<Event> getDefaultEventExceptionHandler();
    /*------------------------------------------------------------------------------*/
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(Event eventType, EventListener<?> listener);
    /**清空所有在队列中等待处理的事件。*/
    public void clearEvent();
    /**清空队列中指定类型的事件。*/
    public void clearEvent(Event eventType);
    /**删除队列中的某一个待执行的事件。*/
    public boolean removeEvent(Sequence sequence);
    /*------------------------------------------------------------------------------*/
    /**
     * 将事件压入到队列中等待执行。
     * @param eventType 要压入的事件类型。
     * @param objects 该事件携带的参数信息
     * @return 返回该事件在队列中的位置。
     */
    public Sequence pushEvent(Event eventType, Object... objects);
    /**弹出所有类型事件，如果在执行事件期间发生异常将使用默认的异常处理器处理事件异常。*/
    public void popEvent();
    /**弹出某种特定类型的事件，如果在执行事件期间发生异常将使用默认的异常处理器处理事件异常。*/
    public void popEvent(Event eventType);
    /**弹出特定顺序位置的事件，如果在执行事件期间发生异常将使用默认的异常处理器处理事件异常。*/
    public void popEvent(Sequence sequence);
    /**绕过事件队列直接通知事件处理器处理这个事件，如果在执行事件期间发生异常将使用默认的异常处理器处理事件异常。*/
    public void doEvent(Event eventType, Object... objects);
    /*------------------------------------------------------------------------------*/
    /**
     * 将事件压入到队列中等待执行，并且使用指定的
     * @param eventType 要压入的事件类型。
     * @param handler 使用的异常处理器。
     * @param objects 该事件携带的参数信息
     * @return 返回该事件在队列中的位置。
     */
    public Sequence pushEvent(Event eventType, EventExceptionHandler<Event> handler, Object... objects);
    /**弹出所有类型事件，{@link EventExceptionHandler}类型参数指定了如果在执行事件期间发生异常的异常处理器。
     * 如果该参数指定为空则使用默认的异常处理器。*/
    public void popEvent(EventExceptionHandler<Event> handler);
    /**弹出某种特定类型的事件，{@link EventExceptionHandler}类型参数指定了如果在执行事件期间发生异常的异常处理器。
     * 如果该参数指定为空则使用默认的异常处理器。*/
    public void popEvent(Event eventType, EventExceptionHandler<Event> handler);
    /**弹出特定顺序位置的事件，{@link EventExceptionHandler}类型参数指定了如果在执行事件期间发生异常的异常处理器。
     * 如果该参数指定为空则使用默认的异常处理器。*/
    public void popEvent(Sequence sequence, EventExceptionHandler<Event> handler);
    /**绕过事件队列直接通知事件处理器处理这个事件，{@link EventExceptionHandler}类型
     * 参数指定了如果在执行事件期间发生异常的异常处理对象。如果该参数指定为空则使用默认的异常处理器。*/
    public void doEvent(Event eventType, EventExceptionHandler<Event> handler, Object... objects);
}