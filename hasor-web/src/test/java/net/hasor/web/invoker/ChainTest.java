package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.Invoker;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.filters.Demo1CallerFilter;
import net.hasor.web.invoker.filters.Demo2CallerFilter;
import net.hasor.web.invoker.params.QueryCallAction;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
//
public class ChainTest extends AbstractWeb30BinderDataTest {
    @Test
    public void chainTest() throws Throwable {
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).filter("*").through(Demo1CallerFilter.class);
                apiBinder.tryCast(WebApiBinder.class).filter("*").through(Demo2CallerFilter.class);
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
            }
        });
        Demo1CallerFilter.resetCalls();
        Demo2CallerFilter.resetCalls();
        //
        InvokerContext invokerContext = new InvokerContext();
        //
        //
        assert !Demo1CallerFilter.isInitCall();
        assert !Demo2CallerFilter.isInitCall();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        assert Demo1CallerFilter.isInitCall();
        assert Demo2CallerFilter.isInitCall();
        //
        //
        HttpServletRequest httpRequest = mockRequest("post", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        Invoker invoker = invokerContext.newInvoker(httpRequest, httpResponse);
        ExceuteCaller caller = invokerContext.genCaller(invoker);
        //
        assert !Demo1CallerFilter.isDoCall();
        assert !Demo2CallerFilter.isDoCall();
        Object o = caller.invoke(invoker, null).get();
        assert Demo1CallerFilter.isDoCall();
        assert Demo2CallerFilter.isDoCall();
        assert o instanceof Map;
        assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
        assert ((BigInteger) ((Map) o).get("bigInteger")).longValue() == 321;
    }
}