package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.Invoker;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.params.QueryCallAction;
import org.junit.Test;

import java.net.URL;
import java.util.List;
import java.util.Map;
//
public class CallerParamsTest extends AbstractWeb30BinderDataTest {
    @Test
    public void syncInvokeTest2() throws Throwable {
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
            }
        });
        //
        List<InMappingDef> definitions = appContext.findBindingBean(InMappingDef.class);
        InvokerCaller caller = new InvokerCaller(definitions.get(0), null, null);
        Invoker invoker1 = newInvoker(mockRequest("post", new URL("http://www.hasor.net/sync.do?byteParam=123"), appContext), appContext);
        // QueryCallAction
        Object o = caller.invoke(invoker1, null).get();
        assert o instanceof Map;
        assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
    }
}