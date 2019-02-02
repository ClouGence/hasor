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
import net.hasor.core.Provider;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.definition.MappingDiscovererDefinition;
import net.hasor.web.definition.WebPluginDefinition;
import net.hasor.web.invoker.beans.TestMappingDiscoverer;
import net.hasor.web.invoker.beans.TestWebPlugin;
import net.hasor.web.startup.RuntimeFilter;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.List;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebBinderDataTest extends AbstractWebBinderDataTest {
    //
    @Test
    public void binderTest0() throws Exception {
        Method target = WebPluginDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final TestWebPlugin testWebPlugin = new TestWebPlugin();
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).addPlugin(testWebPlugin);
                apiBinder.tryCast(WebApiBinder.class).setEncodingCharacter("UTF-8-TTT", "UTF-8-AAA");
                //
                assert servletContext == apiBinder.getServletContext();
                assert apiBinder.tryCast(WebApiBinder.class).getServletVersion() == ServletVersion.V2_5;
                //
            }
        });
        assert "UTF-8-TTT".equals(appContext.findBindingBean(RuntimeFilter.HTTP_REQUEST_ENCODING_KEY, String.class));
        assert "UTF-8-AAA".equals(appContext.findBindingBean(RuntimeFilter.HTTP_RESPONSE_ENCODING_KEY, String.class));
        //
        //
        // . Mock seervlet 3.1
        final ServletContext sc = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(sc.getEffectiveMajorVersion()).thenReturn(1);
        PowerMockito.when(sc.getVirtualServerName()).thenReturn("");
        Hasor.create(sc).asSmaller()//
                .addSettings("http://test.hasor.net", "hasor.innerApiBinderSet.binder", newDefaultXmlNode())//
                .build(new WebModule() {
                    @Override
                    public void loadModule(WebApiBinder apiBinder) throws Throwable {
                        assert sc == apiBinder.getServletContext();
                        assert apiBinder.tryCast(WebApiBinder.class).getServletVersion() == ServletVersion.V3_1;
                        //
                    }
                });
        //
    }
    //
    @Test
    public void webPluginTest1() throws Exception {
        Method target = WebPluginDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final TestWebPlugin testWebPlugin = new TestWebPlugin();
        final Provider<? extends TestWebPlugin> testWebPluginProvider = InstanceProvider.of(testWebPlugin);
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).addPlugin(testWebPlugin);
                apiBinder.tryCast(WebApiBinder.class).addPlugin(testWebPluginProvider);
                //
                apiBinder.tryCast(WebApiBinder.class).addPlugin(TestWebPlugin.class);
                //
                // 强制设置成单例
                BindInfo<TestWebPlugin> bindInfo = apiBinder.bindType(TestWebPlugin.class).asEagerSingleton().toInfo();
                apiBinder.tryCast(WebApiBinder.class).addPlugin(bindInfo);
            }
        });
        //
        List<WebPluginDefinition> definitions = appContext.findBindingBean(WebPluginDefinition.class);
        assert definitions.size() == 4;
        TestWebPlugin.resetInit();
        assert !TestWebPlugin.isBeforeFilter();
        assert !TestWebPlugin.isAfterFilter();
        //
        TestWebPlugin.resetInit();
        definitions.get(0).initPlugin(appContext);
        definitions.get(0).beforeFilter(null, null);
        definitions.get(0).afterFilter(null, null);
        assert TestWebPlugin.isBeforeFilter();
        assert TestWebPlugin.isAfterFilter();
        //
        definitions.get(0).initPlugin(appContext);
        definitions.get(1).initPlugin(appContext);
        definitions.get(2).initPlugin(appContext);
        definitions.get(3).initPlugin(appContext);
        //
        Object invoke1 = target.invoke(definitions.get(0));
        Object invoke2 = target.invoke(definitions.get(1));
        Object invoke3_1 = target.invoke(definitions.get(2));
        Object invoke3_2 = target.invoke(definitions.get(2));
        Object invoke4_1 = target.invoke(definitions.get(3));
        Object invoke4_2 = target.invoke(definitions.get(3));
        //
        assert invoke1 == testWebPlugin;
        assert invoke2 == testWebPlugin;
        assert invoke3_1 != invoke3_2;
        assert invoke4_1 == invoke4_2;
    }
    //
    @Test
    public void discovererTest1() throws Exception {
        Method target = MappingDiscovererDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final TestMappingDiscoverer testDiscoverer = new TestMappingDiscoverer();
        final Provider<? extends TestMappingDiscoverer> testDiscovererProvider = InstanceProvider.of(testDiscoverer);
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).addDiscoverer(testDiscoverer);
                apiBinder.tryCast(WebApiBinder.class).addDiscoverer(testDiscovererProvider);
                //
                apiBinder.tryCast(WebApiBinder.class).addDiscoverer(TestMappingDiscoverer.class);
                //
                // 强制设置成单例
                BindInfo<TestMappingDiscoverer> bindInfo = apiBinder.bindType(TestMappingDiscoverer.class).asEagerSingleton().toInfo();
                apiBinder.tryCast(WebApiBinder.class).addDiscoverer(bindInfo);
            }
        });
        //
        List<MappingDiscovererDefinition> definitions = appContext.findBindingBean(MappingDiscovererDefinition.class);
        assert definitions.size() == 4;
        TestMappingDiscoverer.resetCall();
        assert !TestMappingDiscoverer.isResetCall();
        definitions.get(0).discover(null);
        assert TestMappingDiscoverer.isResetCall();
        //
        Object invoke1 = target.invoke(definitions.get(0));
        Object invoke2 = target.invoke(definitions.get(1));
        Object invoke3_1 = target.invoke(definitions.get(2));
        Object invoke3_2 = target.invoke(definitions.get(2));
        Object invoke4_1 = target.invoke(definitions.get(3));
        Object invoke4_2 = target.invoke(definitions.get(3));
        //
        assert invoke1 == testDiscoverer;
        assert invoke2 == testDiscoverer;
        assert invoke3_1 != invoke3_2;
        assert invoke4_1 == invoke4_2;
    }
}