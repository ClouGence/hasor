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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.web.Invoker;
import net.hasor.web.invoker.beans.TestServlet;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
//
public class InMappingServletTest {
    private Invoker newInvoker(String mappingTo, String httpMethod, AppContext appContext) {
        Invoker invoker = PowerMockito.mock(Invoker.class);
        HttpServletRequest servletRequest = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(invoker.getRequestPath()).thenReturn(mappingTo);
        PowerMockito.when(servletRequest.getMethod()).thenReturn(httpMethod);
        PowerMockito.when(servletRequest.getRequestURI()).thenReturn(mappingTo);
        PowerMockito.when(servletRequest.getContextPath()).thenReturn("");
        PowerMockito.when(invoker.getHttpRequest()).thenReturn(servletRequest);
        PowerMockito.when(invoker.getAppContext()).thenReturn(appContext);
        return invoker;
    }
    //
    @Test
    public void basicTest() throws Throwable {
        AppContext appContext = Hasor.create().asTiny().build();
        //
        BindInfo<TestServlet> targetInfo = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(targetInfo.getBindType()).thenReturn(TestServlet.class);
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        //
        InMappingServlet mappingDef = new InMappingServlet(1, targetInfo, "/execute.do", null, servletContext);
        //
        Invoker invoker1 = newInvoker("/execute.do", "GET", appContext);
        Method method1 = mappingDef.findMethod(invoker1.getHttpRequest());
        assert method1 != null;
        //
        Invoker invoker2 = newInvoker("/execute.do", "POST", appContext);
        assert mappingDef.findMethod(invoker1.getHttpRequest()) == mappingDef.findMethod(invoker2.getHttpRequest());
        //
        assert mappingDef.getHttpMethodSet().length == 1;
        assert mappingDef.getIndex() == 1;
        assert mappingDef.getTargetType() == targetInfo;
        assert "/execute.do".equals(mappingDef.getMappingTo());
        //
        Object newInstance = appContext.getInstance(mappingDef.getTargetType());
        mappingDef.beanCreated((HttpServlet) newInstance, targetInfo);
        assert TestServlet.isStaticInitServlet();
        TestServlet.resetInit();
        assert !TestServlet.isStaticInitServlet();
    }
}