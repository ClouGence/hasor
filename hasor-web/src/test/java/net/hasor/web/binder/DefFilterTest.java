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
package net.hasor.web.binder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.test.web.filters.SimpleInvokerFilter;
import net.hasor.web.AbstractTest;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.HashMap;
import java.util.Map;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefFilterTest extends AbstractTest {
    @Test
    public void defTest_1() throws Throwable {
        SimpleInvokerFilter filter = new SimpleInvokerFilter();
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(SimpleInvokerFilter.class).toInstance(filter);
        });
        BindInfo<SimpleInvokerFilter> bindInfo = appContext.getBindInfo(SimpleInvokerFilter.class);
        //
        int index = 123;
        UriPatternMatcher patternMatcher = UriPatternType.get(UriPatternType.SERVLET, "*.do");
        Map<String, String> initParams = new HashMap<>();
        FilterDef def = new FilterDef(index, patternMatcher, initParams, bindInfo, () -> appContext);
        //
        Invoker invoker1 = PowerMockito.mock(Invoker.class);
        PowerMockito.when(invoker1.getRequestPath()).thenReturn("/test/");
        Invoker invoker2 = PowerMockito.mock(Invoker.class);
        PowerMockito.when(invoker2.getRequestPath()).thenReturn("/test/abc.do");
        //
        assert def.getIndex() == 123;
        assert !def.matchesInvoker(invoker1);
        assert def.matchesInvoker(invoker2);
        assert def.toString().contains("uriPatternType");
        //
        try {
            def.doInvoke(invoker1, null);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equals("this Filter uninitialized.");
        }
        //
        assert !filter.isInit();
        OneConfig oneConfig1 = new OneConfig("test", () -> appContext) {{
            put("config_a", "a");
        }};
        OneConfig oneConfig2 = new OneConfig("test", () -> appContext) {{
            put("config_a", "aa");
        }};
        def.init(oneConfig1);
        def.init(oneConfig2);
        assert filter.isInit();
        assert filter.getConfig().getInitParameter("config_a").equals("a");
        //
        def.doInvoke(invoker1, PowerMockito.mock(InvokerChain.class));
        assert filter.isDoCall();
        //
        assert !filter.isDestroy();
        def.destroy();
        def.destroy();
        assert filter.isDestroy();
    }

    @Test
    public void defTest_2() throws Throwable {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(SimpleInvokerFilter.class).toInstance(null);
        });
        BindInfo<SimpleInvokerFilter> bindInfo = appContext.getBindInfo(SimpleInvokerFilter.class);
        //
        int index = 123;
        UriPatternMatcher patternMatcher = UriPatternType.get(UriPatternType.SERVLET, "*.do");
        Map<String, String> initParams = new HashMap<>();
        FilterDef def = new FilterDef(index, patternMatcher, initParams, bindInfo, () -> appContext);
        //
        try {
            def.init(new OneConfig("test", () -> appContext) {{
                put("config_a", "a");
            }});
            def.doInvoke(PowerMockito.mock(Invoker.class), PowerMockito.mock(InvokerChain.class));
        } catch (Exception e) {
            e.printStackTrace();
            assert e.getMessage().equals("target InvokerFilter instance is null.");
        }
    }
}