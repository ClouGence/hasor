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
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.hasor.context.AdvancedEventManager;
import org.hasor.context.HasorEventListener;
import org.hasor.context.Settings;
import org.more.RepeateException;
import org.more.util.StringUtils;
/**
 * 阶段事件管理器的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public class StandardAdvancedEventManager extends StandardEventManager implements AdvancedEventManager {
    private Map<String, LinkedList<HasorEventListener>> listenerMap = new HashMap<String, LinkedList<HasorEventListener>>();
    private Map<String, ScheduledFuture<?>>             timerMap    = new HashMap<String, ScheduledFuture<?>>();
    //
    public StandardAdvancedEventManager(Settings settings) {
        super(settings);
    }
    @Override
    public synchronized void pushEventListener(String eventType, HasorEventListener hasorEventListener) {
        if (StringUtils.isBlank(eventType) || hasorEventListener == null)
            return;
        LinkedList<HasorEventListener> eventList = this.listenerMap.get(eventType);
        if (eventList == null) {
            eventList = new LinkedList<HasorEventListener>();
            this.listenerMap.put(eventType, eventList);
        }
        if (eventList.contains(hasorEventListener) == false)
            eventList.push(hasorEventListener);
    }
    @Override
    public void doSyncEvent(String eventType, Object... objects) {
        super.doSyncEvent(eventType, objects);
        //
        if (StringUtils.isBlank(eventType))
            return;
        LinkedList<HasorEventListener> eventList = this.listenerMap.get(eventType);
        if (eventList != null) {
            HasorEventListener listener = null;
            while ((listener = eventList.pollLast()) != null)
                listener.onEvent(eventType, objects);
        }
    }
    @Override
    public void doAsynEvent(final String eventType, final Object... objects) {
        super.doAsynEvent(eventType, objects);
        this.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isBlank(eventType))
                    return;
                LinkedList<HasorEventListener> eventList = listenerMap.get(eventType);
                if (eventList != null) {
                    HasorEventListener listener = null;
                    while ((listener = eventList.pollLast()) != null)
                        listener.onEvent(eventType, objects);
                }
            }
        });
    }
    @Override
    public synchronized void clean() {
        super.clean();
        this.listenerMap.clear();
        //停止所有计时器
        for (ScheduledFuture<?> fut : this.timerMap.values())
            fut.cancel(true);
        this.timerMap.clear();
    }
    @Override
    public synchronized void addTimer(final String timerName, final HasorEventListener hasorEventListener) throws RepeateException {
        if (this.timerMap.containsKey(timerName))
            throw new RepeateException(timerName + " timer is exist.");
        //
        int timerPeriod = this.getSettings().getInteger("framework.timerEvent");
        final String timerType = this.getSettings().getString("framework.timerEvent.type");
        ScheduledFuture<?> future = null;
        Runnable eventListener = new Runnable() {
            @Override
            public void run() {
                hasorEventListener.onEvent(timerName, null);
            }
        };
        /**固定间隔*/
        if (StringUtils.eqUnCaseSensitive(timerType, "FixedDelay")) {
            future = this.getExecutorService().scheduleWithFixedDelay(eventListener, 0, timerPeriod, TimeUnit.MILLISECONDS);
        }
        /**固定周期*/
        if (StringUtils.eqUnCaseSensitive(timerType, "FixedRate")) {
            future = this.getExecutorService().scheduleAtFixedRate(eventListener, 0, timerPeriod, TimeUnit.MILLISECONDS);
        }
        //
        if (future != null)
            this.timerMap.put(timerName, future);
    }
    @Override
    public synchronized void removeTimer(String timerName) {
        if (this.timerMap.containsKey(timerName)) {
            ScheduledFuture<?> future = this.timerMap.remove(timerName);
            future.cancel(false);
        }
    }
    @Override
    public void removeTimerNow(String timerName) {
        if (this.timerMap.containsKey(timerName)) {
            ScheduledFuture<?> future = this.timerMap.remove(timerName);
            future.cancel(true);
        }
    }
}