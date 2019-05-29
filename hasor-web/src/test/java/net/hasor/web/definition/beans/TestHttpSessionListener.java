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
package net.hasor.web.definition.beans;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestHttpSessionListener implements HttpSessionListener {
    private static AtomicBoolean sessionCreatedCall   = new AtomicBoolean(false);
    private static AtomicBoolean sessionDestroyedCall = new AtomicBoolean(false);
    //
    public static boolean isSessionCreatedCallCall() {
        return sessionCreatedCall.get();
    }
    public static boolean issSessionDestroyedCallCall() {
        return sessionDestroyedCall.get();
    }
    public static void resetCalls() {
        sessionCreatedCall.set(false);
        sessionDestroyedCall.set(false);
    }
    //
    @Override
    public void sessionCreated(HttpSessionEvent sce) {
        sessionCreatedCall.set(true);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent sce) {
        sessionDestroyedCall.set(true);
    }
}
