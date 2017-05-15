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
import net.hasor.core.BindInfo;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
/**
 *
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class HttpSessionListenerDefinition implements HttpSessionListener {
    private BindInfo<? extends HttpSessionListener> listenerRegister = null;
    private HttpSessionListener                     listenerInstance = null;
    private AppContext                              appContext       = null;
    //
    public HttpSessionListenerDefinition(final BindInfo<? extends HttpSessionListener> listenerRegister) {
        this.listenerRegister = listenerRegister;
    }
    //
    protected HttpSessionListener getTarget() {
        if (this.listenerInstance == null) {
            this.listenerInstance = this.appContext.getInstance(this.listenerRegister);
        }
        return this.listenerInstance;
    }
    @Override
    public String toString() {
        return String.format("type %s listenerKey=%s", HttpSessionListenerDefinition.class, this.listenerInstance);
    }
    /**/
    public void init(final AppContext appContext) {
        this.appContext = appContext;
        this.getTarget();
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void sessionCreated(final HttpSessionEvent event) {
        HttpSessionListener httpSessionListener = this.getTarget();
        if (httpSessionListener != null) {
            httpSessionListener.sessionCreated(event);
        }
    }
    /**/
    public void sessionDestroyed(final HttpSessionEvent event) {
        HttpSessionListener httpSessionListener = this.getTarget();
        if (httpSessionListener != null) {
            httpSessionListener.sessionDestroyed(event);
        }
    }
}