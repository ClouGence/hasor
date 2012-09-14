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
package org.more.webui.event;
import org.more.core.error.InitializationException;
/**
 * 事件是一种通知机制，使用事件不能控制主控流程的执行。不过却可以通过事件得知内部的工作状态。
 * 该接口表示的是一个{@link EventManager}可以被识别处理的事件。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class Event {
    private String eventType = null;
    public Event(String eventType) {
        this.eventType = eventType;
    }
    public String getEventType() {
        return eventType;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return this.toString().equals(obj.toString());
    }
    @Override
    public String toString() {
        return "Event " + eventType;
    }
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    //----------------------------------------
    /**获取指定类型事件对象，如果参数为空则直接返回空值。事件对象在hypha中是全程唯一的，这样做的目的是为了减少new的数量。*/
    public static Event getEvent(String eventType) throws InitializationException {
        if (eventType == null)
            return null;
        return new Event(eventType);
    }
};