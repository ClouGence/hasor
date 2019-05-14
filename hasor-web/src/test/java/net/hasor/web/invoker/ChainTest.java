package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.filters.Demo1CallerFilter;
import net.hasor.web.invoker.filters.Demo2CallerFilter;
import net.hasor.web.invoker.filters.Demo3CallerFilter;
import net.hasor.web.invoker.params.QueryCallAction;
import net.hasor.web.wrap.DefaultServlet;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//
public class ChainTest extends AbstractWeb30BinderDataTest {
    @Test
    public void chainTest1() throws Throwable {
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).filter("*").through(Demo1CallerFilter.class);
            apiBinder.tryCast(WebApiBinder.class).filter("*").through(Demo2CallerFilter.class);
            apiBinder.tryCast(WebApiBinder.class).filter("/abc/*").through(Demo3CallerFilter.class);
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
        });
        Demo1CallerFilter.resetCalls();
        Demo2CallerFilter.resetCalls();
        Demo3CallerFilter.resetCalls();
        //
        InvokerContext invokerContext = new InvokerContext();
        //
        //
        assert !Demo1CallerFilter.isInitCall();
        assert !Demo2CallerFilter.isInitCall();
        assert !Demo3CallerFilter.isInitCall();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        assert Demo1CallerFilter.isInitCall();
        assert Demo2CallerFilter.isInitCall();
        assert Demo3CallerFilter.isInitCall();
        //
        //
        HttpServletRequest httpRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        ExceuteCaller caller = invokerContext.genCaller(httpRequest, httpResponse);
        //
        assert !Demo1CallerFilter.isDoCall();
        assert !Demo2CallerFilter.isDoCall();
        assert !Demo3CallerFilter.isDoCall();
        Object o = caller.invoke(null).get();
        assert Demo1CallerFilter.isDoCall();
        assert Demo2CallerFilter.isDoCall();
        assert !Demo3CallerFilter.isDoCall();
        assert o instanceof Map;
        assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
        assert ((BigInteger) ((Map) o).get("bigInteger")).longValue() == 321;
    }
    @Test
    public void chainTest2() throws Throwable {
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).filter("*").through(Demo1CallerFilter.class);
            apiBinder.tryCast(WebApiBinder.class).filter("*").through(Demo2CallerFilter.class);
            apiBinder.tryCast(WebApiBinder.class).filter("/abc/*").through(Demo3CallerFilter.class);
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
        });
        Demo1CallerFilter.resetCalls();
        Demo2CallerFilter.resetCalls();
        Demo3CallerFilter.resetCalls();
        //
        InvokerContext invokerContext = new InvokerContext();
        //
        //
        assert !Demo1CallerFilter.isInitCall();
        assert !Demo2CallerFilter.isInitCall();
        assert !Demo3CallerFilter.isInitCall();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        assert Demo1CallerFilter.isInitCall();
        assert Demo2CallerFilter.isInitCall();
        assert Demo3CallerFilter.isInitCall();
        //
        //
        HttpServletRequest httpRequest = mockRequest("post", new URL("http://www.hasor.net/abc.html"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        ExceuteCaller caller = invokerContext.genCaller(httpRequest, httpResponse);
        //
        assert !Demo1CallerFilter.isDoCall();
        assert !Demo2CallerFilter.isDoCall();
        assert !Demo3CallerFilter.isDoCall();
        Object o = caller.invoke(null).get();
        assert Demo1CallerFilter.isDoCall();
        assert Demo2CallerFilter.isDoCall();
        assert !Demo3CallerFilter.isDoCall();
        assert o == null;
    }
    @Test
    public void sortTest1() throws Throwable {
        final ArrayList<String> sortData = new ArrayList<>();
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).filter("*").through(1, (invoker, chain) -> {
                sortData.add("Filter_1");
                return chain.doNext(invoker);
            });
            apiBinder.tryCast(WebApiBinder.class).filter("*").through(0, (invoker, chain) -> {
                sortData.add("Filter_0");
                return chain.doNext(invoker);
            });
            //
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
        });
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        //
        HttpServletRequest httpRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        ExceuteCaller caller = invokerContext.genCaller(httpRequest, httpResponse);
        //
        caller.invoke(null).get();
        assert sortData.size() == 2;
        assert sortData.get(0).equalsIgnoreCase("Filter_0");
        assert sortData.get(1).equalsIgnoreCase("Filter_1");
    }
    @Test
    public void sortTest2() throws Throwable {
        final ArrayList<String> sortData = new ArrayList<>();
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).filter("*").through((invoker, chain) -> {
                sortData.add("Filter_1");
                return chain.doNext(invoker);
            });
            apiBinder.tryCast(WebApiBinder.class).filter("*").through((invoker, chain) -> {
                sortData.add("Filter_0");
                return chain.doNext(invoker);
            });
            //
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
        });
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        //
        HttpServletRequest httpRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        ExceuteCaller caller = invokerContext.genCaller(httpRequest, httpResponse);
        //
        caller.invoke(null).get();
        assert sortData.size() == 2;
        assert sortData.get(0).equalsIgnoreCase("Filter_1");
        assert sortData.get(1).equalsIgnoreCase("Filter_0");
    }
    @Test
    public void sortTest3() throws Throwable {
        final ArrayList<String> sortData = new ArrayList<>();
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).jeeServlet("/*.do").with(1, new DefaultServlet() {
                public void service(ServletRequest req, ServletResponse res) {
                    sortData.add("Servlet_1");
                }
            });
            apiBinder.tryCast(WebApiBinder.class).jeeServlet("/*abc.do").with(0, new DefaultServlet() {
                public void service(ServletRequest req, ServletResponse res) {
                    sortData.add("Servlet_0");
                }
            });
            //
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
        });
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        //
        HttpServletRequest httpRequest = mockRequest("post", new URL("http://www.hasor.net/test_abc.do"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        ExceuteCaller caller = invokerContext.genCaller(httpRequest, httpResponse);
        //
        caller.invoke(null).get();
        assert sortData.size() == 1;
        assert sortData.get(0).equalsIgnoreCase("Servlet_0");
    }
    @Test
    public void sortTest4() throws Throwable {
        final ArrayList<String> sortData = new ArrayList<>();
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).jeeServlet("/*.do").with(0, new DefaultServlet() {
                public void service(ServletRequest req, ServletResponse res) {
                    sortData.add("Servlet_1");
                }
            });
            apiBinder.tryCast(WebApiBinder.class).jeeServlet("/*abc.do").with(1, new DefaultServlet() {
                public void service(ServletRequest req, ServletResponse res) {
                    sortData.add("Servlet_0");
                }
            });
            //
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
        });
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        //
        HttpServletRequest httpRequest = mockRequest("post", new URL("http://www.hasor.net/test_abc.do"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        ExceuteCaller caller = invokerContext.genCaller(httpRequest, httpResponse);
        //
        caller.invoke(null).get();
        assert sortData.size() == 1;
        assert sortData.get(0).equalsIgnoreCase("Servlet_1");
    }
}