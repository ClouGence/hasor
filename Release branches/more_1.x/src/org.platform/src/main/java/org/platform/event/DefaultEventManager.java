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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.platform.Platform;
import org.platform.event.Event.Sequence;
/**
* 该类管理事件分发的基类，该类是{@link EventManager}接口的实现类。
* @version 2010-10-10
* @author 赵永春 (zyc@byshell.org)
*/
public class DefaultEventManager implements EventManager {
    private EventExceptionHandler<Event>       defaultEventExceptionHandler = null;
    //
    private Map<Event, List<EventListener<?>>> listener                     = new HashMap<Event, List<EventListener<?>>>();
    private List<Sequence>                     eventQueue                   = new LinkedList<Sequence>();
    private Map<Sequence, Event>               eventQueueMap                = new HashMap<Sequence, Event>();
    /*------------------------------------------------------------------------------*/
    public void setDefaultEventExceptionHandler(EventExceptionHandler<Event> handler) {
        Platform.info("change default EventExceptionHandler, new handler = %s", handler);
        this.defaultEventExceptionHandler = handler;
    }
    public EventExceptionHandler<Event> getDefaultEventExceptionHandler() {
        return this.defaultEventExceptionHandler;
    }
    /*------------------------------------------------------------------------------*/
    public void addEventListener(Event eventType, EventListener<?> listener) {
        if (eventType == null || listener == null) {
            Platform.warning("add event listener error , eventType or listener is null.");
            return;
        }
        List<EventListener<?>> listeners = this.listener.get(eventType);
        if (listeners == null) {
            listeners = new ArrayList<EventListener<?>>();
            this.listener.put(eventType, listeners);
        }
        listeners.add(listener);
    }
    public synchronized void clearEvent() {
        this.eventQueue.clear();
        this.eventQueueMap.clear();
        Platform.debug("clear all event.");
    }
    public synchronized void clearEvent(Event eventType) {
        if (eventType == null) {
            Platform.warning("clearEvent an error, eventType is null.");
            return;
        }
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType() == eventType)
                als.add(si);
        for (Sequence si : als)
            this.removeEvent(si);
        Platform.debug("clear %1 event, count = %2", eventType, als.size());
    }
    public synchronized boolean removeEvent(Sequence sequence) {
        if (sequence == null) {
            Platform.warning("removeEvent an error, sequence is null.");
            return false;
        }
        this.eventQueueMap.remove(sequence);
        boolean r2 = this.eventQueue.remove(sequence);
        Platform.debug("remove event, type = %s, index = %s", sequence.getEventType(), sequence.getIndex());
        return r2;
    }
    /*------------------------------------------------------------------------------*/
    public Sequence pushEvent(Event eventType, Object... objects) {
        if (eventType == null) {
            Platform.warning("pushEvent an error, eventType is null.");
            return null;
        }
        Sequence impl = new SequenceImpl(this.eventQueue, eventType, this.defaultEventExceptionHandler, objects);
        this.eventQueue.add(impl);
        this.eventQueueMap.put(impl, eventType);
        Platform.debug("pushEvent %s event ,index = %s ,params = %s", eventType, impl.getIndex(), objects);
        return impl;
    }
    public synchronized void popEvent() {
        Platform.debug("popEvent all event...");
        for (Sequence si : this.eventQueue)
            this.exeSequence(si.getEventType(), si, si.getHandler());
        this.clearEvent();
    }
    public synchronized void popEvent(Event eventType) {
        if (eventType == null) {
            Platform.warning("popEvent an error, eventType is null.");
            return;
        }
        Platform.debug("popEvent %s event...", eventType);
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType() == eventType) {
                this.exeSequence(eventType, si, si.getHandler());//run
                als.add(si);
            }
        for (Sequence si : als)
            this.removeEvent(si);//delete
    }
    public synchronized void popEvent(Sequence sequence) {
        if (sequence == null) {
            Platform.warning("popEvent an error, sequence is null.");
            return;
        }
        if (this.removeEvent(sequence) == true) {
            Platform.debug("popEvent Sequence, event = %s, index = %s", sequence.getEventType(), sequence.getIndex());
            this.exeSequence(sequence.getEventType(), sequence, sequence.getHandler());
        } else
            Platform.debug("popEvent Sequence is not found, event = %s, index = %s", sequence.getEventType(), sequence.getIndex());
    }
    public void doEvent(Event eventType, Object... objects) {
        if (eventType == null) {
            Platform.warning("doEvent an error, eventType is null.");
            return;
        }
        Platform.debug("doEvent %s event, params = %s", eventType, objects);
        Sequence sequence = new SequenceImpl(this.eventQueue, eventType, null, objects);
        this.exeSequence(eventType, sequence, this.defaultEventExceptionHandler);
    }
    /*------------------------------------------------------------------------------*/
    public Sequence pushEvent(Event eventType, EventExceptionHandler<Event> handler, Object... objects) {
        if (eventType == null) {
            Platform.warning("pushEvent an error, eventType is null.");
            return null;
        }
        Sequence impl = null;
        if (handler != null)
            impl = new SequenceImpl(this.eventQueue, eventType, handler, objects);
        else
            impl = new SequenceImpl(this.eventQueue, eventType, this.defaultEventExceptionHandler, objects);
        this.eventQueue.add(impl);
        this.eventQueueMap.put(impl, eventType);
        Platform.debug("pushEvent %s event ,index = %s ,params = %s", eventType, impl.getIndex(), objects);
        return impl;
    }
    public synchronized void popEvent(EventExceptionHandler<Event> handler) {
        Platform.debug("popEvent all event ... , handler = %s", handler);
        for (Sequence si : this.eventQueue) {
            if (handler == null)
                handler = si.getHandler();
            this.exeSequence(si.getEventType(), si, handler);
        }
        this.clearEvent();
    }
    public synchronized void popEvent(Event eventType, EventExceptionHandler<Event> handler) {
        if (eventType == null) {
            Platform.warning("popEvent an error, eventType is null.");
            return;
        }
        Platform.debug("popEvent %s event... , handler = %s", eventType, handler);
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType() == eventType) {
                if (handler == null)
                    handler = si.getHandler();
                this.exeSequence(eventType, si, handler);//run
                als.add(si);
            }
        for (Sequence si : als)
            this.removeEvent(si);//delete
    }
    public synchronized void popEvent(Sequence sequence, EventExceptionHandler<Event> handler) {
        if (sequence == null) {
            Platform.warning("popEvent an error, sequence is null.");
            return;
        }
        if (this.removeEvent(sequence) == true) {
            Platform.debug("popEvent Sequence, event = %s, index = %s , handler = %s", sequence.getEventType(), sequence.getIndex(), handler);
            if (handler == null)
                handler = sequence.getHandler();
            this.exeSequence(sequence.getEventType(), sequence, handler);
        } else
            Platform.debug("popEvent Sequence is not found, event = %s, index = %s , handler = %s", sequence.getEventType(), sequence.getIndex(), handler);
    }
    public void doEvent(Event eventType, EventExceptionHandler<Event> handler, Object... objects) {
        if (eventType == null) {
            Platform.warning("doEvent an error, eventType is null.");
            return;
        }
        Platform.debug("doEvent %s event, handler = %s , params = %s", eventType, handler, objects);
        Sequence sequence = new SequenceImpl(this.eventQueue, eventType, handler, objects);
        this.exeSequence(eventType, sequence, handler);
    };
    /*------------------------------------------------------------------------------*/
    /**执行*/
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void exeSequence(Event eventType, Sequence sequence, EventExceptionHandler<Event> handler) {
        //begin
        for (Event e : this.listener.keySet())
            if (e == eventType) {
                List<EventListener<?>> listeners = this.listener.get(eventType);
                Platform.debug("run exeSequence, find listeners %s", listeners);
                for (EventListener listener : listeners)
                    try {
                        listener.onEvent(eventType, sequence);
                        Platform.debug("listener %s run , eventType= %s, sequence = %s", listener, eventType, sequence);
                    } catch (Throwable e1) {
                        if (handler == null)
                            handler = sequence.getHandler();
                        if (handler == null)
                            handler = this.defaultEventExceptionHandler;
                        if (handler != null) {
                            Platform.info("exeSequence listener %s, error , use handler = %s , eventType= %s, sequence = %s", listener, handler, eventType, sequence);
                            handler.processException(e1, sequence, listener);
                        } else
                            throw new EventException(e1);
                    }
            }
        //end
    };
}
/***/
class SequenceImpl extends Sequence {
    private List<Sequence>               eventQueue = null;
    private Event                        eventType  = null;
    private Object[]                     objects    = null;
    private EventExceptionHandler<Event> handler    = null;
    //
    SequenceImpl(List<Sequence> eventQueue, Event eventType, EventExceptionHandler<Event> handler, Object[] objects) {
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
    public EventExceptionHandler<Event> getHandler() {
        return this.handler;
    }
}