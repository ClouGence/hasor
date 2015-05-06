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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import org.more.util.StringUtils;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardEventManager implements EventContext {
    private static final EmptyEventCallBackHook      EMPTY_CALLBACK  = new EmptyEventCallBackHook();
    //
    private ScheduledExecutorService                 executorService = null;
    private ConcurrentMap<String, EventListenerPool> listenerMap     = new ConcurrentHashMap<String, EventListenerPool>();
    //
    //
    public StandardEventManager(int eventThreadPoolSize) {
        this.executorService = Executors.newScheduledThreadPool(eventThreadPoolSize);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executorService;
        threadPool.setCorePoolSize(eventThreadPoolSize);
        threadPool.setMaximumPoolSize(eventThreadPoolSize);
    }
    /**获取执行事件使用的{@link ScheduledExecutorService}接口对象。*/
    protected ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }
    //
    private EventListenerPool getListenerPool(String eventType) {
        EventListenerPool pool = listenerMap.get(eventType);
        if (pool == null) {
            EventListenerPool newPool = new EventListenerPool();
            pool = listenerMap.putIfAbsent(eventType, newPool);
            if (pool == null) {
                pool = newPool;
            }
        }
        return pool;
    }
    //
    @Override
    public void pushListener(final String eventType, final EventListener eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return;
        }
        this.getListenerPool(eventType).pushOnceListener(eventListener);
    }
    @Override
    public void addListener(final String eventType, final EventListener eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return;
        }
        this.getListenerPool(eventType).addListener(eventListener);
    }
    @Override
    public void removeListener(final String eventType, final EventListener eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return;
        }
        this.getListenerPool(eventType).removeListener(eventListener);
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
        callBack = callBack != null ? callBack : StandardEventManager.EMPTY_CALLBACK;
        if (StringUtils.isBlank(eventType) == true) {
            return;
        }
        //
        //1.引发事件.
        EventListenerPool listenerPool = this.getListenerPool(eventType);
        if (listenerPool != null) {
            List<EventListener> snapshot = listenerPool.getListenerSnapshot();
            for (EventListener listener : snapshot) {
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
        //2.处理Once事件.
        List<EventListener> onceList = listenerPool.popOnceListener();
        if (onceList != null) {
            for (EventListener listener : onceList) {
                try {
                    listener.onEvent(eventType, objects);
                } catch (Throwable e) {
                    callBack.handleException(eventType, objects, e);
                } finally {
                    callBack.handleComplete(eventType, objects);
                }
            }
        }
    };
    //
    public void release() {
        this.executorService.shutdownNow();
        this.listenerMap.clear();
    }
}
//
class EventListenerPool {
    private final Object                              ONCE_LOCK = new Object();
    private ArrayList<EventListener>                  onceListener;
    private final CopyOnWriteArrayList<EventListener> listenerList;
    //
    public EventListenerPool() {
        onceListener = new ArrayList<EventListener>();
        listenerList = new CopyOnWriteArrayList<EventListener>();
    }
    //
    public void pushOnceListener(EventListener eventListener) {
        synchronized (ONCE_LOCK) {
            onceListener.add(eventListener);
        }
    }
    public void addListener(EventListener eventListener) {
        listenerList.add(eventListener);
    }
    //
    public List<EventListener> popOnceListener() {
        List<EventListener> onceList = null;
        synchronized (ONCE_LOCK) {
            onceList = this.onceListener;
            this.onceListener = new ArrayList<EventListener>();
        }
        return onceList;
    }
    public List<EventListener> getListenerSnapshot() {
        return new ArrayList<EventListener>(this.listenerList);
    }
    public void removeListener(EventListener eventListener) {
        listenerList.remove(eventListener);
    }
}