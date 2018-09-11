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
package test.net.hasor.core._07_binder;
import net.hasor.core.*;
import net.hasor.core.classcode.matcher.AopMatchers;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.scope.SingletonScope;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class BinderDataTest extends AbstractBinderDataTest {
    @Before
    public void beforeTest() throws IOException {
        super.beforeTest();
    }
    //
    @Test
    public void binderTest1() {
        binder.bindType(BinderDataTest.class);
        assert reference.get().getBindType() == BinderDataTest.class;
    }
    //
    @Test
    public void binderTest2() {
        List<Object> list = new ArrayList<Object>();
        binder.bindType(List.class, list);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getCustomerProvider().get() == list;
    }
    //
    @Test
    public void binderTest3() {
        binder.bindType(List.class, LinkedList.class);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getSourceType() == LinkedList.class;
    }
    //
    @Test
    public void binderTest4() {
        Date self = new Date();
        Provider<Date> selfProvider = new InstanceProvider<Date>(self);
        binder.bindType(Date.class, selfProvider);
        assert reference.get().getBindType() == Date.class;
        assert reference.get().getSourceType() == null;
        assert reference.get().getCustomerProvider() == selfProvider;
        assert reference.get().getCustomerProvider().get() == self;
    }
    //
    @Test
    public void binderTest5() {
        binder.bindType("abc", ArrayList.class);
        assert reference.get().getBindType() == ArrayList.class;
        assert !reference.get().getBindName().equals(reference.get().getBindID());
        assert reference.get().getBindName().equals("abc");
    }
    //
    @Test
    public void binderTest6() {
        List<Object> list = new ArrayList<Object>();
        binder.bindType("myList", List.class, list);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getCustomerProvider().get() == list;
        assert reference.get().getBindName().equalsIgnoreCase("myList");
    }
    //
    @Test
    public void binderTest7() {
        binder.bindType("myLinkedList", List.class, LinkedList.class);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getSourceType() == LinkedList.class;
        assert reference.get().getBindName().equalsIgnoreCase("myLinkedList");
    }
    //
    @Test
    public void binderTest8() {
        Date self = new Date();
        Provider<Date> selfProvider = new InstanceProvider<Date>(self);
        binder.bindType("myDate", Date.class, selfProvider);
        assert reference.get().getBindType() == Date.class;
        assert reference.get().getSourceType() == null;
        assert reference.get().getCustomerProvider() == selfProvider;
        assert reference.get().getCustomerProvider().get() == self;
        assert reference.get().getBindName().equalsIgnoreCase("myDate");
    }
    //
    @Test
    public void binderTest9() {
        binder.bindType(BinderDataTest.class).idWith("12345");
        assert reference.get().getBindType() == BinderDataTest.class;
        assert "12345".equals(reference.get().getBindID());
    }
    //
    @Test
    public void binderTest10() {
        try {
            ignoreType.add(Date.class);
            ignoreType.add(Timestamp.class);
            BindInfo<?> valueInfo = PowerMockito.mock(BindInfo.class);
            ApiBinder.InjectPropertyBindingBuilder<?> bindType = binder.bindType(TestBean.class);
            InstanceProvider<Object> valProvider = new InstanceProvider<Object>("val");
            bindType = bindType.inject("abc1", Timestamp.class);
            bindType = bindType.injectValue("abc2", 123);
            bindType = bindType.inject("abc3", valProvider);
            bindType = bindType.inject("abc4", valueInfo);
            Field innerField = DefaultBindInfoProviderAdapter.class.getDeclaredField("injectProperty");
            innerField.setAccessible(true);
            Map<String, Object> propertys = (Map<String, Object>) innerField.get(reference.get());
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("net.hasor.core.info.ParamInfo");
            Field paramTypeField = aClass.getField("paramType");
            Field useProviderField = aClass.getField("useProvider");
            Field valueInfoField = aClass.getField("valueInfo");
            Field valueProviderField = aClass.getField("valueProvider");
            paramTypeField.setAccessible(true);
            useProviderField.setAccessible(true);
            valueInfoField.setAccessible(true);
            valueProviderField.setAccessible(true);
            //
            assert reference.get().getBindType() == TestBean.class;
            assert propertys.containsKey("abc1") &&//
                    propertys.containsKey("abc2") &&//
                    propertys.containsKey("abc3") && //
                    propertys.containsKey("abc4");
            //
            assert paramTypeField.get(propertys.get("abc1")) == Date.class;
            assert ((DefaultBindInfoProviderAdapter) valueInfoField.get(propertys.get("abc1"))).getBindType() == Timestamp.class;
            //
            assert paramTypeField.get(propertys.get("abc2")) == Integer.TYPE;
            assert useProviderField.getBoolean(propertys.get("abc2"));
            assert ((Provider) valueProviderField.get(propertys.get("abc2"))).get().equals(123);
            //
            assert paramTypeField.get(propertys.get("abc3")) == Object.class;
            assert useProviderField.getBoolean(propertys.get("abc3"));
            assert valProvider == valueProviderField.get(propertys.get("abc3"));
            assert "val".equals(((Provider) valueProviderField.get(propertys.get("abc3"))).get());
            //
            assert paramTypeField.get(propertys.get("abc4")) == Method.class;
            assert !useProviderField.getBoolean(propertys.get("abc4"));
            assert valueInfoField.get(propertys.get("abc4")) == valueInfo;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            ignoreType.clear();
        }
    }
    //
    @Test
    public void binderTest11() {
        binder.bindType(TestBean.class).initMethod("doInit");
        assert reference.get().getInitMethod() != null;
    }
    //
    @Test
    public void binderTest12() {
        try {
            Constructor<ArrayList> constructor = ArrayList.class.getConstructor(Integer.TYPE);
            binder.bindType(List.class).toConstructor(constructor);
            Constructor refConstructor = reference.get().getConstructor(mockApp);
            assert refConstructor.equals(constructor); // 相同的构造方法，对象不相同（ Hasor 使用的是重找回方式）
            assert refConstructor != constructor;      // 构造方法对象不相同
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
    //
    @Test
    public void binderTest13() {
        binder.bindType(TestBean.class).metaData("metaKey", "metaValue");
        assert "metaValue".equals(reference.get().getMetaData("metaKey"));
    }
    //
    @Test
    public void binderTest14() {
        Method target = PowerMockito.mock(Method.class);
        binder.bindType(TestBean.class).metaData("metaKey", target);
        assert target == reference.get().getMetaData("metaKey");
    }
    //
    @Test
    public void binderTest15() {
        try {
            Constructor<ArrayList> constructor = ArrayList.class.getConstructor(Integer.TYPE);
            binder.bindType(List.class).toConstructor(constructor);
            Constructor refConstructor = reference.get().getConstructor(ArrayList.class, mockApp);
            assert refConstructor.equals(constructor); // 相同的构造方法，对象不相同（ Hasor 使用的是重找回方式）
            assert refConstructor != constructor;      // 构造方法对象不相同
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
    //
    @Test
    public void binderTest16() {
        binder.bindType(BinderDataTest.class).asEagerSingleton();
        assert reference.get().isSingleton();
        binder.bindType(BinderDataTest.class).asEagerPrototype();
        assert !reference.get().isSingleton();
    }
    //
    @Test
    public void binderTest17() {
        try {
            ignoreType.add(Date.class);
            ignoreType.add(Timestamp.class);
            BindInfo<?> valueInfo = PowerMockito.mock(BindInfo.class);
            Constructor<? extends TestBean2> constructor = TestBean2.class.getConstructor(Date.class, Integer.TYPE, Object.class, Method.class);
            ApiBinder.InjectConstructorBindingBuilder<TestBean2> bindType = binder.bindType(TestBean2.class).toConstructor(constructor);
            InstanceProvider<Object> valProvider = new InstanceProvider<Object>("val");
            bindType = bindType.inject(0, Timestamp.class);
            bindType = bindType.inject(2, valProvider);
            bindType = bindType.injectValue(1, 123);
            bindType = bindType.inject(3, valueInfo);
            Field innerField = DefaultBindInfoProviderAdapter.class.getDeclaredField("constructorParams");
            innerField.setAccessible(true);
            Map<String, Object> propertys = (Map<String, Object>) innerField.get(reference.get());
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("net.hasor.core.info.ParamInfo");
            Field paramTypeField = aClass.getField("paramType");
            Field useProviderField = aClass.getField("useProvider");
            Field valueInfoField = aClass.getField("valueInfo");
            Field valueProviderField = aClass.getField("valueProvider");
            paramTypeField.setAccessible(true);
            useProviderField.setAccessible(true);
            valueInfoField.setAccessible(true);
            valueProviderField.setAccessible(true);
            //
            assert reference.get().getBindType() == TestBean2.class;
            assert propertys.containsKey(0) &&//
                    propertys.containsKey(1) &&//
                    propertys.containsKey(2) && //
                    propertys.containsKey(3);
            //
            assert paramTypeField.get(propertys.get(0)) == Date.class;
            assert ((DefaultBindInfoProviderAdapter) valueInfoField.get(propertys.get(0))).getBindType() == Timestamp.class;
            //
            assert paramTypeField.get(propertys.get(1)) == Integer.TYPE;
            assert useProviderField.getBoolean(propertys.get(1));
            assert ((Provider) valueProviderField.get(propertys.get(1))).get().equals(123);
            //
            assert paramTypeField.get(propertys.get(2)) == Object.class;
            assert useProviderField.getBoolean(propertys.get(2));
            assert valProvider == valueProviderField.get(propertys.get(2));
            assert "val".equals(((Provider) valueProviderField.get(propertys.get(2))).get());
            //
            assert paramTypeField.get(propertys.get(3)) == Method.class;
            assert !useProviderField.getBoolean(propertys.get(3));
            assert valueInfoField.get(propertys.get(3)) == valueInfo;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            ignoreType.clear();
        }
    }
    //
    @Test
    public void binderTest18() {
        Scope mockScope = PowerMockito.mock(Scope.class);
        binder.bindType(TestBean.class).toScope(mockScope);
        assert reference.get().getScopeProvider().get() == mockScope;
    }
    //
    @Test
    public void binderTest19() {
        String scopeName1 = "my";
        String scopeName2 = "my2";
        final SingletonScope myScope = new SingletonScope();
        PowerMockito.when(scopManager.findScope(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if ("my".equals(invocationOnMock.getArguments()[0])) {
                    return new InstanceProvider<Scope>(myScope);
                }
                return null;
            }
        });
        binder.bindType(TestBean.class).toScope(scopeName1);
        assert reference.get().getScopeProvider().get() == myScope;
        try {
            binder.bindType(TestBean.class).toScope(scopeName2);
            assert false;
        } catch (IllegalStateException e) {
            assert e.getMessage().endsWith("scope '" + scopeName2 + "' Have not yet registered"); // scopeName2 尚未注册
        }
        //
        //
        final Map<String, Object> scopeMap = new HashMap<String, Object>();
        PowerMockito.when(scopManager.registerScope(anyString(), (Provider<Scope>) anyObject())).thenAnswer(new Answer<Provider<Scope>>() {
            @Override
            public Provider<Scope> answer(InvocationOnMock invocationOnMock) throws Throwable {
                scopeMap.put((String) invocationOnMock.getArguments()[0], invocationOnMock.getArguments()[1]);
                return (Provider<Scope>) invocationOnMock.getArguments()[1];
            }
        });
        Scope mockScope = PowerMockito.mock(Scope.class);
        binder.registerScope("testScope", mockScope);
        assert scopeMap.containsKey("testScope") && ((Provider) scopeMap.get("testScope")).get() == mockScope; // Scope 注册
        //
        InstanceProvider<Scope> scopeProvider = new InstanceProvider<Scope>(mockScope);
        binder.registerScope("scopeProvider", scopeProvider);
        assert scopeMap.containsKey("scopeProvider") && ((Provider) scopeMap.get("scopeProvider")).get() == mockScope; // Scope 注册
    }
    //
    @Test
    public void binderTest20() {
        try {
            binder.installModule(PowerMockito.mock(Module.class));
            assert false;
        } catch (Throwable e) {
            assert "current state is not allowed.".equals(e.getMessage());
        }
        //
        try {
            binder.tryCast(TestBinder.class);
            assert false;
        } catch (Throwable e) {
            assert "current state is not allowed.".equals(e.getMessage());
        }
    }
    //
    @Test
    public void binderTest21() {
        binder.getBindInfo("tttt");
        binder.getBindInfo(TestBinder.class);
        binder.findBindingRegister("", TestBinder.class);
        binder.findBindingRegister(TestBinder.class);
    }
    //
    @Test
    public void binderTest22() {
        MethodInterceptor interceptor = PowerMockito.mock(MethodInterceptor.class);
        //
        try {
            binder.bindInterceptor("xxx", interceptor);
            AopBindInfoAdapter aopAdapter = (AopBindInfoAdapter) reference.get().getCustomerProvider().get();
            Field declaredField = AopBindInfoAdapter.class.getDeclaredField("interceptor");
            declaredField.setAccessible(true);
            assert declaredField.get(aopAdapter) == interceptor;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
        //
        //
        try {
            Matcher<Class<?>> matcherClass = AopMatchers.anyClass();
            Matcher<Method> matcherMethod = AopMatchers.anyMethod();
            binder.bindInterceptor(matcherClass, matcherMethod, interceptor);
            AopBindInfoAdapter aopAdapter = (AopBindInfoAdapter) reference.get().getCustomerProvider().get();
            Field interceptorField = AopBindInfoAdapter.class.getDeclaredField("interceptor");
            Field matcherClassField = AopBindInfoAdapter.class.getDeclaredField("matcherClass");
            Field matcherMethodField = AopBindInfoAdapter.class.getDeclaredField("matcherMethod");
            interceptorField.setAccessible(true);
            matcherClassField.setAccessible(true);
            matcherMethodField.setAccessible(true);
            //
            assert interceptorField.get(aopAdapter) == interceptor;
            assert matcherClassField.get(aopAdapter) == matcherClass;
            assert matcherMethodField.get(aopAdapter) == matcherMethod;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
    //
    @Test
    public void binderTest23() {
        assert binder.findClass(null) == null;
        assert !binder.findClass(ApiBinder.class).isEmpty();
        assert binder.findClass(null, null) == null;
        assert !binder.findClass(ApiBinder.class, new String[] { "test.net.hasor.core._07_binder" }).isEmpty();
        assert binder.getEnvironment() != null;
    }
}