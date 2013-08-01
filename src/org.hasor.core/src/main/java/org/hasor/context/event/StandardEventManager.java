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
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.hasor.Hasor;
import org.hasor.context.EventManager;
import org.hasor.context.HasorEventListener;
import org.hasor.context.HasorSettingListener;
import org.hasor.context.Settings;
import org.more.RepeateException;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public class StandardEventManager implements EventManager {
    private Settings                          settings        = null;
    private ScheduledExecutorService          executorService = null;
    private Map<String, HasorEventListener[]> listenerMap     = new HashMap<String, HasorEventListener[]>();
    private ReadWriteLock                     listenerRWLock  = new ReentrantReadWriteLock();
    //
    public StandardEventManager(Settings settings) {
        this.settings = settings;
        this.executorService = Executors.newScheduledThreadPool(1);
        settings.addSettingsListener(new HasorSettingListener() {
            @Override
            public void onLoadConfig(Settings newConfig) {
                update();
            }
        });
        this.update();
    }
    /**获取Setting接口对象*/
    public Settings getSettings() {
        return this.settings;
    }
    private void update() {
        int eventThreadPoolSize = this.getSettings().getInteger("hasor.eventThreadPoolSize", 20);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) executorService;
        threadPool.setCorePoolSize(eventThreadPoolSize);
    }
    /**获取执行事件使用的ScheduledExecutorService接口对象。*/
    protected ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }
    @Override
    public void addEventListener(String eventType, HasorEventListener hasorEventListener) {
        this.listenerRWLock.writeLock().lock();//加锁(写)
        //
        Hasor.assertIsNotNull(hasorEventListener, "add EventListener object is null.");
        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (eventListenerArray == null) {
            eventListenerArray = new HasorEventListener[] { hasorEventListener };
            this.listenerMap.put(eventType, eventListenerArray);
        } else {
            if (ArrayUtils.contains(eventListenerArray, hasorEventListener) == false) {
                eventListenerArray = ArrayUtils.addToArray(eventListenerArray, hasorEventListener);
                this.listenerMap.put(eventType, eventListenerArray);
            }
        }
        //
        this.listenerRWLock.writeLock().unlock();//解锁(写)
    }
    @Override
    public void removeAllEventListener(String eventType) {
        this.listenerRWLock.writeLock().lock();//加锁(写)
        //
        this.listenerMap.remove(eventType);
        //
        this.listenerRWLock.writeLock().unlock();//解锁(写)
    }
    @Override
    public void removeEventListener(String eventType, HasorEventListener hasorEventListener) {
        this.listenerRWLock.writeLock().lock();//加锁(写)
        //
        Hasor.assertIsNotNull(eventType, "remove eventType is null.");
        Hasor.assertIsNotNull(hasorEventListener, "remove EventListener object is null.");
        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (!ArrayUtils.isBlank(eventListenerArray)) {
            eventListenerArray = ArrayUtils.removeInArray(eventListenerArray, hasorEventListener);
            this.listenerMap.put(eventType, eventListenerArray);
        }
        //
        this.listenerRWLock.writeLock().unlock();//解锁(写)
    }
    private static final HasorEventListener[] EmptyEventListener = new HasorEventListener[0];
    @Override
    public HasorEventListener[] getEventListener(String eventType) {
        this.listenerRWLock.readLock().lock();//加锁(读)
        //
        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (eventListenerArray != null) {
            HasorEventListener[] array = new HasorEventListener[eventListenerArray.length];
            System.arraycopy(eventListenerArray, 0, array, 0, eventListenerArray.length);
            eventListenerArray = array;
        } else
            eventListenerArray = EmptyEventListener;
        //
        this.listenerRWLock.readLock().unlock();//解锁(读)
        return eventListenerArray;
    }
    @Override
    public String[] getEventTypes() {
        this.listenerRWLock.readLock().lock();//加锁(读)
        //
        Set<String> eventTypes = this.listenerMap.keySet();
        String[] eventTypeNames = eventTypes.toArray(new String[eventTypes.size()]);
        //
        this.listenerRWLock.readLock().unlock();//解锁(读)
        return eventTypeNames;
    }
    @Override
    public void doSyncEvent(String eventType, Object... objects) {
        if (StringUtils.isBlank(eventType) == true)
            return;
        this.listenerRWLock.readLock().lock();//加锁(读)
        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        this.listenerRWLock.readLock().unlock();//解锁(读)
        //
        if (eventListenerArray != null) {
            for (HasorEventListener event : eventListenerArray)
                event.onEvent(eventType, objects);
        }
        //
        this.processOnceListener(eventType, objects);
    }
    @Override
    public void doAsynEvent(final String eventType, final Object... objects) {
        if (StringUtils.isBlank(eventType) == true)
            return;
        this.listenerRWLock.readLock().lock();//加锁(读)
        final HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
        this.listenerRWLock.readLock().unlock();//解锁(读)
        this.executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (eventListenerArray != null) {
                    for (HasorEventListener event : eventListenerArray)
                        event.onEvent(eventType, objects);
                }
                processOnceListener(eventType, objects);
            }
        });
    }
    @Override
    public synchronized void clean() {
        this.onceListenerLock.lock();//加锁
        this.onceListenerMap.clear();
        this.onceListenerLock.unlock();//解锁
        //
        //停止所有计时器
        for (ScheduledFuture<?> fut : this.timerMap.values())
            fut.cancel(true);
        this.timerMap.clear();
        //
        this.executorService.shutdownNow();
        this.executorService = Executors.newScheduledThreadPool(1);
        this.update();
    }
    private Map<String, LinkedList<HasorEventListener>> onceListenerMap  = new HashMap<String, LinkedList<HasorEventListener>>();
    private Lock                                        onceListenerLock = new ReentrantLock();
    private Map<String, ScheduledFuture<?>>             timerMap         = new HashMap<String, ScheduledFuture<?>>();
    @Override
    public void pushEventListener(String eventType, HasorEventListener hasorEventListener) {
        if (StringUtils.isBlank(eventType) || hasorEventListener == null)
            return;
        this.onceListenerLock.lock();//加锁
        LinkedList<HasorEventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList == null) {
            eventList = new LinkedList<HasorEventListener>();
            this.onceListenerMap.put(eventType, eventList);
        }
        if (eventList.contains(hasorEventListener) == false)
            eventList.push(hasorEventListener);
        this.onceListenerLock.unlock();//解锁
    }
    private void processOnceListener(String eventType, Object... objects) {
        this.onceListenerLock.lock();//加锁
        LinkedList<HasorEventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList != null) {
            HasorEventListener listener = null;
            while ((listener = eventList.pollLast()) != null)
                listener.onEvent(eventType, objects);
        }
        this.onceListenerLock.unlock();//解锁
    }
    @Override
    public synchronized void addTimer(final String timerName, final HasorEventListener hasorEventListener) throws RepeateException {
        if (this.timerMap.containsKey(timerName))
            throw new RepeateException(timerName + " timer is exist.");
        //
        int timerPeriod = this.getSettings().getInteger("hasor.timerEvent");
        final String timerType = this.getSettings().getString("hasor.timerEvent.type");
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
    public synchronized void removeTimerNow(String timerName) {
        if (this.timerMap.containsKey(timerName)) {
            ScheduledFuture<?> future = this.timerMap.remove(timerName);
            future.cancel(true);
        }
    }
}