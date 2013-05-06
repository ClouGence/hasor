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
package org.platform.event;
import org.platform.event.Event;
import org.platform.event.EventListener;
/**
 * 事件处理器，该接口的目的是为了处理{@link Event}类型事件。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EventManager {
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(String eventType, EventListener listener);
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(Event eventType, EventListener listener);
    /**删除某个监听器的注册。*/
    public void removeEventListener(EventListener listener);
    /**同步方式抛出事件。*/
    public void throwEvent(String eventType);
    /**同步方式抛出事件。*/
    public void throwEvent(Event eventType);
    /**异步方式抛出事件。*/
    public void asynThrowEvent(String eventType);
    /**异步方式抛出事件。*/
    public void asynThrowEvent(Event eventType);
}