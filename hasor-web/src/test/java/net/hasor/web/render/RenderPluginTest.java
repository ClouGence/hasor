package net.hasor.web.render;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerData;
import net.hasor.web.RenderEngine;
import net.hasor.web.WebModule;
import net.hasor.web.definition.AbstractDefinition;
import net.hasor.web.definition.InvokeFilterDefinition;
import net.hasor.web.definition.beans.TestRenderEngine;
import net.hasor.web.invoker.AbstractWeb30BinderDataTest;
import net.hasor.web.invoker.InMappingDef;
import net.hasor.web.invoker.InvokerContext;
import net.hasor.web.render.produces.HtmlProduces;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
            apiBinder.suffix("htm").bind(PowerMockito.mock(RenderEngine.class));
        });
        //
        List<BindInfo<AbstractDefinition>> register = appContext.findBindingRegister(AbstractDefinition.class);
        assert register.size() == 1;
        //
        AbstractDefinition instance = appContext.getInstance(register.get(0));
        assert instance instanceof InvokeFilterDefinition;
        InvokeFilterDefinition definition = (InvokeFilterDefinition) instance;
        assert definition.getIndex() == Integer.MAX_VALUE;
    }
    @Test
    public void chainTest2() throws Throwable {
        //
        RenderEngine engine = PowerMockito.mock(RenderEngine.class);
        //
        hasor.putData("HASOR_RESTFUL_LAYOUT", "true");
        hasor.putData("HASOR_RESTFUL_LAYOUT_PATH_LAYOUT", "/layout/mytest");
        hasor.putData("HASOR_RESTFUL_LAYOUT_PATH_TEMPLATES", "/templates/myfiles");
        //
        RenderWebPlugin webPlugin = new RenderWebPlugin();
        AppContext appContext = hasor.setMainSettings("META-INF/hasor-framework/web-hconfig.xml").build((WebModule) apiBinder -> {
            apiBinder.installModule(webPlugin);
            apiBinder.suffix("htm").bind(engine);
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
        hasor.putData("HASOR_RESTFUL_LAYOUT", "true");
        hasor.putData("HASOR_RESTFUL_LAYOUT_PATH_LAYOUT", "/layout/mytest");
        hasor.putData("HASOR_RESTFUL_LAYOUT_PATH_TEMPLATES", "/templates/myfiles");
        //
        RenderWebPlugin webPlugin = new RenderWebPlugin();
        AppContext appContext = hasor.setMainSettings("META-INF/hasor-framework/web-hconfig.xml").build((WebModule) apiBinder -> {
            apiBinder.installModule(webPlugin);
            apiBinder.suffix("htm").bind(engine);
        });
        //
        //
        Invoker mockInvoker = newInvoker(mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"), appContext), appContext);
        InvokerData data = PowerMockito.mock(InvokerData.class);
        //
        assert !TestRenderEngine.isInitEngineCall();
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<>());
        assert TestRenderEngine.isInitEngineCall();
        //
        //
        Invoker invoker = invokerContext.newInvoker(mockInvoker.getHttpRequest(), mockInvoker.getHttpResponse());
        invokerContext.beforeFilter(invoker, data);
        invokerContext.afterFilter(invoker, data);
        //
        invokerContext.destroyContext();
    }
    @Test
    public void chainTest4() throws Throwable {
        TestRenderEngine.resetCalls();
        RenderEngine engine = new TestRenderEngine();
        //
        hasor.putData("HASOR_RESTFUL_LAYOUT", "true");
        //
        RenderWebPlugin webPlugin = new RenderWebPlugin();
        AppContext appContext = hasor.setMainSettings("META-INF/hasor-framework/web-hconfig.xml").build((WebModule) apiBinder -> {
            apiBinder.installModule(webPlugin);
            apiBinder.suffix("htm").bind(engine);
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
            Invoker mockInvoker = newInvoker(mockRequest("post", new URL("http://www.hasor.net/abc.do"), appContext), servletResponse, appContext);
            Invoker invoker = invokerContext.newInvoker(mockInvoker.getHttpRequest(), mockInvoker.getHttpResponse());
            //
            InvokerData data = PowerMockito.mock(InvokerData.class);
            Method targetMethod = definitions.get(0).findMethod(invoker);
            PowerMockito.when(data.targetMethod()).thenReturn(targetMethod);
            //
            invokerContext.beforeFilter(invoker, data);
            assert responseType.contains("test/html");
        }
        //
        {
            responseType.clear();
            Invoker mockInvoker = newInvoker(mockRequest("get", new URL("http://www.hasor.net/abc.do"), appContext), servletResponse, appContext);
            Invoker invoker = invokerContext.newInvoker(mockInvoker.getHttpRequest(), mockInvoker.getHttpResponse());
            //
            InvokerData data = PowerMockito.mock(InvokerData.class);
            Method targetMethod = definitions.get(0).findMethod(invoker);
            PowerMockito.when(data.targetMethod()).thenReturn(targetMethod);
            //
            invokerContext.beforeFilter(invoker, data);
            assert responseType.size() == 0;
        }
    }
}