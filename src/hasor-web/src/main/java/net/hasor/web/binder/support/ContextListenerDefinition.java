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
import net.hasor.core.BindInfo;
import net.hasor.web.WebAppContext;
/**
 * 
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
class ContextListenerDefinition {
    private BindInfo<ServletContextListener> listenerRegister = null;
    private ServletContextListener               listenerInstance = null;
    private WebAppContext                        appContext       = null;
    //
    //
    public ContextListenerDefinition(final BindInfo<ServletContextListener> listenerRegister) {
        this.listenerRegister = listenerRegister;
    }
    //
    protected ServletContextListener getTarget() {
        if (this.listenerInstance == null) {
            this.listenerInstance = this.appContext.getInstance(this.listenerRegister);
        }
        return this.listenerInstance;
    }
    @Override
    public String toString() {
        return String.format("type %s listenerKey=%s",//
                ContextListenerDefinition.class, this.listenerInstance);
    }
    /**/
    public void init(final WebAppContext appContext) {
        this.appContext = appContext;
        this.getTarget();
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void contextInitialized(final ServletContextEvent event) {
        ServletContextListener servletContextListener = this.getTarget();
        if (servletContextListener != null) {
            servletContextListener.contextInitialized(event);
        }
    }
    /**/
    public void contextDestroyed(final ServletContextEvent event) {
        ServletContextListener servletContextListener = this.getTarget();
        if (servletContextListener != null) {
            servletContextListener.contextDestroyed(event);
        }
    }
}