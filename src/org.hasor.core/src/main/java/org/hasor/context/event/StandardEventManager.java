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
package org.hasor.context.event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.hasor.Hasor;
import org.hasor.context.EventManager;
import org.hasor.context.HasorEventListener;
import org.hasor.context.Settings;
import org.more.util.StringUtils;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public class StandardEventManager implements EventManager {
    private ScheduledExecutorService              executorService     = null;
    private int                                   eventThreadPoolSize = 0;
    private Map<String, List<HasorEventListener>> eventListenerMap    = new HashMap<String, List<HasorEventListener>>();
    //
    public StandardEventManager(Settings settings) {
        this.eventThreadPoolSize = settings.getInteger("framework.eventThreadPoolSize", 20);
        this.executorService = Executors.newScheduledThreadPool(eventThreadPoolSize);
    }
    /**获取执行事件使用的ScheduledExecutorService接口对象。*/
    protected ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }
    @Override
    public synchronized void addEventListener(String eventType, HasorEventListener hasorEventListener) {
        Hasor.assertIsNotNull(hasorEventListener, "add EventListener object is null.");
        List<HasorEventListener> eventListenerList = this.eventListenerMap.get(eventType);
        if (eventListenerList == null) {
            eventListenerList = new ArrayList<HasorEventListener>();
            this.eventListenerMap.put(eventType, eventListenerList);
        }
        if (eventListenerList.contains(hasorEventListener) == false)
            eventListenerList.add(hasorEventListener);
    }
    @Override
    public synchronized void removeAllEventListener(String eventType) {
        this.eventListenerMap.remove(eventType);
    }
    @Override
    public synchronized void removeEventListener(String eventType, HasorEventListener hasorEventListener) {
        Hasor.assertIsNotNull(eventType, "remove eventType is null.");
        Hasor.assertIsNotNull(hasorEventListener, "remove EventListener object is null.");
        List<HasorEventListener> eventListenerList = this.eventListenerMap.get(eventType);
        if (eventListenerList.isEmpty())
            return;
        eventListenerList.remove(hasorEventListener);
    }
    @Override
    public List<HasorEventListener> getEventListener(String eventType) {
        List<HasorEventListener> eventListenerList = this.eventListenerMap.get(eventType);
        return (eventListenerList == null) ? new ArrayList<HasorEventListener>(0) : Collections.unmodifiableList(eventListenerList);
    }
    @Override
    public String[] getEventTypes() {
        Set<String> eventTypes = this.eventListenerMap.keySet();
        return eventTypes.toArray(new String[eventTypes.size()]);
    }
    @Override
    public void doSyncEvent(String eventType, Object... objects) {
        if (StringUtils.isBlank(eventType) == true)
            return;
        List<HasorEventListener> eventListenerList = this.eventListenerMap.get(eventType);
        if (eventListenerList != null) {
            for (HasorEventListener event : eventListenerList)
                event.onEvent(eventType, objects);
        }
    }
    @Override
    public void doAsynEvent(final String eventType, final Object... objects) {
        if (StringUtils.isBlank(eventType) == true)
            return;
        final List<HasorEventListener> eventListenerList = this.eventListenerMap.get(eventType);
        this.executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (eventListenerList != null) {
                    for (HasorEventListener event : eventListenerList)
                        event.onEvent(eventType, objects);
                }
            }
        });
    }
    @Override
    public synchronized void clean() {
        this.executorService.shutdownNow();
        this.executorService = Executors.newScheduledThreadPool(this.eventThreadPoolSize);
    }
}