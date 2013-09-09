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
package net.hasor.servlet.binder.support;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.hasor.core.AppContext;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class ContextListenerDefinition implements Provider<ContextListenerDefinition> {
    private Key<? extends ServletContextListener> listenerKey      = null;
    private ServletContextListener                listenerInstance = null;
    //
    //
    public ContextListenerDefinition(Key<? extends ServletContextListener> listenerKey, ServletContextListener listenerInstance) {
        this.listenerKey = listenerKey;
        this.listenerInstance = listenerInstance;
    }
    //
    public ContextListenerDefinition get() {
        return this;
    }
    protected ServletContextListener getTarget(Injector injector) {
        if (this.listenerInstance == null)
            this.listenerInstance = injector.getInstance(this.listenerKey);
        return this.listenerInstance;
    }
    public String toString() {
        return String.format("type %s listenerKey=%s",//
                ContextListenerDefinition.class, this.listenerKey);
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void contextInitialized(AppContext appContext, ServletContextEvent event) {
        ServletContextListener servletContextListener = this.getTarget(appContext.getGuice());
        if (servletContextListener != null)
            servletContextListener.contextInitialized(event);
    }
    /**/
    public void contextDestroyed(AppContext appContext, ServletContextEvent event) {
        ServletContextListener servletContextListener = this.getTarget(appContext.getGuice());
        if (servletContextListener != null)
            servletContextListener.contextDestroyed(event);
    }
}