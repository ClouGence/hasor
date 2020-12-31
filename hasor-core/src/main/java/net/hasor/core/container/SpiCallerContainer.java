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
package net.hasor.core.container;
import net.hasor.core.Environment;
import net.hasor.core.spi.SpiCaller;
import net.hasor.core.spi.SpiJudge;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SPI 管理器。
 * @version : 2019年06月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpiCallerContainer extends AbstractContainer implements SpiTrigger {
    protected static Logger                                                     logger      = LoggerFactory.getLogger(SpiCallerContainer.class);
    private final    Environment                                                environment;
    private final    ConcurrentHashMap<Class<?>, List<Supplier<EventListener>>> spiListener = new ConcurrentHashMap<>();
    private final    ConcurrentHashMap<Class<?>, Supplier<SpiJudge>>            spiSpiJudge = new ConcurrentHashMap<>();

    public SpiCallerContainer() {
        this(null);
    }

    public SpiCallerContainer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public <R, T extends EventListener> R notifySpi(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult) {
        return spiCommonCall(spiType, spiCaller, defaultResult, true, false);
    }

    @Override
    public <R, T extends EventListener> R notifyWithoutJudge(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult) {
        return spiCommonCall(spiType, spiCaller, defaultResult, true, true);
    }

    @Override
    public <R, T extends EventListener> R chainSpi(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult) {
        return spiCommonCall(spiType, spiCaller, defaultResult, false, false);
    }

    @Override
    public boolean hasSpi(Class<? extends EventListener> spiType) {
        return this.spiListener.containsKey(spiType);
    }

    @Override
    public boolean hasJudge(Class<? extends EventListener> spiJudge) {
        return this.spiSpiJudge.containsKey(spiJudge);
    }

    private <R, T extends EventListener> R spiCommonCall(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult, boolean isNotify, boolean ignoreJudge) {
        List<Supplier<EventListener>> listeners = this.spiListener.get(spiType);
        // .没有 SPI 监听器，那么返回默认值
        if (listeners == null || listeners.isEmpty()) {
            return defaultResult;
        }
        // .只有一个 SPI 监听器，那么选用监听器的值
        if (listeners.size() == 1) {
            try {
                T listener = (T) listeners.get(0).get();
                return spiCaller.doResultSpi(listener, defaultResult);
            } catch (Throwable e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        //
        // .多个 SPI 监听器情况下，通过仲裁决定哪些监听器有效
        SpiJudge spiJudge = SpiJudge.DEFAULT;
        if (!ignoreJudge) {
            if (this.spiSpiJudge.containsKey(spiType)) {
                // 有仲裁，但是仲裁不能为空
                spiJudge = this.spiSpiJudge.get(spiType).get();
                Objects.requireNonNull(spiJudge, "spi '" + spiType.getName() + "' SpiJudge is null.");
            } else if (isNotify) {
                // 必须要设置仲裁
                throw new UnsupportedOperationException("spi '" + spiType.getName() + "' encounters Multiple, require SpiJudge.");
            }
        }
        List<EventListener> collect = listeners.stream().map(Supplier::get).collect(Collectors.toList());
        collect = spiJudge.judgeSpi(collect);
        //
        // .执行监听器
        try {
            if (isNotify) {
                List<R> list = new ArrayList<>();
                for (EventListener listener : collect) {
                    list.add(spiCaller.doResultSpi((T) listener, defaultResult));
                }
                return spiJudge.judgeResult(list, defaultResult);// .仲裁SPI返回结果
            } else {
                R lastResult = defaultResult;
                for (EventListener listener : collect) {
                    lastResult = spiCaller.doResultSpi((T) listener, lastResult);
                }
                return lastResult;
            }
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    /** 获取某个类型 SPI 下面的所有监听器。 */
    public List<Supplier<EventListener>> getEventListenerList(Class<?> spiType) {
        return this.spiListener.get(spiType);
    }

    /** 获取已经注册的 SPI 类型总数。 */
    public int getListenerTypeSize() {
        return this.spiListener.size();
    }

    /** 获取所有SPI总共注册的监听器数。 */
    public long getListenerSize() {
        return this.spiListener.entrySet().stream()//
                .flatMap((Function<Map.Entry<Class<?>, List<Supplier<EventListener>>>, Stream<?>>) classListEntry -> {
                    return classListEntry.getValue().stream();
                }).count();
    }

    /** 注册一个 SPI 监听器 */
    public synchronized <T extends EventListener> void addListener(Class<T> spiType, Supplier<T> spiListener) {
        Objects.requireNonNull(spiType, "spiType is null.");
        Objects.requireNonNull(spiListener, "spiListener is null.");
        //
        List<Supplier<EventListener>> listenerList = this.spiListener.computeIfAbsent(spiType, k -> {
            return new ArrayList<>(5);
        });
        listenerList.add((Supplier<EventListener>) spiListener);
    }

    /** 注册一个 SPI 仲裁 */
    public synchronized <T extends EventListener> void bindSpiJudge(Class<T> spiType, Supplier<SpiJudge> spiSpiJudgeSupplier) {
        Objects.requireNonNull(spiType, "spiType is null.");
        Objects.requireNonNull(spiSpiJudgeSupplier, "spiSpiJudgeSupplier is null.");
        this.spiSpiJudge.put(spiType, spiSpiJudgeSupplier);
    }

    /** 遍历所有 Listener */
    public void forEachListener(Consumer<Map.Entry<Class<?>, EventListener>> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<Class<?>, List<Supplier<EventListener>>> entryList : this.spiListener.entrySet()) {
            Class<?> listenerKey = entryList.getKey();
            for (Supplier<EventListener> entry : entryList.getValue()) {
                action.accept(new MapEntry(listenerKey, entry.get()));
            }
        }
    }

    /** A single entry in the map. */
    private static final class MapEntry implements Map.Entry<Class<?>, EventListener> {
        private final Class<?>      listenerKey;
        private final EventListener listenerEntry;

        public MapEntry(Class<?> listenerKey, EventListener listenerEntry) {
            this.listenerKey = listenerKey;
            this.listenerEntry = listenerEntry;
        }

        @Override
        public Class<?> getKey() {
            return this.listenerKey;
        }

        @Override
        public EventListener getValue() {
            return this.listenerEntry;
        }

        @Override
        public EventListener setValue(EventListener value) {
            throw new UnsupportedOperationException("this entry no support.");
        }
    }

    /** 初始化过程 */
    protected void doInitialize() {
        if (this.environment != null) {
            String[] checkAutoLoadArrays = this.environment.getSettings().getStringArray("hasor.autoLoadSpi.spi");
            Set<String> checkAutoLoad = new HashSet<>(Arrays.asList(checkAutoLoadArrays));
            Set<Class<?>> autoLoadSet = new HashSet<>();
            try {
                ResourcesUtils.scan("META-INF/services/*", (event, isInJar) -> {
                    String eventName = event.getName().substring("META-INF/services/".length());
                    if (checkAutoLoad.contains(eventName)) {
                        try {
                            Class<?> aClass = this.environment.getClassLoader().loadClass(eventName);
                            autoLoadSet.add(aClass);
                        } catch (ClassNotFoundException e) {
                            /*  */
                        }
                    }
                });
            } catch (IOException | URISyntaxException e) {
                logger.error(e.getMessage(), e);
            }
            //
            for (Class<?> autoLoad : autoLoadSet) {
                if (!EventListener.class.isAssignableFrom(autoLoad)) {
                    logger.error("spi " + autoLoad.getName() + " is not java.util.EventListener");
                    continue;
                }
                ServiceLoader<?> serviceLoader = ServiceLoader.load(autoLoad);
                for (Object spiObject : serviceLoader) {
                    if (!autoLoad.isInstance(spiObject)) {
                        logger.error("spi " + spiObject.getClass() + " not implement " + autoLoad.getName());
                        continue;
                    }
                    logger.info("load Java SPI " + autoLoad.getName() + " implement by " + spiObject.getClass());
                    addListener((Class<EventListener>) autoLoad, () -> {
                        return (EventListener) spiObject;
                    });
                }
            }
            //
        }
    }

    /** 销毁过程，清理掉所有已经注册的 SPI 监听器 */
    @Override
    protected void doClose() {
        this.spiListener.clear();
    }
}