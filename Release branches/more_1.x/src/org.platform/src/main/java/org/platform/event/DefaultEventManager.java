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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.more.util.ArrayUtil;
import org.more.util.StringUtil;
import org.platform.Assert;
import org.platform.context.AppContext;
/**
 * {@link EventManager}接口的默认实现。
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
class DefaultEventManager implements EventManager, ManagerLife {
    private ExecutorService              executorService  = null;
    private Map<String, EventListener[]> eventListenerMap = new HashMap<String, EventListener[]>();
    //
    @Override
    public synchronized void addEventListener(String eventType, EventListener listener) {
        Assert.isNotNull(listener, "add EventListener object is null.");
        EventListener[] eventListener = this.eventListenerMap.get(eventType);
        if (eventListener == null) {
            eventListener = new EventListener[0];
        }
        eventListener = ArrayUtil.addToArray(eventListener, listener);
        this.eventListenerMap.put(eventType, eventListener);
    }
    @Override
    public synchronized void removeAllEventListener(String eventType) {
        this.eventListenerMap.remove(eventType);
    }
    @Override
    public synchronized void removeEventListener(String eventType, EventListener listener) {
        Assert.isNotNull(eventType, "remove eventType is null.");
        Assert.isNotNull(listener, "remove EventListener object is null.");
        EventListener[] eventListener = this.eventListenerMap.get(eventType);
        if (ArrayUtil.isBlank(eventListener))
            return;
        eventListener = ArrayUtil.removeInArray(eventListener, listener);
        this.eventListenerMap.put(eventType, eventListener);
    }
    @Override
    public EventListener[] getEventListener(String eventType) {
        EventListener[] eventListener = this.eventListenerMap.get(eventType);
        return (eventListener == null) ? new EventListener[0] : eventListener;
    }
    @Override
    public String[] getEventTypes() {
        Set<String> eventTypes = this.eventListenerMap.keySet();
        return eventTypes.toArray(new String[eventTypes.size()]);
    }
    @Override
    public void throwEvent(String eventType, Object... objects) {
        if (StringUtil.isBlank(eventType) == true)
            return;
        EventListener[] eventListener = this.eventListenerMap.get(eventType);
        if (eventListener != null) {
            for (EventListener event : eventListener)
                event.onEvent(eventType, objects);
        }
    }
    @Override
    public void asynThrowEvent(final String eventType, final Object... objects) {
        if (StringUtil.isBlank(eventType) == true)
            return;
        final EventListener[] eventListener = this.eventListenerMap.get(eventType);
        this.executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (eventListener != null) {
                    for (EventListener event : eventListener)
                        event.onEvent(eventType, objects);
                }
            }
        });
    }
    @Override
    public void initLife(AppContext appContext) {
        this.executorService = Executors.newCachedThreadPool();
    }
    @Override
    public void destroyLife(AppContext appContext) {
        this.executorService.shutdown();
    }
}