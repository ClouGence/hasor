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
package net.hasor.core.event;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.utils.StringUtils;

import java.util.List;
import java.util.concurrent.*;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardEventManager implements EventContext {
    private static final EmptyEventCallBackHook                   EMPTY_CALLBACK  = new EmptyEventCallBackHook();
    private              ScheduledExecutorService                 executorService = null;
    private              ConcurrentMap<String, EventListenerPool> listenerMap     = new ConcurrentHashMap<String, EventListenerPool>();
    //
    //
    public StandardEventManager(int eventThreadPoolSize, String name, ClassLoader classLoader) {
        this.executorService = Executors.newScheduledThreadPool(eventThreadPoolSize, new NameThreadFactory(name + "-EventPool-%s", classLoader));
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
    public <T> void pushListener(final String eventType, final EventListener<T> eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return;
        }
        this.getListenerPool(eventType).pushOnceListener(eventListener);
    }
    @Override
    public <T> void addListener(final String eventType, final EventListener<T> eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return;
        }
        this.getListenerPool(eventType).addListener(eventListener);
    }
    @Override
    public <T> void removeListener(final String eventType, final EventListener<T> eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return;
        }
        this.getListenerPool(eventType).removeListener(eventListener);
    }
    //
    @Override
    public final <T> void fireSyncEvent(final String eventType, final T eventData) {
        this.fireSyncEvent(eventType, null, eventData);
    }
    @Override
    public final <T> void fireSyncEvent(final String eventType, final EventCallBackHook<T> callBack, final T eventData) {
        this.fireEvent(eventType, true, callBack, eventData);
    }
    @Override
    public final <T> void fireAsyncEvent(final String eventType, final T eventData) {
        this.fireAsyncEvent(eventType, null, eventData);
    }
    @Override
    public final <T> void fireAsyncEvent(final String eventType, final EventCallBackHook<T> callBack, final T eventData) {
        this.fireEvent(eventType, false, callBack, eventData);
    }
    private final <T> void fireEvent(final String eventType, final boolean sync, final EventCallBackHook<T> callBack, final T eventData) {
        EventObject<T> event = this.createEvent(eventType, sync);
        event.setCallBack(callBack);
        event.setEventData(eventData);
        this.fireEvent(event);
    }
    /**创建事件对象*/
    protected <T> EventObject<T> createEvent(final String eventType, final boolean sync) {
        return new EventObject<T>(eventType, sync);
    }
    ;
    /**引发事件*/
    protected <T> void fireEvent(final EventObject<T> event) {
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
    }
    ;
    /**引发事件*/
    protected <T> void executeEvent(final EventObject<T> eventObj) {
        String eventType = eventObj.getEventType();
        T eventData = eventObj.getEventData();
        EventCallBackHook<T> callBack = eventObj.getCallBack();
        callBack = (callBack != null ? callBack : (EventCallBackHook<T>) StandardEventManager.EMPTY_CALLBACK);
        if (StringUtils.isBlank(eventType)) {
            return;
        }
        //
        //1.引发事件.
        EventListenerPool listenerPool = this.getListenerPool(eventType);
        if (listenerPool != null) {
            List<EventListener<?>> snapshot = (List<EventListener<?>>) listenerPool.getListenerSnapshot();
            for (EventListener<?> listenerItem : snapshot) {
                try {
                    EventListener<Object> listener = (EventListener<Object>) listenerItem;
                    listener.onEvent(eventType, eventData);
                    callBack.handleComplete(eventType, eventData);
                } catch (Throwable e) {
                    callBack.handleException(eventType, eventData, e);
                }
            }
        }
        //
        //2.处理Once事件.
        List<EventListener<?>> onceList = listenerPool.popOnceListener();
        if (onceList != null) {
            for (EventListener<?> listenerItem : onceList) {
                try {
                    EventListener<Object> listener = (EventListener<Object>) listenerItem;
                    listener.onEvent(eventType, eventData);
                    callBack.handleComplete(eventType, eventData);
                } catch (Throwable e) {
                    callBack.handleException(eventType, eventData, e);
                }
            }
        }
    }
    ;
    //
    public void release() {
        this.executorService.shutdownNow();
        this.listenerMap.clear();
    }
}