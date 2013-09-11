/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core.event;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.hasor.Hasor;
import net.hasor.core.AsyncCallBackHook;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.EventManager;
import net.hasor.core.Settings;
import net.hasor.core.SettingsListener;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardEventManager implements EventManager {
    private static final EmptyAsyncCallBackHook    EmptyAsyncCallBack = new EmptyAsyncCallBackHook();
    //
    private Settings                               settings           = null;
    private ScheduledExecutorService               executorService    = null;
    private Map<String, EventListener[]>           listenerMap        = new HashMap<String, EventListener[]>();
    private ReadWriteLock                          listenerRWLock     = new ReentrantReadWriteLock();
    private Map<String, LinkedList<EventListener>> onceListenerMap    = new HashMap<String, LinkedList<EventListener>>();
    private Lock                                   onceListenerLock   = new ReentrantLock();
    //
    public StandardEventManager(Environment env) {
        env = Hasor.assertIsNotNull(env, "Environment type parameter is empty!");
        this.settings = env.getSettings();
        this.executorService = Executors.newScheduledThreadPool(1);
        env.addSettingsListener(new SettingsListener() {
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
        //更新ThreadPoolExecutor
        int eventThreadPoolSize = this.getSettings().getInteger("hasor.eventThreadPoolSize", 20);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) executorService;
        threadPool.setCorePoolSize(eventThreadPoolSize);
        threadPool.setMaximumPoolSize(eventThreadPoolSize);
    }
    /**获取执行事件使用的ScheduledExecutorService接口对象。*/
    protected ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }
    //
    //
    //
    //
    //
    public void pushEventListener(String eventType, EventListener eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null)
            return;
        this.onceListenerLock.lock();//加锁
        LinkedList<EventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList == null) {
            eventList = new LinkedList<EventListener>();
            this.onceListenerMap.put(eventType, eventList);
        }
        if (eventList.contains(eventListener) == false)
            eventList.push(eventListener);
        this.onceListenerLock.unlock();//解锁
    }
    public void addEventListener(String eventType, EventListener eventListener) {
        this.listenerRWLock.writeLock().lock();//加锁(写)
        //
        Hasor.assertIsNotNull(eventListener, "add EventListener object is null.");
        EventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (eventListenerArray == null) {
            eventListenerArray = new EventListener[] { eventListener };
            this.listenerMap.put(eventType, eventListenerArray);
        } else {
            if (ArrayUtils.contains(eventListenerArray, eventListener) == false) {
                eventListenerArray = (EventListener[]) ArrayUtils.add(eventListenerArray, eventListener);
                this.listenerMap.put(eventType, eventListenerArray);
            }
        }
        //
        this.listenerRWLock.writeLock().unlock();//解锁(写)
    }
    //    public void removeAllEventListener(String eventType) {
    //        this.listenerRWLock.writeLock().lock();//加锁(写)
    //        this.listenerMap.remove(eventType);
    //        this.listenerRWLock.writeLock().unlock();//解锁(写)
    //    }
    public void removeEventListener(String eventType, EventListener eventListener) {
        this.listenerRWLock.writeLock().lock();//加锁(写)
        //
        Hasor.assertIsNotNull(eventType, "remove eventType is null.");
        Hasor.assertIsNotNull(eventListener, "remove EventListener object is null.");
        EventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (!ArrayUtils.isEmpty(eventListenerArray)) {
            int index = ArrayUtils.indexOf(eventListenerArray, eventListener);
            eventListenerArray = (EventListener[]) ((index == ArrayUtils.INDEX_NOT_FOUND) ? eventListenerArray : ArrayUtils.remove(eventListenerArray, index));
            this.listenerMap.put(eventType, eventListenerArray);
        }
        //
        this.listenerRWLock.writeLock().unlock();//解锁(写)
    }
    //    public HasorEventListener[] getEventListener(String eventType) {
    //        this.listenerRWLock.readLock().lock();//加锁(读)
    //        //
    //        HasorEventListener[] eventListenerArray = this.listenerMap.get(eventType);
    //        if (eventListenerArray != null) {
    //            HasorEventListener[] array = new HasorEventListener[eventListenerArray.length];
    //            System.arraycopy(eventListenerArray, 0, array, 0, eventListenerArray.length);
    //            eventListenerArray = array;
    //        } else
    //            eventListenerArray = EmptyEventListener;
    //        //
    //        this.listenerRWLock.readLock().unlock();//解锁(读)
    //        return eventListenerArray;
    //    }
    //    public String[] getEventTypes() {
    //        this.listenerRWLock.readLock().lock();//加锁(读)
    //        //
    //        Set<String> eventTypes = this.listenerMap.keySet();
    //        String[] eventTypeNames = eventTypes.toArray(new String[eventTypes.size()]);
    //        //
    //        this.listenerRWLock.readLock().unlock();//解锁(读)
    //        return eventTypeNames;
    //    }
    //
    //
    //
    //
    //
    public void doSyncEvent(String eventType, Object... objects) throws Throwable {
        this._doSyncEvent(false, eventType, objects);
    }
    public void doSyncEventIgnoreThrow(String eventType, Object... objects) {
        try {
            this._doSyncEvent(true, eventType, objects);
        } catch (Throwable e) {
            throw new RuntimeException(e);//由于ignore参数为true不会抛出事件的异常。这里可以再次抛出异常原因是确保不会吞掉潜在的异常信息。
        }
    }
    public void doAsynEvent(String eventType, AsyncCallBackHook callBack, Object... objects) {
        _doAsynEvent(false, eventType, callBack, objects);
    }
    public void doAsynEventIgnoreThrow(final String eventType, final Object... objects) {
        _doAsynEvent(true, eventType, null, objects);
    }
    private void _doSyncEvent(boolean ignore, String eventType, Object... objects) throws Throwable {
        if (StringUtils.isBlank(eventType) == true)
            return;
        this.listenerRWLock.readLock().lock();//加锁(读)
        EventListener[] eventListenerArray = this.listenerMap.get(eventType);
        this.listenerRWLock.readLock().unlock();//解锁(读)
        //
        if (eventListenerArray != null) {
            for (EventListener event : eventListenerArray)
                try {
                    event.onEvent(eventType, objects);
                } catch (Throwable e) {
                    if (ignore)
                        Hasor.warning("During the execution of SyncEvent ‘%s’ throw an error.%s", event.getClass(), e);
                    else
                        throw e;
                }
        }
        //处理OnceListener
        this.processOnceListener(ignore, eventType, EmptyAsyncCallBack, objects);
    }
    private void _doAsynEvent(final boolean ignore, final String eventType, final AsyncCallBackHook hook, final Object... objects) {
        if (StringUtils.isBlank(eventType) == true)
            return;
        final AsyncCallBackHook callBack = (hook != null) ? hook : EmptyAsyncCallBack;
        this.listenerRWLock.readLock().lock();//加锁(读)
        final EventListener[] eventListenerArray = this.listenerMap.get(eventType);
        this.listenerRWLock.readLock().unlock();//解锁(读)
        this.executorService.submit(new Runnable() {
            public void run() {
                if (eventListenerArray != null) {
                    for (EventListener event : eventListenerArray)
                        try {
                            event.onEvent(eventType, objects);
                        } catch (Throwable e) {
                            if (ignore)
                                Hasor.warning("During the execution of AsynEvent ‘%s’ throw an error.%s", event.getClass(), e);
                            else
                                callBack.handleException(eventType, objects, e);
                        }
                }
                //处理OnceListener
                processOnceListener(ignore, eventType, callBack, objects);
                callBack.handleComplete(eventType, objects);
            }
        });
    }
    private void processOnceListener(boolean ignore, String eventType, AsyncCallBackHook callBack, Object... objects) {
        this.onceListenerLock.lock();//加锁
        LinkedList<EventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList != null) {
            EventListener listener = null;
            while ((listener = eventList.pollLast()) != null) {
                try {
                    listener.onEvent(eventType, objects);
                } catch (Throwable e) {
                    if (ignore)
                        Hasor.warning("During the execution of OnceListener ‘%s’ throw an error.%s", listener.getClass(), e);
                    else
                        callBack.handleException(eventType, objects, e);
                }
            }
        }
        this.onceListenerLock.unlock();//解锁
    }
    //
    //
    //
    //
    //
    public synchronized void clean() {
        this.onceListenerLock.lock();//加锁
        this.onceListenerMap.clear();
        this.onceListenerLock.unlock();//解锁
        //
        this.executorService.shutdownNow();
        this.executorService = Executors.newScheduledThreadPool(1);
        this.update();
    }
    public void release() {
        this.clean();
        this.listenerRWLock.writeLock().lock();//加锁
        this.listenerMap.clear();
        this.listenerRWLock.writeLock().unlock();//解锁
    }
}