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
package net.hasor.web.startup;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebSpiTest implements Module {
    public static Set<String> spiCall = new HashSet<>();

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindSpiListener(ServletContextListener.class, new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                spiCall.add("ServletContextListener.contextInitialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                spiCall.add("ServletContextListener.contextDestroyed");
            }
        });
        //
        apiBinder.bindSpiListener(HttpSessionListener.class, new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent sce) {
                spiCall.add("HttpSessionListener.sessionCreated");
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent sce) {
                spiCall.add("HttpSessionListener.sessionDestroyed");
            }
        });
        //
        apiBinder.bindSpiListener(ServletRequestListener.class, new ServletRequestListener() {
            @Override
            public void requestInitialized(ServletRequestEvent sce) {
                spiCall.add("ServletRequestListener.requestInitialized");
            }

            @Override
            public void requestDestroyed(ServletRequestEvent sce) {
                spiCall.add("ServletRequestListener.requestDestroyed");
            }
        });
        //
    }
}