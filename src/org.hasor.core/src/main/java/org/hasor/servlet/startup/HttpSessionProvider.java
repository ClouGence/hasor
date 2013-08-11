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
package org.hasor.servlet.startup;
import javax.servlet.http.HttpSession;
import com.google.inject.Provider;
/** 
 *  
 * @version : 2013-8-11 
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class HttpSessionProvider implements Provider<HttpSession> {
    private static HttpSessionProvider sessionProvider = null;
    private ThreadLocal<HttpSession>   session         = new ThreadLocal<HttpSession>();
    //
    public static HttpSessionProvider getProvider() {
        if (sessionProvider == null)
            sessionProvider = new HttpSessionProvider();
        return sessionProvider;
    }
    @Override
    public synchronized HttpSession get() {
        return this.session.get();
    }
    public synchronized void update(HttpSession session) {
        if (this.session.get() != null)
            this.reset();
        this.session.set(session);
    }
    public synchronized void reset() {
        this.session.remove();
    }
}