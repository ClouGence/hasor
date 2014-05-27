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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.hasor.core.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
class ContextListenerDefinition implements Provider<ContextListenerDefinition> {
    private Provider<ServletContextListener> listenerProvider = null;
    private ServletContextListener           listenerInstance = null;
    //
    //
    public ContextListenerDefinition(Provider<ServletContextListener> listenerProvider) {
        this.listenerProvider = listenerProvider;
    }
    //
    public ContextListenerDefinition get() {
        return this;
    }
    protected ServletContextListener getTarget() {
        if (this.listenerInstance == null)
            this.listenerInstance = listenerProvider.get();
        return this.listenerInstance;
    }
    public String toString() {
        return String.format("type %s listenerKey=%s",//
                ContextListenerDefinition.class, this.listenerInstance);
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void contextInitialized(ServletContextEvent event) {
        ServletContextListener servletContextListener = this.getTarget();
        if (servletContextListener != null)
            servletContextListener.contextInitialized(event);
    }
    /**/
    public void contextDestroyed(ServletContextEvent event) {
        ServletContextListener servletContextListener = this.getTarget();
        if (servletContextListener != null)
            servletContextListener.contextDestroyed(event);
    }
}