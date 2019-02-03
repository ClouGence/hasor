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
import net.hasor.web.definition.*;
import net.hasor.web.definition.beans.TestCallerFilter;
import net.hasor.web.definition.beans.TestHttpSessionListener;
import net.hasor.web.definition.beans.TestMappingDiscoverer;
import net.hasor.web.definition.beans.TestServletContextListener;
import net.hasor.web.invoker.beans.TestWebPlugin;
import net.hasor.web.startup.RuntimeFilter;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        assert !TestMappingDiscoverer.isDiscoverCall();
        definitions.get(0).discover(null);
        assert TestMappingDiscoverer.isDiscoverCall();
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
    //
    @Test
    public void servletListenerTest1() throws Exception {
        Method target = ContextListenerDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final TestServletContextListener testServletContextListener = new TestServletContextListener();
        final Provider<TestServletContextListener> testtestServletContextListenerProvider = InstanceProvider.of(testServletContextListener);
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).addServletListener(testServletContextListener);
                apiBinder.tryCast(WebApiBinder.class).addServletListener(testtestServletContextListenerProvider);
                //
                apiBinder.tryCast(WebApiBinder.class).addServletListener(TestServletContextListener.class);
                //
                // 强制设置成单例
                BindInfo<TestServletContextListener> bindInfo = apiBinder.bindType(TestServletContextListener.class).asEagerSingleton().toInfo();
                apiBinder.tryCast(WebApiBinder.class).addServletListener(bindInfo);
            }
        });
        //
        List<ContextListenerDefinition> definitions = appContext.findBindingBean(ContextListenerDefinition.class);
        assert definitions.size() == 4;
        definitions.get(0).init(appContext);
        definitions.get(1).init(appContext);
        definitions.get(2).init(appContext);
        definitions.get(3).init(appContext);
        //
        TestServletContextListener.resetCalls();
        assert !TestServletContextListener.isContextInitializedCall();
        assert !TestServletContextListener.isContextDestroyedCall();
        definitions.get(0).contextInitialized(null);
        definitions.get(0).contextDestroyed(null);
        assert TestServletContextListener.isContextInitializedCall();
        assert TestServletContextListener.isContextDestroyedCall();
        //
        Object invoke1 = target.invoke(definitions.get(0));
        Object invoke2 = target.invoke(definitions.get(1));
        Object invoke3_1 = target.invoke(definitions.get(2));
        Object invoke3_2 = target.invoke(definitions.get(2));
        Object invoke4_1 = target.invoke(definitions.get(3));
        Object invoke4_2 = target.invoke(definitions.get(3));
        //
        assert invoke1 == testServletContextListener;
        assert invoke2 == testServletContextListener;
        assert invoke3_1 != invoke3_2;
        assert invoke4_1 == invoke4_2;
    }
    //
    @Test
    public void sessionListenerTest1() throws Exception {
        Method target = HttpSessionListenerDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final TestHttpSessionListener testServletContextListener = new TestHttpSessionListener();
        final Provider<TestHttpSessionListener> testtestServletContextListenerProvider = InstanceProvider.of(testServletContextListener);
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).addSessionListener(testServletContextListener);
                apiBinder.tryCast(WebApiBinder.class).addSessionListener(testtestServletContextListenerProvider);
                //
                apiBinder.tryCast(WebApiBinder.class).addSessionListener(TestHttpSessionListener.class);
                //
                // 强制设置成单例
                BindInfo<TestHttpSessionListener> bindInfo = apiBinder.bindType(TestHttpSessionListener.class).asEagerSingleton().toInfo();
                apiBinder.tryCast(WebApiBinder.class).addSessionListener(bindInfo);
            }
        });
        //
        List<HttpSessionListenerDefinition> definitions = appContext.findBindingBean(HttpSessionListenerDefinition.class);
        assert definitions.size() == 4;
        definitions.get(0).init(appContext);
        definitions.get(1).init(appContext);
        definitions.get(2).init(appContext);
        definitions.get(3).init(appContext);
        //
        TestHttpSessionListener.resetCalls();
        assert !TestHttpSessionListener.isSessionCreatedCallCall();
        assert !TestHttpSessionListener.issSessionDestroyedCallCall();
        definitions.get(0).sessionCreated(null);
        definitions.get(0).sessionDestroyed(null);
        assert TestHttpSessionListener.isSessionCreatedCallCall();
        assert TestHttpSessionListener.issSessionDestroyedCallCall();
        //
        Object invoke1 = target.invoke(definitions.get(0));
        Object invoke2 = target.invoke(definitions.get(1));
        Object invoke3_1 = target.invoke(definitions.get(2));
        Object invoke3_2 = target.invoke(definitions.get(2));
        Object invoke4_1 = target.invoke(definitions.get(3));
        Object invoke4_2 = target.invoke(definitions.get(3));
        //
        assert invoke1 == testServletContextListener;
        assert invoke2 == testServletContextListener;
        assert invoke3_1 != invoke3_2;
        assert invoke4_1 == invoke4_2;
    }
    //
    @Test
    public void filterTest1() throws Throwable {
        Method target = InvokeFilterDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final String[] urls = new String[] { "/abc.do", "/def.do" };
        final TestCallerFilter testCallerFilter = new TestCallerFilter();
        final Provider<TestCallerFilter> testCallerFilterProvider = InstanceProvider.of(testCallerFilter);
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                BindInfo<TestCallerFilter> filterBindInfo1 = apiBinder.bindType(TestCallerFilter.class).asEagerSingleton().toInfo();
                BindInfo<TestCallerFilter> filterBindInfo2 = apiBinder.bindType(TestCallerFilter.class).toInfo();
                //
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(testCallerFilter);           // 1
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(testCallerFilterProvider);   // 2
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(TestCallerFilter.class);     // 3
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(filterBindInfo1);            // 4
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(filterBindInfo2);            // 5
            }
        });
        //
        List<AbstractDefinition> definitions = appContext.findBindingBean(AbstractDefinition.class);
        assert definitions.size() == 10;
        for (int i = 0; i < 10; i++) {
            definitions.get(i).init(new InvokerMapConfig(null, appContext));
        }
        //
        assert "/abc.do".equals(definitions.get(0).getPattern());
        assert "/abc.do".equals(definitions.get(2).getPattern());
        assert "/abc.do".equals(definitions.get(4).getPattern());
        assert "/abc.do".equals(definitions.get(6).getPattern());
        assert "/abc.do".equals(definitions.get(8).getPattern());
        //
        assert "/def.do".equals(definitions.get(1).getPattern());
        assert "/def.do".equals(definitions.get(3).getPattern());
        assert "/def.do".equals(definitions.get(5).getPattern());
        assert "/def.do".equals(definitions.get(7).getPattern());
        assert "/def.do".equals(definitions.get(9).getPattern());
        //
        for (int i = 0; i < 10; i++) {
            assert definitions.get(i).getIndex() == 0;
        }
        //
        Object invoke0 = target.invoke(definitions.get(0));     // 1
        Object invoke2 = target.invoke(definitions.get(2));     // 2
        Object invoke4_1 = target.invoke(definitions.get(4));   // 3
        Object invoke4_2 = target.invoke(definitions.get(4));   // 3
        Object invoke6_1 = target.invoke(definitions.get(6));   // 4
        Object invoke6_2 = target.invoke(definitions.get(6));   // 4
        Object invoke8_1 = target.invoke(definitions.get(8));   // 5
        Object invoke8_2 = target.invoke(definitions.get(8));   // 5
        //
        assert invoke0 == testCallerFilter;
        assert invoke2 == testCallerFilter;
        assert invoke4_1 != invoke4_2;
        assert invoke6_1 == invoke6_2;
        assert invoke8_1 != invoke8_2;
    }
    //
    @Test
    public void filterTest2() throws Throwable {
        Method target = InvokeFilterDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final String[] urls = new String[] { "/abc.do", "/def.do" };
        final TestCallerFilter testCallerFilter = new TestCallerFilter();
        final Provider<TestCallerFilter> testCallerFilterProvider = InstanceProvider.of(testCallerFilter);
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                BindInfo<TestCallerFilter> filterBindInfo1 = apiBinder.bindType(TestCallerFilter.class).asEagerSingleton().toInfo();
                BindInfo<TestCallerFilter> filterBindInfo2 = apiBinder.bindType(TestCallerFilter.class).toInfo();
                //
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(1, testCallerFilter);           // 1
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(2, testCallerFilterProvider);   // 2
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(3, TestCallerFilter.class);     // 3
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(4, filterBindInfo1);            // 4
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(5, filterBindInfo2);            // 5
            }
        });
        //
        List<AbstractDefinition> definitions = appContext.findBindingBean(AbstractDefinition.class);
        assert definitions.size() == 10;
        for (int i = 0; i < 10; i++) {
            definitions.get(i).init(new InvokerMapConfig(null, appContext));
        }
        //
        assert "/abc.do".equals(definitions.get(0).getPattern());
        assert "/abc.do".equals(definitions.get(2).getPattern());
        assert "/abc.do".equals(definitions.get(4).getPattern());
        assert "/abc.do".equals(definitions.get(6).getPattern());
        assert "/abc.do".equals(definitions.get(8).getPattern());
        assert definitions.get(0).getIndex() == 1;
        assert definitions.get(2).getIndex() == 2;
        assert definitions.get(4).getIndex() == 3;
        assert definitions.get(6).getIndex() == 4;
        assert definitions.get(8).getIndex() == 5;
        //
        assert "/def.do".equals(definitions.get(1).getPattern());
        assert "/def.do".equals(definitions.get(3).getPattern());
        assert "/def.do".equals(definitions.get(5).getPattern());
        assert "/def.do".equals(definitions.get(7).getPattern());
        assert "/def.do".equals(definitions.get(9).getPattern());
        assert definitions.get(1).getIndex() == 1;
        assert definitions.get(3).getIndex() == 2;
        assert definitions.get(5).getIndex() == 3;
        assert definitions.get(7).getIndex() == 4;
        assert definitions.get(9).getIndex() == 5;
        //
        Object invoke0 = target.invoke(definitions.get(0));     // 1
        Object invoke2 = target.invoke(definitions.get(2));     // 2
        Object invoke4_1 = target.invoke(definitions.get(4));   // 3
        Object invoke4_2 = target.invoke(definitions.get(4));   // 3
        Object invoke6_1 = target.invoke(definitions.get(6));   // 4
        Object invoke6_2 = target.invoke(definitions.get(6));   // 4
        Object invoke8_1 = target.invoke(definitions.get(8));   // 5
        Object invoke8_2 = target.invoke(definitions.get(8));   // 5
        //
        assert invoke0 == testCallerFilter;
        assert invoke2 == testCallerFilter;
        assert invoke4_1 != invoke4_2;
        assert invoke6_1 == invoke6_2;
        assert invoke8_1 != invoke8_2;
    }
    //
    @Test
    public void filterTest3() throws Throwable {
        Method target = InvokeFilterDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final String[] urls = new String[] { "/abc.do", "/def.do" };
        final TestCallerFilter testCallerFilter = new TestCallerFilter();
        final Provider<TestCallerFilter> testCallerFilterProvider = InstanceProvider.of(testCallerFilter);
        final Map<String, String> params1 = new HashMap<String, String>();
        params1.put("arg_string1", "abc");
        final Map<String, String> params2 = new HashMap<String, String>();
        params2.put("arg_string2", "def");
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                BindInfo<TestCallerFilter> filterBindInfo1 = apiBinder.bindType(TestCallerFilter.class).asEagerSingleton().toInfo();
                BindInfo<TestCallerFilter> filterBindInfo2 = apiBinder.bindType(TestCallerFilter.class).toInfo();
                //
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(1, testCallerFilter, params1);           // 1
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(2, testCallerFilterProvider, params1);   // 2
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(3, TestCallerFilter.class, params1);     // 3
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(4, filterBindInfo1, params1);            // 4
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(5, filterBindInfo2, params1);            // 5
            }
        });
        //
        List<AbstractDefinition> definitions = appContext.findBindingBean(AbstractDefinition.class);
        assert definitions.size() == 10;
        for (int i = 0; i < 10; i++) {
            definitions.get(i).init(new InvokerMapConfig(params2, appContext));
        }
        //
        for (int i = 0; i < 10; i++) {
            assert "abc".equals(definitions.get(i).getInitParams().get("arg_string1"));
            assert "def".equals(definitions.get(i).getInitParams().get("arg_string2"));
        }
        //
        assert "/abc.do".equals(definitions.get(0).getPattern());
        assert "/abc.do".equals(definitions.get(2).getPattern());
        assert "/abc.do".equals(definitions.get(4).getPattern());
        assert "/abc.do".equals(definitions.get(6).getPattern());
        assert "/abc.do".equals(definitions.get(8).getPattern());
        assert definitions.get(0).getIndex() == 1;
        assert definitions.get(2).getIndex() == 2;
        assert definitions.get(4).getIndex() == 3;
        assert definitions.get(6).getIndex() == 4;
        assert definitions.get(8).getIndex() == 5;
        //
        assert "/def.do".equals(definitions.get(1).getPattern());
        assert "/def.do".equals(definitions.get(3).getPattern());
        assert "/def.do".equals(definitions.get(5).getPattern());
        assert "/def.do".equals(definitions.get(7).getPattern());
        assert "/def.do".equals(definitions.get(9).getPattern());
        assert definitions.get(1).getIndex() == 1;
        assert definitions.get(3).getIndex() == 2;
        assert definitions.get(5).getIndex() == 3;
        assert definitions.get(7).getIndex() == 4;
        assert definitions.get(9).getIndex() == 5;
        //
        Object invoke0 = target.invoke(definitions.get(0));     // 1
        Object invoke2 = target.invoke(definitions.get(2));     // 2
        Object invoke4_1 = target.invoke(definitions.get(4));   // 3
        Object invoke4_2 = target.invoke(definitions.get(4));   // 3
        Object invoke6_1 = target.invoke(definitions.get(6));   // 4
        Object invoke6_2 = target.invoke(definitions.get(6));   // 4
        Object invoke8_1 = target.invoke(definitions.get(8));   // 5
        Object invoke8_2 = target.invoke(definitions.get(8));   // 5
        //
        assert invoke0 == testCallerFilter;
        assert invoke2 == testCallerFilter;
        assert invoke4_1 != invoke4_2;
        assert invoke6_1 == invoke6_2;
        assert invoke8_1 != invoke8_2;
    }
    //
    @Test
    public void filterTest4() throws Throwable {
        Method target = InvokeFilterDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final String[] urls = new String[] { "/abc.do", "/def.do" };
        final TestCallerFilter testCallerFilter = new TestCallerFilter();
        final Provider<TestCallerFilter> testCallerFilterProvider = InstanceProvider.of(testCallerFilter);
        final Map<String, String> params1 = new HashMap<String, String>();
        params1.put("arg_string1", "abc");
        final Map<String, String> params2 = new HashMap<String, String>();
        params2.put("arg_string2", "def");
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                BindInfo<TestCallerFilter> filterBindInfo1 = apiBinder.bindType(TestCallerFilter.class).asEagerSingleton().toInfo();
                BindInfo<TestCallerFilter> filterBindInfo2 = apiBinder.bindType(TestCallerFilter.class).toInfo();
                //
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(testCallerFilter, params1);           // 1
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(testCallerFilterProvider, params1);   // 2
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(TestCallerFilter.class, params1);     // 3
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(filterBindInfo1, params1);            // 4
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(filterBindInfo2, params1);            // 5
            }
        });
        //
        List<AbstractDefinition> definitions = appContext.findBindingBean(AbstractDefinition.class);
        assert definitions.size() == 10;
        for (int i = 0; i < 10; i++) {
            definitions.get(i).init(new InvokerMapConfig(params2, appContext));
        }
        //
        for (int i = 0; i < 10; i++) {
            assert "abc".equals(definitions.get(i).getInitParams().get("arg_string1"));
            assert "def".equals(definitions.get(i).getInitParams().get("arg_string2"));
        }
        //
        assert "/abc.do".equals(definitions.get(0).getPattern());
        assert "/abc.do".equals(definitions.get(2).getPattern());
        assert "/abc.do".equals(definitions.get(4).getPattern());
        assert "/abc.do".equals(definitions.get(6).getPattern());
        assert "/abc.do".equals(definitions.get(8).getPattern());
        //
        assert "/def.do".equals(definitions.get(1).getPattern());
        assert "/def.do".equals(definitions.get(3).getPattern());
        assert "/def.do".equals(definitions.get(5).getPattern());
        assert "/def.do".equals(definitions.get(7).getPattern());
        assert "/def.do".equals(definitions.get(9).getPattern());
        //
        for (int i = 0; i < 10; i++) {
            assert definitions.get(i).getIndex() == 0;
        }
        //
        Object invoke0 = target.invoke(definitions.get(0));     // 1
        Object invoke2 = target.invoke(definitions.get(2));     // 2
        Object invoke4_1 = target.invoke(definitions.get(4));   // 3
        Object invoke4_2 = target.invoke(definitions.get(4));   // 3
        Object invoke6_1 = target.invoke(definitions.get(6));   // 4
        Object invoke6_2 = target.invoke(definitions.get(6));   // 4
        Object invoke8_1 = target.invoke(definitions.get(8));   // 5
        Object invoke8_2 = target.invoke(definitions.get(8));   // 5
        //
        assert invoke0 == testCallerFilter;
        assert invoke2 == testCallerFilter;
        assert invoke4_1 != invoke4_2;
        assert invoke6_1 == invoke6_2;
        assert invoke8_1 != invoke8_2;
    }
    //
    @Test
    public void filterTest5() throws Throwable {
        Method target = InvokeFilterDefinition.class.getDeclaredMethod("getTarget");
        target.setAccessible(true);
        //
        final String[] urls = new String[] { "/abc.do", "/def.do" };
        final TestCallerFilter testCallerFilter = new TestCallerFilter();
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).filter(urls).through(testCallerFilter);        // 1
                apiBinder.tryCast(WebApiBinder.class).filterRegex(urls).through(testCallerFilter);   // 2
                apiBinder.tryCast(WebApiBinder.class).jeeFilter(urls).through(testCallerFilter);     // 3
                apiBinder.tryCast(WebApiBinder.class).jeeFilterRegex(urls).through(testCallerFilter);// 4
            }
        });
        //
        List<AbstractDefinition> definitions = appContext.findBindingBean(AbstractDefinition.class);
        assert definitions.size() == 8;
        //
        assert "/abc.do".equals(definitions.get(0).getPattern());
        assert "/abc.do".equals(definitions.get(2).getPattern());
        assert "/abc.do".equals(definitions.get(4).getPattern());
        assert "/abc.do".equals(definitions.get(6).getPattern());
        assert definitions.get(0) instanceof InvokeFilterDefinition;
        assert definitions.get(2) instanceof InvokeFilterDefinition;
        assert definitions.get(4) instanceof FilterDefinition;
        assert definitions.get(6) instanceof FilterDefinition;
        //
        assert "/def.do".equals(definitions.get(1).getPattern());
        assert "/def.do".equals(definitions.get(3).getPattern());
        assert "/def.do".equals(definitions.get(5).getPattern());
        assert "/def.do".equals(definitions.get(7).getPattern());
        assert definitions.get(1) instanceof InvokeFilterDefinition;
        assert definitions.get(3) instanceof InvokeFilterDefinition;
        assert definitions.get(5) instanceof FilterDefinition;
        assert definitions.get(7) instanceof FilterDefinition;
    }
}