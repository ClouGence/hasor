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
package org.more.core.event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.more.core.event.Event.Sequence;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
/**
* 该类管理事件分发的基类，该类是{@link EventManager}接口的实现类。
* @version 2010-10-10
* @author 赵永春 (zyc@byshell.org)
*/
public class AbstractEventManager implements EventManager {
    private static final Log                log                          = LogFactory.getLog(AbstractEventManager.class);
    private EventExceptionHandler           defaultEventExceptionHandler = null;
    //
    private Map<Event, List<EventListener>> listener                     = new HashMap<Event, List<EventListener>>();
    private List<Sequence>                  eventQueue                   = new LinkedList<Sequence>();
    private Map<Sequence, Event>            eventQueueMap                = new HashMap<Sequence, Event>();
    /*------------------------------------------------------------------------------*/
    public void setDefaultEventExceptionHandler(EventExceptionHandler handler) {
        log.info("change default EventExceptionHandler, new handler = {%0}", handler);
        this.defaultEventExceptionHandler = handler;
    }
    public EventExceptionHandler getDefaultEventExceptionHandler() {
        return this.defaultEventExceptionHandler;
    }
    /*------------------------------------------------------------------------------*/
    public void addEventListener(Event eventType, EventListener listener) {
        if (eventType == null || listener == null) {
            log.warning("add event listener error , eventType or listener is null.");
            return;
        }
        List<EventListener> listeners = this.listener.get(eventType);
        if (listeners == null) {
            log.debug("this event is first append, event = {%0}, listener = {%1}", eventType, listener);
            listeners = new ArrayList<EventListener>();
            this.listener.put(eventType, listeners);
        }
        log.debug("add event listener, event = {%0}, listener = {%1}", eventType, listener);
        listeners.add(listener);
    }
    public synchronized void clearEvent() {
        this.eventQueue.clear();
        this.eventQueueMap.clear();
        log.debug("clear all event.");
    }
    public synchronized void clearEvent(Event eventType) {
        if (eventType == null) {
            log.error("clearEvent an error, eventType is null.");
            return;
        }
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType().equals(eventType) == true)
                als.add(si);
        for (Sequence si : als)
            this.removeEvent(si);
        log.debug("clear {%0} event, count = {%1}", eventType, als.size());
    }
    public synchronized boolean removeEvent(Sequence sequence) {
        if (sequence == null) {
            log.error("removeEvent an error, sequence is null.");
            return false;
        }
        this.eventQueueMap.remove(sequence);
        boolean r2 = this.eventQueue.remove(sequence);
        log.debug("remove event, type = {%0}, index = {%1}", sequence.getEventType(), sequence.getIndex());
        return r2;
    }
    /*------------------------------------------------------------------------------*/
    public Sequence pushEvent(Event eventType, Object... objects) {
        if (eventType == null) {
            log.error("pushEvent an error, eventType is null.");
            return null;
        }
        Sequence impl = new SequenceImpl(this.eventQueue, eventType, this.defaultEventExceptionHandler, objects);
        this.eventQueue.add(impl);
        this.eventQueueMap.put(impl, eventType);
        log.debug("pushEvent {%0} event ,index = {%1} ,params = {%2}", eventType, impl.getIndex(), objects);
        return impl;
    }
    public synchronized void popEvent() {
        log.debug("popEvent all event...");
        for (Sequence si : this.eventQueue)
            this.exeSequence(si.getEventType(), si, si.getHandler());
        this.clearEvent();
    }
    public synchronized void popEvent(Event eventType) {
        if (eventType == null) {
            log.error("popEvent an error, eventType is null.");
            return;
        }
        log.debug("popEvent {%0} event...", eventType);
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType().equals(eventType) == true) {
                this.exeSequence(eventType, si, si.getHandler());//run
                als.add(si);
            }
        for (Sequence si : als)
            this.removeEvent(si);//delete
    }
    public synchronized void popEvent(Sequence sequence) {
        if (sequence == null) {
            log.error("popEvent an error, sequence is null.");
            return;
        }
        if (this.removeEvent(sequence) == true) {
            log.debug("popEvent Sequence, event = {%0}, index = {%1}", sequence.getEventType(), sequence.getIndex());
            this.exeSequence(sequence.getEventType(), sequence, sequence.getHandler());
        } else
            log.debug("popEvent Sequence is not found, event = {%0}, index = {%1}", sequence.getEventType(), sequence.getIndex());
    }
    public void doEvent(Event eventType, Object... objects) {
        if (eventType == null) {
            log.error("doEvent an error, eventType is null.");
            return;
        }
        log.debug("doEvent {%0} event, params = {%1}", eventType, objects);
        Sequence sequence = new SequenceImpl(this.eventQueue, eventType, null, objects);
        this.exeSequence(eventType, sequence, this.defaultEventExceptionHandler);
    }
    /*------------------------------------------------------------------------------*/
    public Sequence pushEvent(Event eventType, EventExceptionHandler handler, Object... objects) {
        if (eventType == null) {
            log.error("pushEvent an error, eventType is null.");
            return null;
        }
        Sequence impl = null;
        if (handler != null)
            impl = new SequenceImpl(this.eventQueue, eventType, handler, objects);
        else
            impl = new SequenceImpl(this.eventQueue, eventType, this.defaultEventExceptionHandler, objects);
        this.eventQueue.add(impl);
        this.eventQueueMap.put(impl, eventType);
        log.debug("pushEvent {%0} event ,index = {%1} ,params = {%2}", eventType, impl.getIndex(), objects);
        return impl;
    }
    public synchronized void popEvent(EventExceptionHandler handler) {
        log.debug("popEvent all event ... , handler = {%0}", handler);
        for (Sequence si : this.eventQueue) {
            if (handler == null)
                handler = si.getHandler();
            this.exeSequence(si.getEventType(), si, handler);
        }
        this.clearEvent();
    }
    public synchronized void popEvent(Event eventType, EventExceptionHandler handler) {
        if (eventType == null) {
            log.error("popEvent an error, eventType is null.");
            return;
        }
        log.debug("popEvent {%0} event... , handler = {%1}", eventType, handler);
        ArrayList<Sequence> als = new ArrayList<Sequence>();
        for (Sequence si : this.eventQueue)
            if (si.getEventType().equals(eventType) == true) {
                if (handler == null)
                    handler = si.getHandler();
                this.exeSequence(eventType, si, handler);//run
                als.add(si);
            }
        for (Sequence si : als)
            this.removeEvent(si);//delete
    }
    public synchronized void popEvent(Sequence sequence, EventExceptionHandler handler) {
        if (sequence == null) {
            log.error("popEvent an error, sequence is null.");
            return;
        }
        if (this.removeEvent(sequence) == true) {
            log.debug("popEvent Sequence, event = {%0}, index = {%1} , handler = {%2}", sequence.getEventType(), sequence.getIndex(), handler);
            if (handler == null)
                handler = sequence.getHandler();
            this.exeSequence(sequence.getEventType(), sequence, handler);
        } else
            log.debug("popEvent Sequence is not found, event = {%0}, index = {%1} , handler = {%2}", sequence.getEventType(), sequence.getIndex(), handler);
    }
    public void doEvent(Event eventType, EventExceptionHandler handler, Object... objects) {
        if (eventType == null) {
            log.error("doEvent an error, eventType is null.");
            return;
        }
        log.debug("doEvent {%0} event, handler = {%1} , params = {%2}", eventType, handler, objects);
        Sequence sequence = new SequenceImpl(this.eventQueue, eventType, handler, objects);
        this.exeSequence(eventType, sequence, handler);
    };
    /*------------------------------------------------------------------------------*/
    /**执行*/
    private void exeSequence(Event eventType, Sequence sequence, EventExceptionHandler handler) {
        //begin
        for (Event e : this.listener.keySet())
            if (e.equals(eventType) == true) {
                List<EventListener> listeners = this.listener.get(eventType);
                log.debug("run exeSequence, find listeners {%0}", listeners);
                for (EventListener listener : listeners)
                    try {
                        listener.onEvent(eventType, sequence);
                        log.debug("listener {%0} run , eventType= {%1}, sequence = {%2}", listener, eventType, sequence);
                    } catch (Throwable e1) {
                        if (handler == null)
                            handler = sequence.getHandler();
                        if (handler == null)
                            handler = this.defaultEventExceptionHandler;
                        if (handler != null) {
                            log.info("exeSequence listener {%0}, error , use handler = {%1} , eventType= {%2}, sequence = {%3}", listener, handler, eventType, sequence);
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
    private List<Sequence>        eventQueue = null;
    private Event                 eventType  = null;
    private Object[]              objects    = null;
    private EventExceptionHandler handler    = null;
    //
    SequenceImpl(List<Sequence> eventQueue, Event eventType, EventExceptionHandler handler, Object[] objects) {
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
    public EventExceptionHandler getHandler() {
        return this.handler;
    }
}