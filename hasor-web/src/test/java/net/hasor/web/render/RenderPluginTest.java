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
package net.hasor.web.render;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.RenderEngine;
import net.hasor.web.WebModule;
import net.hasor.web.definition.FilterDefinition;
import net.hasor.test.beans.TestRenderEngine;
import net.hasor.web.invoker.AbstractWeb30BinderDataTest;
import net.hasor.web.invoker.ExceuteCaller;
import net.hasor.web.invoker.InMappingDef;
import net.hasor.web.invoker.InvokerContext;
import net.hasor.test._.HtmlProduces;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

import static org.mockito.Matchers.anyString;
//
public class RenderPluginTest extends AbstractWeb30BinderDataTest {
    @Before
    public void beforeTest() {
        loadInvokerSet.add(LoadExtEnum.Render);
        super.beforeTest();
    }
    @Test
    public void chainTest1() throws Throwable {
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.installModule(new RenderWebPlugin());
            apiBinder.addRender("htm").toInstance(PowerMockito.mock(RenderEngine.class));
        });
        //
        List<BindInfo<FilterDefinition>> register = appContext.findBindingRegister(FilterDefinition.class);
        assert register.size() == 1;
        //
        FilterDefinition instance = appContext.getInstance(register.get(0));
        assert instance instanceof InvokeFilterDefinition;
        InvokeFilterDefinition definition = (InvokeFilterDefinition) instance;
        assert definition.getIndex() == Integer.MIN_VALUE;
    }
    @Test
    public void chainTest2() throws Throwable {
        //
        RenderEngine engine = PowerMockito.mock(RenderEngine.class);
        //
        hasor.addVariable("HASOR_RESTFUL_LAYOUT", "true");
        hasor.addVariable("HASOR_RESTFUL_LAYOUT_PATH_LAYOUT", "/layout/mytest");
        hasor.addVariable("HASOR_RESTFUL_LAYOUT_PATH_TEMPLATES", "/templates/myfiles");
        //
        RenderWebPlugin webPlugin = new RenderWebPlugin();
        AppContext appContext = hasor.mainSettingWith("META-INF/hasor-framework/web-hconfig.xml").build((WebModule) apiBinder -> {
            apiBinder.installModule(webPlugin);
            apiBinder.addRender("htm").toInstance(engine);
        });
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<>());
        //
        Field layoutPathField = RenderWebPlugin.class.getDeclaredField("layoutPath");
        Field useLayoutField = RenderWebPlugin.class.getDeclaredField("useLayout");
        Field templatePathField = RenderWebPlugin.class.getDeclaredField("templatePath");
        Field engineMapField = RenderWebPlugin.class.getDeclaredField("engineMap");
        layoutPathField.setAccessible(true);
        useLayoutField.setAccessible(true);
        templatePathField.setAccessible(true);
        engineMapField.setAccessible(true);
        //
        String layoutPath = (String) layoutPathField.get(webPlugin);
        boolean useLayout = (boolean) useLayoutField.get(webPlugin);
        String templatePath = (String) templatePathField.get(webPlugin);
        Map<String, RenderEngine> engineMap = (Map<String, RenderEngine>) engineMapField.get(webPlugin);
        //
        assert "/layout/mytest".equals(layoutPath);
        assert useLayout;
        assert "/templates/myfiles".equals(templatePath);
        assert engineMap.size() == 1;
        assert engineMap.get("HTM") == engine;
    }
    @Test
    public void chainTest3() throws Throwable {
        TestRenderEngine.resetCalls();
        RenderEngine engine = new TestRenderEngine();
        //
        hasor.addVariable("HASOR_RESTFUL_LAYOUT", "true");
        hasor.addVariable("HASOR_RESTFUL_LAYOUT_PATH_LAYOUT", "/layout/mytest");
        hasor.addVariable("HASOR_RESTFUL_LAYOUT_PATH_TEMPLATES", "/templates/myfiles");
        //
        RenderWebPlugin webPlugin = new RenderWebPlugin();
        AppContext appContext = hasor.mainSettingWith("META-INF/hasor-framework/web-hconfig.xml").build((WebModule) apiBinder -> {
            apiBinder.installModule(webPlugin);
            apiBinder.addRender("htm").toInstance(engine);
        });
        //
        //
        HttpServletRequest request = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"), appContext);
        //
        assert !TestRenderEngine.isInitEngineCall();
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<>());
        //
        ExceuteCaller caller = invokerContext.genCaller(request, PowerMockito.mock(HttpServletResponse.class));
        caller.invoke(null);
        //
        invokerContext.destroyContext();
        assert TestRenderEngine.isInitEngineCall();
    }
    @Test
    public void chainTest4() throws Throwable {
        TestRenderEngine.resetCalls();
        RenderEngine engine = new TestRenderEngine();
        //
        hasor.addVariable("HASOR_RESTFUL_LAYOUT", "true");
        //
        RenderWebPlugin webPlugin = new RenderWebPlugin();
        AppContext appContext = hasor.mainSettingWith("META-INF/hasor-framework/web-hconfig.xml").build((WebModule) apiBinder -> {
            apiBinder.installModule(webPlugin);
            apiBinder.addRender("htm").toInstance(engine);
            apiBinder.loadMappingTo(HtmlProduces.class);
            apiBinder.addMimeType("html", "test/html");
        });
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        assert definitions.size() == 2;
        //
        //
        final Set<String> responseType = new HashSet<>();
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        PowerMockito.doAnswer((Answer<Void>) invocation -> {
            responseType.add(invocation.getArguments()[0].toString());
            return null;
        }).when(servletResponse).setContentType(anyString());
        //
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<>());
        {
            ExceuteCaller caller = invokerContext.genCaller(mockRequest("post", new URL("http://www.hasor.net/abc.do"), appContext), servletResponse);
            caller.invoke(null);
            assert responseType.contains("test/html");
        }
        //
        {
            responseType.clear();
            ExceuteCaller caller = invokerContext.genCaller(mockRequest("get", new URL("http://www.hasor.net/abc.do"), appContext), servletResponse);
            caller.invoke(null);
            assert responseType.size() == 0;
        }
    }
}