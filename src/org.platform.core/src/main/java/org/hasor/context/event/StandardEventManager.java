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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hasor.HasorFramework;
import org.hasor.context.AppEventListener;
import org.hasor.context.EventManager;
import org.hasor.context.Settings;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public class StandardEventManager implements EventManager {
    private ExecutorService                 executorService  = null;
    private Map<String, AppEventListener[]> eventListenerMap = new HashMap<String, AppEventListener[]>();
    //
    public StandardEventManager(Settings settings) {
        int eventThreadPoolSize = settings.getInteger(HasorFramework.Platform_EventThreadPoolSize, 20);
        this.executorService = Executors.newScheduledThreadPool(eventThreadPoolSize);
    }
    @Override
    public synchronized void addEventListener(String eventType, AppEventListener eventListener) {
        HasorFramework.assertIsNotNull(eventListener, "add EventListener object is null.");
        AppEventListener[] eventListenerArray = this.eventListenerMap.get(eventType);
        if (eventListenerArray == null) {
            eventListenerArray = new AppEventListener[0];
        }
        eventListenerArray = ArrayUtils.addToArray(eventListenerArray, eventListener);
        this.eventListenerMap.put(eventType, eventListenerArray);
    }
    @Override
    public synchronized void removeAllEventListener(String eventType) {
        this.eventListenerMap.remove(eventType);
    }
    @Override
    public synchronized void removeEventListener(String eventType, AppEventListener eventListener) {
        HasorFramework.assertIsNotNull(eventType, "remove eventType is null.");
        HasorFramework.assertIsNotNull(eventListener, "remove EventListener object is null.");
        AppEventListener[] eventListenerArray = this.eventListenerMap.get(eventType);
        if (ArrayUtils.isBlank(eventListenerArray))
            return;
        eventListenerArray = ArrayUtils.removeInArray(eventListenerArray, eventListener);
        this.eventListenerMap.put(eventType, eventListenerArray);
    }
    @Override
    public AppEventListener[] getEventListener(String eventType) {
        AppEventListener[] eventListener = this.eventListenerMap.get(eventType);
        return (eventListener == null) ? new AppEventListener[0] : eventListener;
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
        AppEventListener[] eventListener = this.eventListenerMap.get(eventType);
        if (eventListener != null) {
            for (AppEventListener event : eventListener)
                event.onEvent(eventType, objects);
        }
    }
    @Override
    public void doAsynEvent(final String eventType, final Object... objects) {
        if (StringUtils.isBlank(eventType) == true)
            return;
        final AppEventListener[] eventListener = this.eventListenerMap.get(eventType);
        this.executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (eventListener != null) {
                    for (AppEventListener event : eventListener)
                        event.onEvent(eventType, objects);
                }
            }
        });
    }
}