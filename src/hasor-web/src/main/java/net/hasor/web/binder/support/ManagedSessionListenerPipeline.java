/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;
import net.hasor.core.AppContext;
import net.hasor.web.binder.SessionListenerPipeline;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 * 
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@Singleton
public class ManagedSessionListenerPipeline implements SessionListenerPipeline {
    private HttpSessionListenerDefinition[] sessionListeners = null;
    private ContextListenerDefinition[]     contextListeners = null;
    private volatile boolean                initialized      = false;
    private AppContext                      appContext       = null;
    //
    //
    public void init(AppContext appContext) {
        if (initialized)
            return;
        this.appContext = appContext;
        this.sessionListeners = collectListenerDefinitions(appContext.getGuice());
        this.contextListeners = collectContextListenerDefinitions(appContext.getGuice());
        //everything was ok...
        this.initialized = true;
    }
    private HttpSessionListenerDefinition[] collectListenerDefinitions(Injector injector) {
        List<HttpSessionListenerDefinition> sessionListeners = new ArrayList<HttpSessionListenerDefinition>();
        TypeLiteral<HttpSessionListenerDefinition> LISTENER_DEFS = TypeLiteral.get(HttpSessionListenerDefinition.class);
        for (Binding<HttpSessionListenerDefinition> entry : injector.findBindingsByType(LISTENER_DEFS)) {
            sessionListeners.add(entry.getProvider().get());
        }
        // Convert to a fixed size array for speed.
        return sessionListeners.toArray(new HttpSessionListenerDefinition[sessionListeners.size()]);
    }
    private ContextListenerDefinition[] collectContextListenerDefinitions(Injector injector) {
        List<ContextListenerDefinition> contextListeners = new ArrayList<ContextListenerDefinition>();
        TypeLiteral<ContextListenerDefinition> LISTENER_DEFS = TypeLiteral.get(ContextListenerDefinition.class);
        for (Binding<ContextListenerDefinition> entry : injector.findBindingsByType(LISTENER_DEFS)) {
            contextListeners.add(entry.getProvider().get());
        }
        // Convert to a fixed size array for speed.
        return contextListeners.toArray(new ContextListenerDefinition[contextListeners.size()]);
    }
    public void sessionCreated(HttpSessionEvent event) {
        if (initialized == false)
            return;
        for (HttpSessionListenerDefinition httpSessionListenerDefinition : sessionListeners) {
            httpSessionListenerDefinition.sessionCreated(this.appContext, event);
        }
    }
    public void sessionDestroyed(HttpSessionEvent event) {
        if (initialized == false)
            return;
        for (HttpSessionListenerDefinition httpSessionListenerDefinition : sessionListeners) {
            httpSessionListenerDefinition.sessionDestroyed(this.appContext, event);
        }
    }
    public void contextInitialized(ServletContextEvent event) {
        if (initialized == false)
            return;
        for (ContextListenerDefinition contextListenerDefinition : contextListeners) {
            contextListenerDefinition.contextInitialized(this.appContext, event);
        }
    }
    public void contextDestroyed(ServletContextEvent event) {
        if (initialized == false)
            return;
        for (ContextListenerDefinition contextListenerDefinition : contextListeners) {
            contextListenerDefinition.contextDestroyed(this.appContext, event);
        }
    }
}