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
package org.more.hypha.beans.assembler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.more.hypha.Event;
import org.more.hypha.EventListener;
import org.more.hypha.EventManager;
/**
 * 该类是{@link EventManager}接口的一个基本实现。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractEventManager implements EventManager {
    private LinkedList<Event>                                    eventQueue = new LinkedList<Event>();
    private HashMap<Class<? extends Event>, List<EventListener>> listener   = new HashMap<Class<? extends Event>, List<EventListener>>();
    //
    /**添加一种类型事件的事件监听器。*/
    public void addEventListener(Class<? extends Event> eventType, EventListener listener) {
        List<EventListener> listeners = this.listener.get(eventType);
        if (listeners == null) {
            listeners = new ArrayList<EventListener>();
            this.listener.put(eventType, listeners);
        }
        listeners.add(listener);
    }
    /**将事件压入到队列中等待执行。*/
    public void pushEvent(Event event) {
        this.eventQueue.add(event);
    }
    /**清空所有在队列中等待处理的事件。*/
    public synchronized void clearEvent() {
        this.eventQueue.clear();
    }
    /**清空队列中指定类型的事件。*/
    public synchronized void clearEvent(Class<? extends Event> eventType) {
        LinkedList<Event> eList = new LinkedList<Event>();
        for (Event e : this.eventQueue)
            if (eventType.isAssignableFrom(eventType) == true)
                eList.add(e);
        this.eventQueue.removeAll(eList);
    }
    /**弹出所有类型事件，在弹出过程中依次激活每个事件的事件处理器，如果一些事件没有相应的事件处理器那么这些事的处理将被忽略。*/
    public synchronized void popEvent() {
        for (Event e : eventQueue)
            this.doEvent(e);
        this.eventQueue.clear();
    }
    /**弹出某种特定类型的事件，在弹出过程中依次激活每个事件的事件处理器，如果这些事件没有相应的事件处理器那么这些事的处理将被忽略。*/
    public synchronized void popEvent(Class<? extends Event> eventType) {
        LinkedList<Event> eList = new LinkedList<Event>();
        for (Event e : this.eventQueue)
            if (eventType.isAssignableFrom(eventType) == true)
                eList.add(e);
        this.eventQueue.removeAll(eList);
        for (Event e : eList)
            this.doEvent(e);
    }
    /**绕过事件队列直接通知事件处理器处理这个事件。*/
    public void doEvent(Event event) {
        for (Class<?> eventType : this.listener.keySet())
            if (eventType.isAssignableFrom(event.getClass()) == true) {
                List<EventListener> listener = this.listener.get(eventType);
                for (EventListener el : listener)
                    el.onEvent(event);
            }
    }
}