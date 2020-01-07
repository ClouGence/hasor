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
import net.hasor.core.spi.SpiCaller;
import net.hasor.core.spi.SpiChainProcessor;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.utils.ExceptionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * SPI 管理器
 * @version : 2019年06月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SpiCallerContainer extends AbstractContainer implements SpiTrigger {
    private ConcurrentHashMap<Class<?>, List<Supplier<EventListener>>>  spiListener      = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<?>, Supplier<SpiChainProcessor<?>>> spiChainListener = new ConcurrentHashMap<>();

    /** 执行 SPI */
    @Override
    public <R, T extends EventListener> R callResultSpi(Class<T> spiType, SpiCaller<T, R> spiCaller, R defaultResult) {
        List<Supplier<EventListener>> listeners = this.spiListener.get(spiType);
        if (listeners == null || listeners.isEmpty()) {
            return defaultResult;
        }
        // .得到 SpiChainProcessor
        SpiChainProcessor<R> spiChain = null;
        if (spiChainListener.get(spiType) != null) {
            spiChain = (SpiChainProcessor<R>) spiChainListener.get(spiType).get();
        }
        //
        AtomicReference<R> result = new AtomicReference<>(defaultResult);
        for (Supplier<EventListener> listener : listeners) {
            try {
                if (spiChain == null) {
                    result.set(spiCaller.doResultSpi((T) listener.get()));
                } else {
                    R spiResult = spiChain.nextSpi(new SpiChainProcessor.SpiChainInvocation<R>() {
                        @Override
                        public R lastSpiResult() {
                            return result.get();
                        }

                        @Override
                        public R doSpi() throws Throwable {
                            return spiCaller.doResultSpi((T) listener.get());
                        }
                    });
                    result.set(spiResult);
                }
            } catch (Throwable e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
        return result.get();
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

    /** 注册一个 SPI 监听器链处理器 */
    public synchronized <T extends EventListener> void bindSpiChainProcessor(Class<T> spiType, Supplier<SpiChainProcessor<?>> chainProcessorSupplier) {
        Objects.requireNonNull(spiType, "spiType is null.");
        Objects.requireNonNull(spiListener, "chainProcessorSupplier is null.");
        this.spiChainListener.put(spiType, chainProcessorSupplier);
    }

    /** 遍历所有 Listener */
    public void forEachListener(Consumer<Map.Entry<Class<?>, EventListener>> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<Class<?>, List<Supplier<EventListener>>> entryList : spiListener.entrySet()) {
            Class<?> listenerKey = entryList.getKey();
            for (Supplier<EventListener> entry : entryList.getValue()) {
                action.accept(new MapEntry(listenerKey, entry.get()));
            }
        }
    }

    /** A single entry in the map. */
    private final class MapEntry implements Map.Entry<Class<?>, EventListener> {
        private Class<?>      listenerKey;
        private EventListener listenerEntry;

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
        //
    }

    /** 销毁过程，清理掉所有已经注册的 SPI 监听器 */
    @Override
    protected void doClose() {
        this.spiListener.clear();
    }
}