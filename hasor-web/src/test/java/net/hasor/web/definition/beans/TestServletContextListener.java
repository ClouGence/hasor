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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class TestServletContextListener implements ServletContextListener {
    private static AtomicBoolean contextInitializedCall = new AtomicBoolean(false);
    private static AtomicBoolean contextDestroyedCall   = new AtomicBoolean(false);
    //
    public static boolean isContextInitializedCall() {
        return contextInitializedCall.get();
    }
    public static boolean isContextDestroyedCall() {
        return contextDestroyedCall.get();
    }
    public static void resetCalls() {
        contextInitializedCall.set(false);
        contextDestroyedCall.set(false);
    }
    //
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        contextInitializedCall.set(true);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        contextDestroyedCall.set(true);
    }
}
