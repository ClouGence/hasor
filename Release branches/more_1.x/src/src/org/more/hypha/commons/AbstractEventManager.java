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
package org.more.hypha.commons;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.EventManager;
import org.more.util.attribute.IAttribute;
/**
 * 该类管理事件分发的基类，该类是{@link EventManager}接口的实现类。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class AbstractEventManager implements EventManager {
    private DefineResource                         defineResource = null;
    private IAttribute                             flash          = null;
    private HashMap<Event, List<EventListener<?>>> listener       = new HashMap<Event, List<EventListener<?>>>();
    //
    private LinkedList<Sequence>                   eventQueue     = new LinkedList<Sequence>();
    private HashMap<Sequence, Event>               eventQueueMap  = new HashMap<Sequence, Event>();
    /***/
    public AbstractEventManager(DefineResource defineResource) {
        this.defineResource = defineResource;
    }
    public void init(IAttribute flash) {
        this.flash = flash;
    }
    /**获取{@link DefineResource}。*/
    protected DefineResource getDefineResource() {
        return this.defineResource;
    }
    /**获取FLASH。*/
    protected IAttribute getFlash() {
        return this.flash;
    }
    public synchronized void addEventListener(Event eventType, EventListener<?> listener) {
        List<EventListener<?>> listeners = this.listener.get(eventType);
        if (listeners == null) {
            listeners = new ArrayList<EventListener<?>>();
            this.listener.put(eventType, listeners);
        }
        listeners.add(listener);
    };
    public synchronized Sequence pushEvent(Event eventType, Object... objects) {
        Sequence impl = new SequenceImpl(this.eventQueue, eventType, objects);
        this.eventQueue.add(impl);
        this.eventQueueMap.put(impl, eventType);
        return impl;
    }
    public synchronized void clearEvent() {
        this.eventQueue.clear();
        this.eventQueueMap.clear();
    };
    public synchronized void clearEvent(Event eventType) {
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType() == eventType)
                als.add(si);
        for (Sequence si : als)
            this.removeEvent(si);
    }
    public synchronized boolean removeEvent(Sequence sequence) {
        this.eventQueueMap.remove(sequence);
        return this.eventQueue.remove(sequence);
    }
    public synchronized void popEvent() throws Throwable {
        for (Sequence si : this.eventQueue)
            this.doEvent(si.getEventType(), si.getParams());
        this.clearEvent();
    }
    public synchronized void popEvent(Event eventType) throws Throwable {
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType() == eventType)
                this.doEvent(eventType, si.getParams());
        for (Sequence si : als)
            this.removeEvent(si);
    }
    public synchronized void popEvent(Sequence sequence) throws Throwable {
        if (this.removeEvent(sequence) == true)
            this.doEvent(sequence.getEventType(), sequence.getParams());
    }
    public synchronized void doEvent(Event eventType, Object... objects) {
        for (Event e : this.listener.keySet())
            if (e == eventType) {
                List<EventListener<?>> listener = this.listener.get(eventType);
                for (EventListener el : listener)
                    el.onEvent(eventType, new SequenceImpl(this.eventQueue, eventType, objects));
            }
    }
}
/***/
class SequenceImpl extends Sequence {
    private LinkedList<Sequence> eventQueue = null;
    private Event                eventType  = null;
    private Object[]             objects    = null;
    //
    SequenceImpl(LinkedList<Sequence> eventQueue, Event eventType, Object[] objects) {
        this.eventQueue = eventQueue;
        this.eventType = eventType;
        this.objects = objects;
    }
    public int getIndex() {
        return this.eventQueue.indexOf(this);
    }
    public Event getEventType() {
        return this.eventType;
    }
    public Object[] getParams() {
        return this.objects;
    }
}