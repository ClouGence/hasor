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
package net.hasor.web.listener;
import net.hasor.core.AppContext;
import net.hasor.web.definition.WebListenerDefinition;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ManagedListenerPipeline implements ListenerPipeline {
    private          Map<Class<?>, ArrayList<WebListenerDefinition>> webListeners = null;
    private volatile boolean                                         initialized  = false;
    //
    //
    @Override
    public void init(final AppContext appContext) {
        if (this.initialized) {
            return;
        }
        //
        // 收集各类 WebListener
        this.webListeners = new HashMap<Class<?>, ArrayList<WebListenerDefinition>>() {{
            put(HttpSessionListener.class, new ArrayList<>());
            put(ServletContextListener.class, new ArrayList<>());
            put(ServletRequestListener.class, new ArrayList<>());
        }};
        List<WebListenerDefinition> listenerList = appContext.findBindingBean(WebListenerDefinition.class);
        listenerList.forEach(listenerDefinition -> {
            // .ServletContextListener
            if (listenerDefinition.getWebListener(ServletContextListener.class) != null) {
                webListeners.get(ServletContextListener.class).add(listenerDefinition);
            }
            // .HttpSessionListener
            if (listenerDefinition.getWebListener(HttpSessionListener.class) != null) {
                webListeners.get(HttpSessionListener.class).add(listenerDefinition);
            }
            // .ServletRequestListener
            if (listenerDefinition.getWebListener(ServletRequestListener.class) != null) {
                webListeners.get(ServletRequestListener.class).add(listenerDefinition);
            }
        });
        //everything was ok...
        this.initialized = true;
    }
    //
    //
    @Override
    public void contextInitialized(final ServletContextEvent event) {
        if (!this.initialized) {
            return;
        }
        for (WebListenerDefinition contextListenerDefinition : this.webListeners.get(ServletContextListener.class)) {
            contextListenerDefinition.getWebListener(ServletContextListener.class).contextInitialized(event);
        }
    }
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        if (!this.initialized) {
            return;
        }
        for (WebListenerDefinition contextListenerDefinition : this.webListeners.get(ServletContextListener.class)) {
            contextListenerDefinition.getWebListener(ServletContextListener.class).contextDestroyed(event);
        }
    }
    @Override
    public void sessionCreated(final HttpSessionEvent event) {
        if (!this.initialized) {
            return;
        }
        for (WebListenerDefinition contextListenerDefinition : this.webListeners.get(HttpSessionListener.class)) {
            contextListenerDefinition.getWebListener(HttpSessionListener.class).sessionCreated(event);
        }
    }
    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        if (!this.initialized) {
            return;
        }
        for (WebListenerDefinition listener : this.webListeners.get(HttpSessionListener.class)) {
            listener.getWebListener(HttpSessionListener.class).sessionDestroyed(event);
        }
    }
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        if (!this.initialized) {
            return;
        }
        for (WebListenerDefinition listener : this.webListeners.get(ServletRequestListener.class)) {
            listener.getWebListener(ServletRequestListener.class).requestDestroyed(sre);
        }
    }
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        if (!this.initialized) {
            return;
        }
        for (WebListenerDefinition listener : this.webListeners.get(ServletRequestListener.class)) {
            listener.getWebListener(ServletRequestListener.class).requestInitialized(sre);
        }
    }
}