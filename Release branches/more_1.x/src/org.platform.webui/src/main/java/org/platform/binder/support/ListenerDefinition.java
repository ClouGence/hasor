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
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.platform.Platform;
import org.platform.context.AppContext;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ListenerDefinition implements Provider<ListenerDefinition> {
    private Key<? extends HttpSessionListener> listenerKey      = null;
    private HttpSessionListener                listenerInstance = null;
    //
    //
    public ListenerDefinition(Key<? extends HttpSessionListener> listenerKey, HttpSessionListener listenerInstance) {
        this.listenerKey = listenerKey;
        this.listenerInstance = listenerInstance;
    }
    //
    @Override
    public ListenerDefinition get() {
        return this;
    }
    protected HttpSessionListener getTarget(Injector injector) {
        if (this.listenerInstance == null)
            this.listenerInstance = injector.getInstance(this.listenerKey);
        return this.listenerInstance;
    }
    @Override
    public String toString() {
        return Platform.formatString("type %s listenerKey=%s",//
                ListenerDefinition.class, this.listenerKey);
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void sessionCreated(AppContext appContext, HttpSessionEvent event) {
        HttpSessionListener httpSessionListener = this.getTarget(appContext.getGuice());
        if (httpSessionListener != null)
            httpSessionListener.sessionCreated(event);
    }
    /**/
    public void sessionDestroyed(AppContext appContext, HttpSessionEvent event) {
        HttpSessionListener httpSessionListener = this.getTarget(appContext.getGuice());
        if (httpSessionListener != null)
            httpSessionListener.sessionDestroyed(event);
    }
}