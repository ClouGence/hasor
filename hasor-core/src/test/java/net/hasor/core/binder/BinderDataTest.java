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
package net.hasor.core.binder;
import net.hasor.core.*;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.spi.BindInfoProvisionListener;
import net.hasor.test.core.basic.init.SingletonPublicCallInitBean;
import net.hasor.test.core.basic.init.WithoutAnnoCallInitBean;
import net.hasor.test.core.basic.pojo.PojoBean;
import net.hasor.test.core.basic.pojo.PojoBeanTestBeanC;
import net.hasor.test.core.basic.pojo.PojoBeanTestBeanP;
import net.hasor.test.core.basic.pojo.SampleBean;
import net.hasor.test.core.binder.TestBinder;
import net.hasor.test.core.scope.My;
import net.hasor.test.core.scope.MyScope;
import net.hasor.core.Provider;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.EventListener;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class BinderDataTest extends AbstractBinderDataTest {
    @Before
    public void beforeTest() throws IOException {
        super.beforeTest();
    }

    @Test
    public void metaDataTest1() {
        binder.bindType(BinderDataTest.class);
        assert reference.get().getBindType() == BinderDataTest.class;
        //
        binder.bindType(BinderDataTest.class).metaData("test", "value");
        assert "value".equals(reference.get().getMetaData("test"));
        reference.get().removeMetaData("test");
    }

    @Test
    public void metaDataTest2() {
        binder.bindType(PojoBeanTestBeanP.class).metaData("metaKey", "metaValue");
        assert "metaValue".equals(reference.get().getMetaData("metaKey"));
    }

    @Test
    public void metaDataTest3() {
        Method target = PowerMockito.mock(Method.class);
        binder.bindType(PojoBeanTestBeanP.class).metaData("metaKey", target);
        assert target == reference.get().getMetaData("metaKey");
    }

    @Test
    public void bindTest1() {
        List<Object> list = new ArrayList<>();
        binder.bindType(List.class, list);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getCustomerProvider().get() == list;
    }

    @Test
    public void bindTest2() {
        binder.bindType(List.class, LinkedList.class);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getSourceType() == LinkedList.class;
    }

    @Test
    public void bindTest3() {
        Date self = new Date();
        Supplier<Date> selfProvider = Provider.of(self);
        binder.bindType(Date.class, selfProvider);
        assert reference.get().getBindType() == Date.class;
        assert reference.get().getSourceType() == null;
        assert reference.get().getCustomerProvider() == selfProvider;
        assert reference.get().getCustomerProvider().get() == self;
    }

    @Test
    public void bindTest4() {
        binder.bindType("abc", ArrayList.class);
        assert reference.get().getBindType() == ArrayList.class;
        assert !reference.get().getBindName().equals(reference.get().getBindID());
        assert reference.get().getBindName().equals("abc");
    }

    @Test
    public void bindTest5() {
        List<Object> list = new ArrayList<>();
        binder.bindType("myList", List.class, list);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getCustomerProvider().get() == list;
        assert reference.get().getBindName().equalsIgnoreCase("myList");
    }

    @Test
    public void bindTest6() {
        binder.bindType("myLinkedList", List.class, LinkedList.class);
        assert reference.get().getBindType() == List.class;
        assert reference.get().getSourceType() == LinkedList.class;
        assert reference.get().getBindName().equalsIgnoreCase("myLinkedList");
    }

    @Test
    public void bindTest7() {
        Date self = new Date();
        Supplier<Date> selfProvider = Provider.of(self);
        binder.bindType("myDate", Date.class, selfProvider);
        assert reference.get().getBindType() == Date.class;
        assert reference.get().getSourceType() == null;
        assert reference.get().getCustomerProvider() == selfProvider;
        assert reference.get().getCustomerProvider().get() == self;
        assert reference.get().getBindName().equalsIgnoreCase("myDate");
    }

    @Test
    public void bindTest8() {
        binder.bindType(BinderDataTest.class).idWith("12345");
        assert reference.get().getBindType() == BinderDataTest.class;
        assert "12345".equals(reference.get().getBindID());
        assert reference.get().getBindName() == null;
        //
        binder.bindType(BinderDataTest.class).bothWith("12345");
        assert "12345".equals(reference.get().getBindID());
        assert "12345".equals(reference.get().getBindName());
    }

    @Test
    public void bindTest9() {
        binder.getBindInfo("tttt");
        binder.getBindInfo(TestBinder.class);
        binder.findBindingRegister("", TestBinder.class);
        binder.findBindingRegister(TestBinder.class);
    }

    @Test
    public void lifeTest1() {
        binder.bindType(PojoBeanTestBeanP.class).initMethod("doInit");
        DefaultBindInfoProviderAdapter<?> adapter = reference.get();
        Class<?> targetType = adapter.getSourceType() != null ? adapter.getSourceType() : adapter.getBindType();
        assert adapter.getInitMethod(targetType) != null;
    }

    @Test
    public void lifeTest2() {
        binder.bindType(PojoBeanTestBeanP.class).destroyMethod("doDestroy");
        DefaultBindInfoProviderAdapter<?> adapter = reference.get();
        Class<?> targetType = adapter.getSourceType() != null ? adapter.getSourceType() : adapter.getBindType();
        assert adapter.getDestroyMethod(targetType) != null;
    }

    @Test
    public void injectTest1() throws Exception {
        try {
            ignoreMatcher = aClass -> {
                return aClass != PojoBeanTestBeanP.class;
            };
            //
            BindInfo<?> valueInfo = PowerMockito.mock(BindInfo.class);
            Provider<Object> valProvider = Provider.of("val");
            //
            //
            Field innerField = DefaultBindInfoProviderAdapter.class.getDeclaredField("injectProperty");
            innerField.setAccessible(true);
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("net.hasor.core.info.ParamInfo");
            //
            {
                ApiBinder.InjectPropertyBindingBuilder<?> bindType = binder.bindType(PojoBeanTestBeanP.class);
                bindType.inject("abc1", Timestamp.class);
                bindType.injectValue("abc2", 123);
                bindType.inject("abc3", valProvider);
                bindType.inject("abc4", valueInfo);
            }
            //
            Field paramTypeField = aClass.getField("paramType");
            Field useProviderField = aClass.getField("useProvider");
            Field valueInfoField = aClass.getField("valueInfo");
            Field valueProviderField = aClass.getField("valueProvider");
            paramTypeField.setAccessible(true);
            useProviderField.setAccessible(true);
            valueInfoField.setAccessible(true);
            valueProviderField.setAccessible(true);
            Map<String, Object> propertys = (Map<String, Object>) innerField.get(reference.get());
            //
            {
                assert reference.get().getBindType() == PojoBeanTestBeanP.class;
                assert propertys.containsKey("abc1") &&//
                        propertys.containsKey("abc2") &&//
                        propertys.containsKey("abc3") && //
                        propertys.containsKey("abc4");
            }
            //
            assert paramTypeField.get(propertys.get("abc1")) == Date.class;
            assert ((DefaultBindInfoProviderAdapter) valueInfoField.get(propertys.get("abc1"))).getBindType() == Timestamp.class;
            //
            assert paramTypeField.get(propertys.get("abc2")) == Integer.TYPE;
            assert useProviderField.getBoolean(propertys.get("abc2"));
            assert ((Supplier) valueProviderField.get(propertys.get("abc2"))).get().equals(123);
            //
            assert paramTypeField.get(propertys.get("abc3")) == Object.class;
            assert useProviderField.getBoolean(propertys.get("abc3"));
            assert valProvider == valueProviderField.get(propertys.get("abc3"));
            assert "val".equals(((Supplier) valueProviderField.get(propertys.get("abc3"))).get());
            //
            assert paramTypeField.get(propertys.get("abc4")) == Method.class;
            assert !useProviderField.getBoolean(propertys.get("abc4"));
            assert valueInfoField.get(propertys.get("abc4")) == valueInfo;
        } finally {
            ignoreMatcher = null;
        }
    }

    @Test
    public void injectTest2() throws Exception {
        try {
            ignoreMatcher = aClass -> {
                return aClass != PojoBeanTestBeanC.class;
            };
            //
            BindInfo<?> valueInfo = PowerMockito.mock(BindInfo.class);
            Provider<Object> valProvider = Provider.of("val");
            //
            //
            Field innerField = DefaultBindInfoProviderAdapter.class.getDeclaredField("constructorParams");
            innerField.setAccessible(true);
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass("net.hasor.core.info.ParamInfo");
            //
            {
                Constructor<PojoBeanTestBeanC> constructor = PojoBeanTestBeanC.class.getConstructor(Date.class, Integer.TYPE, Object.class, Method.class);
                ApiBinder.InjectConstructorBindingBuilder<PojoBeanTestBeanC> bindType = binder.bindType(PojoBeanTestBeanC.class).toConstructor(constructor);
                bindType.inject(0, Timestamp.class);
                bindType.injectValue(1, 123);
                bindType.inject(2, valProvider);
                bindType.inject(3, valueInfo);
            }
            //
            Field paramTypeField = aClass.getField("paramType");
            Field useProviderField = aClass.getField("useProvider");
            Field valueInfoField = aClass.getField("valueInfo");
            Field valueProviderField = aClass.getField("valueProvider");
            paramTypeField.setAccessible(true);
            useProviderField.setAccessible(true);
            valueInfoField.setAccessible(true);
            valueProviderField.setAccessible(true);
            Map<String, Object> propertys = (Map<String, Object>) innerField.get(reference.get());
            //
            {
                assert reference.get().getBindType() == PojoBeanTestBeanC.class;
                assert propertys.containsKey(0) &&//
                        propertys.containsKey(1) &&//
                        propertys.containsKey(2) && //
                        propertys.containsKey(3);
            }
            //
            assert paramTypeField.get(propertys.get(0)) == Date.class;
            assert ((DefaultBindInfoProviderAdapter) valueInfoField.get(propertys.get(0))).getBindType() == Timestamp.class;
            //
            assert paramTypeField.get(propertys.get(1)) == Integer.TYPE;
            assert useProviderField.getBoolean(propertys.get(1));
            assert ((Supplier) valueProviderField.get(propertys.get(1))).get().equals(123);
            //
            assert paramTypeField.get(propertys.get(2)) == Object.class;
            assert useProviderField.getBoolean(propertys.get(2));
            assert valProvider == valueProviderField.get(propertys.get(2));
            assert "val".equals(((Supplier) valueProviderField.get(propertys.get(2))).get());
            //
            assert paramTypeField.get(propertys.get(3)) == Method.class;
            assert !useProviderField.getBoolean(propertys.get(3));
            assert valueInfoField.get(propertys.get(3)) == valueInfo;
        } finally {
            ignoreMatcher = null;
        }
    }

    @Test
    public void aopTest1() {
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
            Predicate<Class<?>> matcherClass = Matchers.anyClass();
            Predicate<Method> matcherMethod = Matchers.anyMethod();
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

    @Test
    public void otherTest2() {
        assert binder.findClass(null) == null;
        assert !binder.findClass(ApiBinder.class).isEmpty();
        assert binder.findClass(null, (String) null) == null;
        assert !binder.findClass(ApiBinder.class, new String[] { "test.net.hasor.core._07_binder" }).isEmpty();
        assert binder.getEnvironment() != null;
    }

    @Test
    public void otherTest3() throws IOException {
        Environment env = new StandardEnvironment(null);
        BeanContainer container = new BeanContainer(env);
        ApiBinderWrap binder = new ApiBinderWrap(newAbstractBinder(env, container));
        container.preInitialize();
        //
        MyScope myScope1 = new MyScope();
        binder.bindScope(My.class, myScope1);
        binder.bindType(PojoBean.class).idWith("aa").toScope(My.class);
        //
        MyScope myScope2 = new MyScope();
        binder.bindType(SampleBean.class).idWith("bb").toScope(myScope2);
        //
        container.init();
        BindInfo<Object> bindInfo1 = container.getBindInfoContainer().findBindInfo("aa");
        Supplier<Scope>[] collectScope1 = container.getScopeContainer().collectScope(bindInfo1);
        assert collectScope1[0].get() == myScope1;
        //
        BindInfo<Object> bindInfo2 = container.getBindInfoContainer().findBindInfo("bb");
        Supplier<Scope>[] collectScope2 = container.getScopeContainer().collectScope(bindInfo2);
        assert collectScope2[0].get() == myScope2;
    }

    @Test
    public void otherTest5() throws IOException {
        Environment env = new StandardEnvironment(null);
        BeanContainer container = new BeanContainer(env);
        ApiBinderWrap binder = new ApiBinderWrap(newAbstractBinder(env, container));
        container.preInitialize();
        //
        BindInfoProvisionListener listener = PowerMockito.mock(BindInfoProvisionListener.class);
        binder.bindSpiListener(BindInfoProvisionListener.class, listener);
        //
        List<Supplier<EventListener>> list = container.getSpiContainer().getEventListenerList(BindInfoProvisionListener.class);
        assert list.size() == 1;
        assert list.get(0).get() == listener;
    }

    @Test
    public void otherTest6() throws IOException {
        Environment env = new StandardEnvironment(null);
        BeanContainer container = new BeanContainer(env);
        ApiBinderWrap binder = new ApiBinderWrap(newAbstractBinder(env, container));
        container.preInitialize();
        //
        MyScope myScope1 = new MyScope();
        MyScope myScope2 = new MyScope();
        binder.bindType(SampleBean.class).idWith("aa").toScope(myScope1, myScope2);
        //
        container.init();
        BindInfo<Object> bindInfo = container.getBindInfoContainer().findBindInfo("aa");
        Supplier<Scope>[] collectScope = container.getScopeContainer().collectScope(bindInfo);
        assert collectScope[0].get() == myScope1;
        assert collectScope[1].get() == myScope2;
    }

    @Test
    public void singletonTest1() throws IOException {
        Environment env = new StandardEnvironment(null);
        BeanContainer container = new BeanContainer(env);
        ApiBinderWrap binder = new ApiBinderWrap(newAbstractBinder(env, container));
        container.preInitialize();
        //
        assert binder.isSingleton(SingletonPublicCallInitBean.class);
        assert !binder.isSingleton(WithoutAnnoCallInitBean.class);
        //
        BindInfo<?> bindInfo1 = binder.bindType(SampleBean.class).toInfo();
        BindInfo<?> bindInfo2 = binder.bindType(SingletonPublicCallInitBean.class).toInfo();
        BindInfo<?> bindInfo3 = binder.bindType(WithoutAnnoCallInitBean.class).asEagerSingleton().toInfo();
        //
        assert !binder.isSingleton(bindInfo1);
        assert binder.isSingleton(bindInfo2);
        assert binder.isSingleton(bindInfo3);
        //
        assert binder.isSingleton(SingletonPublicCallInitBean.class);
        assert binder.isSingleton(WithoutAnnoCallInitBean.class);
    }
}
