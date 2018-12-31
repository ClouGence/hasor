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
import net.hasor.core.*;
import net.hasor.utils.NameThreadFactory;
import net.hasor.utils.StringUtils;
import net.hasor.utils.future.BasicFuture;
import net.hasor.utils.future.FutureCallback;

import java.util.List;
import java.util.concurrent.*;
/**
 * 标准事件处理器接口的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardEventManager implements EventContext {
    private ScheduledExecutorService                 executorService = null;
    private ConcurrentMap<String, EventListenerPool> listenerMap     = new ConcurrentHashMap<String, EventListenerPool>();
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
    public <T> boolean pushListener(final String eventType, final EventListener<T> eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return false;
        }
        return this.getListenerPool(eventType).pushOnceListener(eventListener);
    }
    //
    @Override
    public <T> boolean addListener(final String eventType, final EventListener<T> eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return false;
        }
        return this.getListenerPool(eventType).addListener(eventListener);
    }
    //
    @Override
    public <T> boolean removeListener(final String eventType, final EventListener<T> eventListener) {
        if (StringUtils.isBlank(eventType) || eventListener == null) {
            return false;
        }
        return this.getListenerPool(eventType).removeListener(eventListener);
    }
    @Override
    public <T> boolean clearListener(String eventType) {
        if (StringUtils.isBlank(eventType)) {
            return false;
        }
        return this.getListenerPool(eventType).clearListener();
    }
    //
    @Override
    public final <T> void fireSyncEvent(final String eventType, final T eventData) throws Throwable {
        try {
            this.fireEvent(eventType, FireType.Interrupt, null, true, eventData).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
    //
    @Override
    public final <T> void fireSyncEventWithAlone(final String eventType, final T eventData) throws Throwable {
        final BasicFuture<Void> future = new BasicFuture<Void>();
        this.asyncTask(new Runnable() {
            @Override
            public void run() {
                try {
                    fireSyncEvent(eventType, eventData);
                    future.completed(null);
                } catch (Throwable e) {
                    future.failed(e);
                }
            }
        });
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
    //
    @Override
    public final <T> void fireAsyncEvent(String eventType, T eventData) {
        this.fireEvent(eventType, FireType.Interrupt, null, false, eventData);
    }
    //
    @Override
    public final <T> void fireAsyncEvent(String eventType, T eventData, FireType fireType) {
        this.fireEvent(eventType, fireType, null, false, eventData);
    }
    //
    @Override
    public final <T> void fireAsyncEvent(String eventType, T eventData, FireType fireType, EventCallBackHook<T> callBack) {
        this.fireEvent(eventType, fireType, callBack, false, eventData);
    }
    @Override
    public <T> boolean asyncTask(final Callable<T> runnable, final FutureCallback<T> callBack) {
        if (runnable == null) {
            return false;
        }
        getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    T result = runnable.call();
                    if (callBack != null) {
                        callBack.completed(result);
                    }
                } catch (Exception e) {
                    if (callBack != null) {
                        callBack.failed(e);
                    }
                }
            }
        });
        return true;
    }
    @Override
    public boolean asyncTask(final Runnable runnable, FutureCallback<Void> callBack) {
        if (runnable == null) {
            return false;
        }
        this.asyncTask(new Callable<Void>() {
            public Void call() {
                runnable.run();
                return null;
            }
        }, callBack);
        return true;
    }
    @Override
    public <T> Future<T> asyncTask(Callable<T> runnable) {
        if (runnable == null) {
            return null;
        }
        final BasicFuture<T> future = new BasicFuture<T>();
        this.asyncTask(runnable, new FutureCallback<T>() {
            @Override
            public void completed(T result) {
                future.completed(result);
            }
            @Override
            public void failed(Throwable ex) {
                future.failed(ex);
            }
        });
        return future;
    }
    @Override
    public Future<Void> asyncTask(final Runnable runnable) {
        if (runnable == null) {
            return null;
        }
        return this.asyncTask(new Callable<Void>() {
            public Void call() {
                runnable.run();
                return null;
            }
        });
    }
    //
    //
    private <T> Future<Boolean> fireEvent(String eventType, FireType fireType, EventCallBackHook<T> callBack, boolean atCurrentThread, T eventData) {
        EventObject<T> event = this.createEvent(eventType, Hasor.assertIsNotNull(fireType));
        event.setCallBack(callBack);
        event.setEventData(eventData);
        return this.fireEvent(event, atCurrentThread);
    }
    /**创建事件对象*/
    protected <T> EventObject<T> createEvent(String eventType, FireType fireType) {
        return new EventObject<T>(eventType, fireType);
    }
    /**引发事件，无论*/
    protected <T> Future<Boolean> fireEvent(final EventObject<T> event, boolean atCurrentThread) {
        final BasicFuture<Boolean> future = new BasicFuture<Boolean>();
        if (atCurrentThread) {
            this.executeEvent(event, future);
        } else {
            getExecutorService().submit(new Runnable() {
                public void run() {
                    StandardEventManager.this.executeEvent(event, future);
                }
            });
        }
        //
        return future;
    }
    /**引发事件*/
    protected <T> void executeEvent(final EventObject<T> eventObj, BasicFuture<Boolean> future) {
        String eventType = eventObj.getEventType();
        T eventData = eventObj.getEventData();
        EventCallBackHook<T> callBack = eventObj.getCallBack();
        if (StringUtils.isBlank(eventType)) {
            future.failed(new NullPointerException("eventType is empty."));
            return;
        }
        //
        //1.引发事件.
        EventListenerPool listenerPool = this.getListenerPool(eventType);
        if (listenerPool != null) {
            List<EventListener<?>> snapshot = listenerPool.getListenerSnapshot();
            for (EventListener<?> listenerItem : snapshot) {
                Throwable doListener = doListener(eventObj, eventType, eventData, callBack, (EventListener<T>) listenerItem);
                if (doListener == null) {
                    continue;
                }
                future.failed(doListener);
                return;
            }
        }
        //
        //2.处理Once事件.
        List<EventListener<?>> onceList = (listenerPool != null) ? listenerPool.popOnceListener() : null;
        if (onceList != null) {
            for (EventListener<?> listenerItem : onceList) {
                Throwable doListener = doListener(eventObj, eventType, eventData, callBack, (EventListener<T>) listenerItem);
                if (doListener == null) {
                    continue;
                }
                future.failed(doListener);
                return;
            }
        }
        //
        future.completed(true);
        return;
    }
    private <T> Throwable doListener(EventObject<T> eventObj, String eventType, T eventData, EventCallBackHook<T> callBack, EventListener<T> listener) {
        try {
            listener.onEvent(eventType, eventData);
            if (callBack != null) {
                callBack.handleComplete(eventType, eventData);
            }
        } catch (Throwable e) {
            if (callBack != null) {
                callBack.handleException(eventType, eventData, e);
            }
            if (FireType.Interrupt == eventObj.getFireType()) {
                return e;
            }
        }
        return null;
    }
    //
    public void release() {
        this.executorService.shutdownNow();
        this.listenerMap.clear();
    }
}