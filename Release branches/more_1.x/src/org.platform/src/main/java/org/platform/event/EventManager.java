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
/**
 * 事件处理器，该接口的目的是为了处理事件。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EventManager {
    /**EventManager服务启动。*/
    public static final String EventManager_Start_Event   = "EventManager_Start_Event";
    /**EventManager服务销毁*/
    public static final String EventManager_Destroy_Event = "EventManager_Destroy_Event";
    //
    //
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(String eventType, EventListener listener);
    /**删除某个监听器的注册。*/
    public void removeAllEventListener(String eventType);
    /**删除某个监听器的注册。*/
    public void removeEventListener(String eventType, EventListener listener);
    /**获取某种特定类型的事件监听器集合。*/
    public EventListener[] getEventListener(String eventType);
    /**获取所有事件监听器类型。*/
    public String[] getEventTypes();
    //
    /**同步方式抛出事件。*/
    public void throwEvent(String eventType, Object... objects);
    /**异步方式抛出事件。*/
    public void asynThrowEvent(String eventType, Object... objects);
}