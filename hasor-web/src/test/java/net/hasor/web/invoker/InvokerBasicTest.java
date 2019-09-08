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
import net.hasor.core.Hasor;
import net.hasor.test.actions.render.HtmlProduces;
import net.hasor.test.render.SimpleRenderEngine;
import net.hasor.web.AbstractTest;
import net.hasor.web.binder.MappingDef;
import net.hasor.web.binder.OneConfig;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;

public class InvokerBasicTest extends AbstractTest {
    @Test
    public void contentType_1() throws Throwable {
        SimpleRenderEngine renderEngine = new SimpleRenderEngine();
        //
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", Hasor::create, apiBinder -> {
            //
            apiBinder.addRender("html").toInstance(renderEngine);
            //
            apiBinder.loadMappingTo(HtmlProduces.class);
            //
        }, servlet30("/"), AbstractTest.LoadModule.Web, AbstractTest.LoadModule.Render);
        //
        //
        List<MappingDef> definitions = appContext.findBindingBean(MappingDef.class);
        assert definitions.size() == 2;
        final Set<String> responseType = new HashSet<>();
        HttpServletResponse servletResponse = PowerMockito.mock(HttpServletResponse.class);
        PowerMockito.doAnswer((Answer<Void>) invocation -> {
            responseType.add(invocation.getArguments()[0].toString());
            return null;
        }).when(servletResponse).setContentType(anyString());
        //
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new OneConfig("", () -> appContext));
        {
            ExceuteCaller caller = invokerContext.genCaller(mockRequest("post", new URL("http://www.hasor.net/abc.do")), servletResponse);
            caller.invoke(null);
            assert responseType.contains("test/html");
        }
        //
        {
            responseType.clear();
            ExceuteCaller caller = invokerContext.genCaller(mockRequest("get", new URL("http://www.hasor.net/abc.do")), servletResponse);
            caller.invoke(null);
            assert responseType.contains("text/javacc_jj");
        }
    }
}