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
import javax.inject.Provider;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class HttpSessionListenerDefinition implements Provider<HttpSessionListenerDefinition> {
    private Provider<HttpSessionListener> listenerProvider = null;
    private HttpSessionListener           listenerInstance = null;
    //
    public HttpSessionListenerDefinition(Provider<HttpSessionListener> listenerProvider) {
        this.listenerProvider = listenerProvider;
    }
    //
    public HttpSessionListenerDefinition get() {
        return this;
    }
    protected HttpSessionListener getTarget() {
        if (this.listenerInstance == null)
            this.listenerInstance = listenerProvider.get();
        return this.listenerInstance;
    }
    public String toString() {
        return String.format("type %s listenerKey=%s",//
                HttpSessionListenerDefinition.class, this.listenerInstance);
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void sessionCreated(HttpSessionEvent event) {
        HttpSessionListener httpSessionListener = this.getTarget();
        if (httpSessionListener != null)
            httpSessionListener.sessionCreated(event);
    }
    /**/
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSessionListener httpSessionListener = this.getTarget();
        if (httpSessionListener != null)
            httpSessionListener.sessionDestroyed(event);
    }
}