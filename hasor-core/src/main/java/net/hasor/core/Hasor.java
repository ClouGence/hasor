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
package net.hasor.core;
import net.hasor.core.context.ContextData;
import net.hasor.core.context.TemplateAppContext;
import net.hasor.core.environment.StandardEnvironment;
/**
 * Hasor 基础工具包。
 * @version : 2013-4-3
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class Hasor {
    public static <T extends EventListener> T pushStartListener(EventContext env, T eventListener) {
        env.pushListener(EventContext.ContextEvent_Started, eventListener);
        return eventListener;
    }
    public static <T extends EventListener> T addStartListener(EventContext env, T eventListener) {
        env.addListener(EventContext.ContextEvent_Started, eventListener);
        return eventListener;
    }
    public static <T extends EventListener> T addShutdownListener(EventContext env, T eventListener) {
        env.addListener(EventContext.ContextEvent_Shutdown, eventListener);
        return eventListener;
    }
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext() {
        return Hasor.createAppContext(TemplateAppContext.DefaultSettings, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final Module... modules) {
        return Hasor.createAppContext(TemplateAppContext.DefaultSettings, modules);
    }
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config) {
        return Hasor.createAppContext(config, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config, final Module... modules) {
        try {
            final Environment dev = new StandardEnvironment(config);
            final ContextData contextData = new ContextData() {
                public Environment getEnvironment() {
                    return dev;
                }
            };
            final AppContext appContext = new TemplateAppContext() {
                protected ContextData getContextData() {
                    return contextData;
                }
            };
            appContext.start(modules);
            return appContext;
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
    //
    /*---------------------------------------------------------------------------------------Util*/
    /**如果参数为空会抛出 NullPointerException 异常。*/
    public static <T> T assertIsNotNull(final T object) {
        return Hasor.assertIsNotNull(object, ""); //$NON-NLS-1$
    }
    /**如果参数为空会抛出 NullPointerException 异常。*/
    public static <T> T assertIsNotNull(final T object, final String message) {
        if (object == null) {
            throw new NullPointerException("null argument:" + message); //$NON-NLS-1$
        }
        return object;
    }
}