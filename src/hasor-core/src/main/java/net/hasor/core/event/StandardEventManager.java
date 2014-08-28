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
import net.hasor.core.Environment;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardEventManager implements EventContext {
    private static final EmptyEventCallBackHook    EmptyAsyncCallBack = new EmptyEventCallBackHook();
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
        this.updateSettings();
    }
    private void updateSettings() {
        //更新ThreadPoolExecutor
        int eventThreadPoolSize = this.getSettings().getInteger("hasor.eventThreadPoolSize", 20);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executorService;
        threadPool.setCorePoolSize(eventThreadPoolSize);
        threadPool.setMaximumPoolSize(eventThreadPoolSize);
    }
    /**获取Setting接口对象*/
    public Settings getSettings() {
        return this.settings;
    }
    /**获取执行事件使用的ScheduledExecutorService接口对象。*/
    protected ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }
    //
    @Override
    public void pushListener(final String eventType, final EventListener eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return;
        }
        this.onceListenerLock.lock();//加锁
        LinkedList<EventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList == null) {
            eventList = new LinkedList<EventListener>();
            this.onceListenerMap.put(eventType, eventList);
        }
        if (eventList.contains(eventListener) == false) {
            eventList.addLast(eventListener);
        }
        this.onceListenerLock.unlock();//解锁
    }
    @Override
    public void addListener(final String eventType, final EventListener eventListener) {
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
    @Override
    public void removeListener(final String eventType, final EventListener eventListener) {
        this.listenerRWLock.writeLock().lock();//加锁(写)
        //
        Hasor.assertIsNotNull(eventType, "remove eventType is null.");
        Hasor.assertIsNotNull(eventListener, "remove EventListener object is null.");
        EventListener[] eventListenerArray = this.listenerMap.get(eventType);
        if (!ArrayUtils.isEmpty(eventListenerArray)) {
            int index = ArrayUtils.indexOf(eventListenerArray, eventListener);
            eventListenerArray = (EventListener[]) (index == ArrayUtils.INDEX_NOT_FOUND ? eventListenerArray : ArrayUtils.remove(eventListenerArray, index));
            this.listenerMap.put(eventType, eventListenerArray);
        }
        //
        this.listenerRWLock.writeLock().unlock();//解锁(写)
    }
    //
    @Override
    public final void fireSyncEvent(final String eventType, final Object... objects) {
        this.fireSyncEvent(eventType, null, objects);
    }
    @Override
    public final void fireSyncEvent(final String eventType, final EventCallBackHook callBack, final Object... objects) {
        this.fireEvent(eventType, true, callBack, objects);
    }
    @Override
    public final void fireAsyncEvent(final String eventType, final Object... objects) {
        this.fireAsyncEvent(eventType, null, objects);
    }
    @Override
    public final void fireAsyncEvent(final String eventType, final EventCallBackHook callBack, final Object... objects) {
        this.fireEvent(eventType, false, callBack, objects);
    }
    private final void fireEvent(final String eventType, final boolean sync, final EventCallBackHook callBack, final Object... objects) {
        EventObject event = this.createEvent(eventType, sync);
        event.setCallBack(callBack);
        event.addParams(objects);
        this.fireEvent(event);
    }
    /**创建事件对象*/
    protected EventObject createEvent(final String eventType, final boolean sync) {
        return new EventObject(eventType, sync);
    };
    /**引发事件*/
    protected void fireEvent(final EventObject event) {
        if (event.isSync()) {
            //同步的
            this.executeEvent(event);
        } else {
            //异步的
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    StandardEventManager.this.executeEvent(event);
                }
            });
        }
    };
    /**引发事件*/
    private void executeEvent(final EventObject eventObj) {
        String eventType = eventObj.getEventType();
        Object[] objects = eventObj.getParams();
        EventCallBackHook callBack = eventObj.getCallBack();
        callBack = callBack != null ? callBack : StandardEventManager.EmptyAsyncCallBack;
        if (StringUtils.isBlank(eventType) == true) {
            return;
        }
        //
        //1.引发事务.
        this.listenerRWLock.readLock().lock();//加锁(读)
        EventListener[] eventListenerArray = this.listenerMap.get(eventType);
        this.listenerRWLock.readLock().unlock();//解锁(读)
        if (eventListenerArray != null) {
            for (EventListener listener : eventListenerArray) {
                try {
                    listener.onEvent(eventType, objects);
                } catch (Throwable e) {
                    callBack.handleException(eventType, objects, e);
                } finally {
                    callBack.handleComplete(eventType, objects);
                }
            }
        }
        //
        //2.处理Once事务.
        this.onceListenerLock.lock();//加锁
        LinkedList<EventListener> eventList = this.onceListenerMap.get(eventType);
        if (eventList != null) {
            EventListener listener = null;
            while ((listener = eventList.pollLast()) != null) {
                try {
                    listener.onEvent(eventType, objects);
                } catch (Throwable e) {
                    callBack.handleException(eventType, objects, e);
                } finally {
                    callBack.handleComplete(eventType, objects);
                }
            }
        }
        this.onceListenerLock.unlock();//解锁
    };
    //
    public void release() {
        this.onceListenerLock.lock();//加锁
        this.onceListenerMap.clear();
        this.onceListenerLock.unlock();//解锁
        //
        this.executorService.shutdownNow();
        this.executorService = Executors.newScheduledThreadPool(1);
        this.updateSettings();
        //
        this.listenerRWLock.writeLock().lock();//加锁
        this.listenerMap.clear();
        this.listenerRWLock.writeLock().unlock();//解锁
    }
}