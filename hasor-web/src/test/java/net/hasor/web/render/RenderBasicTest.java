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
import net.hasor.web.AbstractTest;
import net.hasor.web.Invoker;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

public class RenderBasicTest extends AbstractTest {
    @Test
    public void renderInvoker_params() throws Throwable {
        //
        HttpServletRequest request = mockRequest("get", new URL("http://www.hasor.net/abc.do?a=1&b=2"));
        Invoker invoker = PowerMockito.mock(Invoker.class);
        PowerMockito.when(invoker.getHttpRequest()).thenReturn(request);
        RenderInvokerSupplier supplier = new RenderInvokerSupplier(invoker);
        //
        assert supplier.get("req_a").equals("1");
        assert supplier.get("req_b").equals("2");
    }
}