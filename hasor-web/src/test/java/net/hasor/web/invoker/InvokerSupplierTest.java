package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.Mapping;
import net.hasor.web.WebModule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Set;
//
public class InvokerSupplierTest extends AbstractWeb30BinderDataTest {
    @Test
    public void invokerSupplierTest() throws Throwable {
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            //apiBinder.addPlugin(new TestWebPlugin());
        });
        //
        HttpServletRequest httpRequest = super.mockRequest("GET", new URL("http://www.hasor.net/query_param.do?byteParam=123&bigInteger=321"), appContext);
        HttpServletResponse httpResponse = PowerMockito.mock(HttpServletResponse.class);
        InvokerSupplier supplier = new InvokerSupplier(PowerMockito.mock(Mapping.class), appContext, httpRequest, httpResponse);
        //
        //
        assert supplier.getHttpRequest() == httpRequest;
        assert supplier.getHttpResponse() == httpResponse;
        assert supplier.getAppContext() == appContext;
        //
        supplier.put("abc", "abc");
        assert "abc".equals(supplier.get("abc"));
        supplier.remove("abc");
        assert supplier.get("abc") == null;
        //
        //
        supplier.put("key", "kv");
        assert "kv".equals(supplier.get("key"));
        supplier.lockKey("key");
        try {
            supplier.put("key", "111");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" is lock key.");
        }
        try {
            supplier.remove("key");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith(" is lock key.");
        }
        //
        Set<String> strings = supplier.keySet();
        assert strings.contains("key");
    }
}