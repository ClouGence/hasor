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
package net.hasor.web.binder.support;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;
import net.hasor.web.WebAppContext;
import net.hasor.web.binder.ListenerPipeline;
/**
 * 
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class ManagedListenerPipeline implements ListenerPipeline {
    private HttpSessionListenerDefinition[] sessionListeners = null;
    private ContextListenerDefinition[]     contextListeners = null;
    private volatile boolean                initialized      = false;
    //
    //
    public void init(WebAppContext appContext) {
        if (initialized)
            return;
        //1.收集HttpSessionListenerDefinition
        List<HttpSessionListenerDefinition> sessionListeners = appContext.findBindingBean(HttpSessionListenerDefinition.class);
        this.sessionListeners = sessionListeners.toArray(new HttpSessionListenerDefinition[sessionListeners.size()]);
        //2.收集ContextListenerDefinition
        List<ContextListenerDefinition> contextListeners = appContext.findBindingBean(ContextListenerDefinition.class);
        this.contextListeners = contextListeners.toArray(new ContextListenerDefinition[contextListeners.size()]);
        //everything was ok...
        this.initialized = true;
    }
    public void sessionCreated(HttpSessionEvent event) {
        if (initialized == false)
            return;
        for (HttpSessionListenerDefinition httpSessionListenerDefinition : this.sessionListeners) {
            httpSessionListenerDefinition.sessionCreated(event);
        }
    }
    public void sessionDestroyed(HttpSessionEvent event) {
        if (initialized == false)
            return;
        for (HttpSessionListenerDefinition httpSessionListenerDefinition : this.sessionListeners) {
            httpSessionListenerDefinition.sessionDestroyed(event);
        }
    }
    public void contextInitialized(ServletContextEvent event) {
        if (initialized == false)
            return;
        for (ContextListenerDefinition contextListenerDefinition : this.contextListeners) {
            contextListenerDefinition.contextInitialized(event);
        }
    }
    public void contextDestroyed(ServletContextEvent event) {
        if (initialized == false)
            return;
        for (ContextListenerDefinition contextListenerDefinition : this.contextListeners) {
            contextListenerDefinition.contextDestroyed(event);
        }
    }
}