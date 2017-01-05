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

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;
import java.util.List;
/**
 *
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ManagedListenerPipeline implements ListenerPipeline {
    private          HttpSessionListenerDefinition[] sessionListeners = null;
    private          ContextListenerDefinition[]     contextListeners = null;
    private volatile boolean                         initialized      = false;
    //
    //
    @Override
    public void init(final AppContext appContext) {
        if (this.initialized) {
            return;
        }
        //1.收集HttpSessionListenerDefinition
        List<HttpSessionListenerDefinition> sessionListeners = appContext.findBindingBean(HttpSessionListenerDefinition.class);
        this.sessionListeners = sessionListeners.toArray(new HttpSessionListenerDefinition[sessionListeners.size()]);
        for (HttpSessionListenerDefinition def : this.sessionListeners) {
            def.init(appContext);
        }
        //2.收集ContextListenerDefinition
        List<ContextListenerDefinition> contextListeners = appContext.findBindingBean(ContextListenerDefinition.class);
        this.contextListeners = contextListeners.toArray(new ContextListenerDefinition[contextListeners.size()]);
        for (ContextListenerDefinition def : this.contextListeners) {
            def.init(appContext);
        }
        //everything was ok...
        this.initialized = true;
    }
    @Override
    public void sessionCreated(final HttpSessionEvent event) {
        if (!this.initialized) {
            return;
        }
        for (HttpSessionListenerDefinition httpSessionListenerDefinition : this.sessionListeners) {
            httpSessionListenerDefinition.sessionCreated(event);
        }
    }
    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        if (!this.initialized) {
            return;
        }
        for (HttpSessionListenerDefinition httpSessionListenerDefinition : this.sessionListeners) {
            httpSessionListenerDefinition.sessionDestroyed(event);
        }
    }
    @Override
    public void contextInitialized(final ServletContextEvent event) {
        if (!this.initialized) {
            return;
        }
        for (ContextListenerDefinition contextListenerDefinition : this.contextListeners) {
            contextListenerDefinition.contextInitialized(event);
        }
    }
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        if (!this.initialized) {
            return;
        }
        for (ContextListenerDefinition contextListenerDefinition : this.contextListeners) {
            contextListenerDefinition.contextDestroyed(event);
        }
    }
}