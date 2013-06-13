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
package org.platform.binder.support;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.servlet.http.HttpSessionEvent;
import org.platform.binder.SessionListenerPipeline;
import org.platform.context.AppContext;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
/**
 * 
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Singleton
class ManagedSessionListenerPipeline implements SessionListenerPipeline {
    private ListenerDefinition[] sessionListeners = null;
    private volatile boolean     initialized      = false;
    private AppContext           appContext       = null;
    //
    //
    @Override
    public void init(AppContext appContext) {
        if (initialized)
            return;
        this.appContext = appContext;
        this.sessionListeners = collectListenerDefinitions(appContext.getGuice());
        //everything was ok...
        this.initialized = true;
    }
    private ListenerDefinition[] collectListenerDefinitions(Injector injector) {
        List<ListenerDefinition> sessionListeners = new ArrayList<ListenerDefinition>();
        TypeLiteral<ListenerDefinition> LISTENER_DEFS = TypeLiteral.get(ListenerDefinition.class);
        for (Binding<ListenerDefinition> entry : injector.findBindingsByType(LISTENER_DEFS)) {
            sessionListeners.add(entry.getProvider().get());
        }
        // Convert to a fixed size array for speed.
        return sessionListeners.toArray(new ListenerDefinition[sessionListeners.size()]);
    }
    public void sessionCreated(HttpSessionEvent event) {
        if (initialized == false)
            return;
        for (ListenerDefinition listenerDefinition : sessionListeners) {
            listenerDefinition.sessionCreated(this.appContext, event);
        }
    }
    public void sessionDestroyed(HttpSessionEvent event) {
        if (initialized == false)
            return;
        for (ListenerDefinition listenerDefinition : sessionListeners) {
            listenerDefinition.sessionDestroyed(this.appContext, event);
        }
    }
}