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
package net.hasor.core;
import net.hasor.core.spi.AppContextAware;

import java.util.Objects;
import java.util.function.Supplier;

import static net.hasor.core.AppContext.ContextEvent_Shutdown;
import static net.hasor.core.AppContext.ContextEvent_Started;

/**
 * Hasor 基础工具包。
 * @version : 2013-4-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class HasorUtils {
    /**
     * 将{@link AppContextAware}接口实现类注册到容器中，Hasor 会在启动的第一时间为这些对象执行注入。
     * @param awareProvider 需要被注册的 AppContextAware 接口实现对象。
     * @return 返回 aware 参数本身。
     */
    public static <T extends AppContextAware> Supplier<T> autoAware(Environment env, final Supplier<T> awareProvider) {
        Objects.requireNonNull(env, "EventContext is null.");
        if (awareProvider != null) {
            env.getEventContext().pushListener(ContextEvent_Started, (EventListener<AppContext>) (event, eventData) -> {
                awareProvider.get().setAppContext(eventData);
            });
        }
        return awareProvider;
    }

    /**
     * 将{@link AppContextAware}接口实现类注册到容器中，Hasor 会在启动的第一时间为这些对象执行注入。
     * @param aware 需要被注册的 AppContextAware 接口实现对象。
     * @return 返回 aware 参数本身。
     */
    public static <T extends AppContextAware> T autoAware(Environment env, final T aware) {
        Objects.requireNonNull(env, "EventContext is null.");
        if (aware != null) {
            pushStartListener(env, (EventListener<AppContext>) (event, eventData) -> {
                aware.setAppContext(eventData);
            });
        }
        return aware;
    }

    public static <TD, T extends EventListener<TD>> T pushStartListener(Environment env, T eventListener) {
        env.getEventContext().pushListener(ContextEvent_Started, eventListener);
        return eventListener;
    }

    public static <TD, T extends EventListener<TD>> T pushShutdownListener(Environment env, T eventListener) {
        env.getEventContext().pushListener(ContextEvent_Shutdown, eventListener);
        return eventListener;
    }

    public static <T extends EventListener<AppContext>> BindInfo<T> pushStartListener(Environment env, final BindInfo<T> eventListener) {
        env.getEventContext().pushListener(ContextEvent_Started, doLazyCallEvent(eventListener));
        return eventListener;
    }

    public static <T extends EventListener<AppContext>> BindInfo<T> pushShutdownListener(Environment env, final BindInfo<T> eventListener) {
        env.getEventContext().pushListener(ContextEvent_Shutdown, doLazyCallEvent(eventListener));
        return eventListener;
    }

    private static EventListener<AppContext> doLazyCallEvent(BindInfo<? extends EventListener<AppContext>> bindInfo) {
        return (event1, eventData) -> eventData.getInstance(bindInfo).onEvent(event1, eventData);
    }
}