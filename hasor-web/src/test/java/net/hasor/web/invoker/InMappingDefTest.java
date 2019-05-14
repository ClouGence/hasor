package net.hasor.web.invoker;
import net.hasor.core.BindInfo;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.web.Invoker;
import net.hasor.web.invoker.beans.Async2TestAction;
import net.hasor.web.invoker.beans.AsyncTestAction;
import net.hasor.web.invoker.beans.BasicTestAction;
import net.hasor.web.invoker.beans.HttpsTestAction;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
//
public class InMappingDefTest {
    private Invoker newInvoker(String mappingTo, String httpMethod) {
        Invoker invoker = PowerMockito.mock(Invoker.class);
        HttpServletRequest servletRequest1 = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(invoker.getRequestPath()).thenReturn(mappingTo);
        PowerMockito.when(servletRequest1.getRequestURI()).thenReturn(mappingTo);
        PowerMockito.when(servletRequest1.getContextPath()).thenReturn("");
        PowerMockito.when(servletRequest1.getMethod()).thenReturn(httpMethod);
        PowerMockito.when(invoker.getHttpRequest()).thenReturn(servletRequest1);
        return invoker;
    }
    //
    @Test
    public void newInMappingDefTest() throws Throwable {
        BindInfo<HttpsTestAction> targetInfo = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo.getBindType()).thenReturn(HttpsTestAction.class);
        //
        try {
            new InMappingDef(1, null, "/abc/test.do", Matchers.anyMethod());
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith("targetType is null.");
        }
        //
        try {
            new InMappingDef(1, targetInfo, null, Matchers.anyMethod());
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith("' Service path is empty.");
        }
        //
        try {
            new InMappingDef(1, targetInfo, "abc/test.do", Matchers.anyMethod());
            assert false;
        } catch (Exception e) {
            assert e.getMessage().endsWith("' Service path format error, must be a '/' at the start.");
        }
        //
        new InMappingDef(1, targetInfo, "/abc/test.do", Matchers.anyMethod());
        //
        //
        Invoker invoker = newInvoker("/abc/test.do", "GET");
        InMappingDef def = new InMappingDef(1, targetInfo, "/*.do", Matchers.anyMethod());
        assert !def.isAsync(invoker.getHttpRequest());
        //
        PowerMockito.when(invoker.getRequestPath()).thenReturn("/abc/asyncAction.do");
        assert !def.isAsync(invoker.getHttpRequest());
        //
        //
        Invoker invoker3 = newInvoker("/abc.do", "POST");
        InMappingDef mappingDef3 = new InMappingDef(1, targetInfo, "/execute.do", Matchers.anyMethod(), true);
        assert !mappingDef3.isAsync(invoker3.getHttpRequest());
    }
    //
    @Test
    public void basicTest() throws Throwable {
        BindInfo<BasicTestAction> targetInfo = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo.getBindType()).thenReturn(BasicTestAction.class);
        //
        //
        InMappingDef mappingDef = new InMappingDef(1, targetInfo, "/execute.do", Matchers.anyMethod(), false);
        Invoker invoker1 = newInvoker("/execute.do", "GET");
        Method method1 = mappingDef.findMethod(invoker1.getHttpRequest());
        assert method1 != null;
        //
        Invoker invoker2 = newInvoker("/execute.do", "POST");
        assert mappingDef.findMethod(invoker1.getHttpRequest()) == mappingDef.findMethod(invoker2.getHttpRequest());
        //
        assert mappingDef.getHttpMethodSet().length == 1;
        assert mappingDef.getIndex() == 1;
        assert mappingDef.getTargetType() == targetInfo;
        assert "/execute.do".equals(mappingDef.getMappingTo());
    }
    //
    @Test
    public void asyncTest() throws Throwable {
        BindInfo<AsyncTestAction> targetInfo1 = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo1.getBindType()).thenReturn(AsyncTestAction.class);
        //
        Invoker invoker1 = newInvoker("/execute.do", "POST");
        InMappingDef mappingDef1 = new InMappingDef(1, targetInfo1, "/execute.do", Matchers.anyMethod(), false);
        assert mappingDef1.isAsync(invoker1.getHttpRequest());
        //
        //
        BindInfo<Async2TestAction> targetInfo2 = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo2.getBindType()).thenReturn(Async2TestAction.class);
        //
        Invoker invoker2 = newInvoker("/execute.do", "POST");
        InMappingDef mappingDef2 = new InMappingDef(1, targetInfo2, "/execute.do", Matchers.anyMethod(), false);
        assert mappingDef2.isAsync(invoker2.getHttpRequest());
    }
    //
    @Test
    public void httpTest() throws Throwable {
        BindInfo<HttpsTestAction> targetInfo = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo.getBindType()).thenReturn(HttpsTestAction.class);
        //
        Invoker invoker1 = newInvoker("/execute.do", "GET");
        Invoker invoker2 = newInvoker("/execute.do", "POST");
        Invoker invoker3 = newInvoker("/execute.do", "ADD");
        Invoker invoker4 = newInvoker("/execute.do", "DELETE");
        Invoker invoker5 = newInvoker("/execute.do", "OPTION");
        //
        InMappingDef mappingDef = new InMappingDef(1, targetInfo, "/execute.do", Matchers.anyMethod(), false);
        assert mappingDef.findMethod(invoker1.getHttpRequest()).getName().equals("execute1");
        assert mappingDef.findMethod(invoker2.getHttpRequest()).getName().equals("execute2");
        assert mappingDef.findMethod(invoker3.getHttpRequest()).getName().equals("execute3");
        assert mappingDef.findMethod(invoker4.getHttpRequest()).getName().equals("execute3");
        assert mappingDef.findMethod(invoker5.getHttpRequest()) == null;
        //
        assert mappingDef.getHttpMethodSet().length == 4;
        assert mappingDef.findMethod("GET").getName().equals("execute1");
        assert mappingDef.findMethod("POST").getName().equals("execute2");
        assert mappingDef.findMethod("ADD").getName().equals("execute3");
        assert mappingDef.findMethod("DELETE").getName().equals("execute3");
    }
    //
    @Test
    public void matchingTest() throws Throwable {
        BindInfo<HttpsTestAction> targetInfo = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo.getBindType()).thenReturn(HttpsTestAction.class);
        InMappingDef mappingDef = new InMappingDef(1, targetInfo, "/execute.do", Matchers.anyMethod(), false);
        //
        Invoker invoker1 = newInvoker("/execute.do", "GET");
        Invoker invoker2 = newInvoker("/abc.do", "GET");
        Invoker invoker3 = newInvoker("/execute.do", "ABC");
        //
        assert mappingDef.matchingMapping(invoker1.getHttpRequest());
        assert !mappingDef.matchingMapping(invoker2.getHttpRequest());
        assert !mappingDef.matchingMapping(invoker3.getHttpRequest());
        assert "/execute.do".matches(mappingDef.getMappingToMatches());
        //
        mappingDef = new InMappingDef(1, targetInfo, "/*.do", Matchers.anyMethod(), false);
        assert "/execute.do".matches(mappingDef.getMappingToMatches());
        assert "/abc.do".matches(mappingDef.getMappingToMatches());
        //
        mappingDef.toString();
    }
}