package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.web.Invoker;
import net.hasor.web.invoker.beans.TestServlet;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
//
public class InMappingServletTest {
    private Invoker newInvoker(String mappingTo, String httpMethod, AppContext appContext) {
        Invoker invoker = PowerMockito.mock(Invoker.class);
        HttpServletRequest servletRequest = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(invoker.getRequestPath()).thenReturn(mappingTo);
        PowerMockito.when(servletRequest.getMethod()).thenReturn(httpMethod);
        PowerMockito.when(invoker.getHttpRequest()).thenReturn(servletRequest);
        PowerMockito.when(invoker.getAppContext()).thenReturn(appContext);
        return invoker;
    }
    //
    @Test
    public void basicTest() throws Throwable {
        AppContext appContext = Hasor.create().asSmaller().build();
        //
        BindInfo<TestServlet> targetInfo = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo.getBindType()).thenReturn(TestServlet.class);
        //
        InMappingServlet mappingDef = new InMappingServlet(1, targetInfo, "/execute.do", null);
        //
        Invoker invoker1 = newInvoker("/execute.do", "GET", appContext);
        Method method1 = mappingDef.findMethod(invoker1);
        assert method1 != null;
        //
        Invoker invoker2 = newInvoker("/execute.do", "POST", appContext);
        assert mappingDef.findMethod(invoker1) == mappingDef.findMethod(invoker2);
        //
        assert mappingDef.getHttpMethodSet().length == 1;
        assert mappingDef.getIndex() == 1;
        assert mappingDef.getTargetType() == targetInfo;
        assert "/execute.do".equals(mappingDef.getMappingTo());
        //
        mappingDef.newInstance(invoker1);
        assert TestServlet.isStaticInitServlet();
        TestServlet.resetInit();
        assert !TestServlet.isStaticInitServlet();
    }
}