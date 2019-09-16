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
import net.hasor.core.AppContext;
import net.hasor.web.AbstractTest;
import net.hasor.web.ServletVersion;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class RuntimeListenerTest extends AbstractTest {
    @Test
    public void basic_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-root-module", StartModule.class.getName());
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        RuntimeListener listener = new RuntimeListener();
        assert listener.newRootModule(servletContext, null) == null;
        //
        assert listener.loadEnvProperties(null, null) == null;
        assert listener.loadEnvProperties(servletContext, null) == null;
        assert listener.loadEnvProperties(servletContext, "abc.abc") == null;
        //
        Properties properties = listener.loadEnvProperties(servletContext, "/net_hasor_web_startup/data-config.properties");
        assert properties != null;
        assert properties.getProperty("mySelf.myBirthday").equals("1986-01-01 00:00:00");
    }

    @Test
    public void params_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-root-module", StartModule.class.getName());
        init_params.put("hasor-hconfig-name", "/net_hasor_web_startup/data-config.properties");
        init_params.put("hasor-envconfig-name", "/net_hasor_web_startup/data-config.properties");
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        //
        RuntimeListener listener = new RuntimeListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
        //
        AppContext appContext = RuntimeListener.getAppContext(servletContext);
        assert appContext.getInstance(ServletVersion.class).eq(ServletVersion.V2_5);
        //
        List instance = appContext.getInstance(List.class);
        assert instance != null && !instance.isEmpty();
        assert instance.get(0).equals("HelloWord");
        //
        assert appContext.getEnvironment().getSettings().getString("mySelf.myBirthday").equals("1986-01-01 00:00:00");
        assert appContext.getEnvironment().getVariable("mySelf.myBirthday").equals("1986-01-01 00:00:00");
    }

    @Test
    public void shutdown_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        RuntimeListener listener = new RuntimeListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
        //
        AppContext appContext = RuntimeListener.getAppContext(servletContext);
        assert appContext.isStart();
        //
        listener.contextDestroyed(new ServletContextEvent(servletContext));
        assert !appContext.isStart();
    }

    @Test
    public void spi_test() throws Throwable {
        HashMap<String, String> init_params = new HashMap<>();
        init_params.put("hasor-root-module", WebSpiTest.class.getName());
        ServletContext servletContext = servletInitParams(servlet25("/"), init_params);
        RuntimeListener listener = new RuntimeListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
        assert WebSpiTest.spiCall.size() == 1;
        assert WebSpiTest.spiCall.contains("ServletContextListener.contextInitialized");
        //
        listener.sessionCreated(PowerMockito.mock(HttpSessionEvent.class));
        assert WebSpiTest.spiCall.size() == 2;
        assert WebSpiTest.spiCall.contains("HttpSessionListener.sessionCreated");
        listener.sessionDestroyed(PowerMockito.mock(HttpSessionEvent.class));
        assert WebSpiTest.spiCall.size() == 3;
        assert WebSpiTest.spiCall.contains("HttpSessionListener.sessionDestroyed");
        //
        listener.requestInitialized(PowerMockito.mock(ServletRequestEvent.class));
        assert WebSpiTest.spiCall.size() == 4;
        assert WebSpiTest.spiCall.contains("ServletRequestListener.requestInitialized");
        listener.requestDestroyed(PowerMockito.mock(ServletRequestEvent.class));
        assert WebSpiTest.spiCall.size() == 5;
        assert WebSpiTest.spiCall.contains("ServletRequestListener.requestDestroyed");
        //
        listener.contextDestroyed(new ServletContextEvent(servletContext));
        assert WebSpiTest.spiCall.size() == 6;
        assert WebSpiTest.spiCall.contains("ServletContextListener.contextDestroyed");
        AppContext appContext = RuntimeListener.getAppContext(servletContext);
        assert !appContext.isStart();
    }
}