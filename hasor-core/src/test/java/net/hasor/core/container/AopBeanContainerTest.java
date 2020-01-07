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
package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.aop.DynamicClass;
import net.hasor.core.info.AopBindInfoAdapter;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.test.core.aop.custom.MyAopInterceptor;
import net.hasor.test.core.aop.ignore.level.LevelFooFunction;
import net.hasor.test.core.aop.ignore.level.l2.L2FooFunction;
import net.hasor.test.core.aop.ignore.thread.ThreadFooFunction;
import net.hasor.test.core.aop.ignore.types.*;
import net.hasor.test.core.basic.pojo.PojoBean;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class AopBeanContainerTest {
    private AppContext appContext = null;

    @Before
    public void beforeTest() {
        this.appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void aopTest1() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        BeanContainer container = new BeanContainer(mockEnv);
        container.preInitialize();
        //
        DefaultBindInfoProviderAdapter<AopBindInfoAdapter> adapter = container.getBindInfoContainer().createInfoAdapter(AopBindInfoAdapter.class, null);
        Predicate<Class<?>> ma = aClass -> true;
        Predicate<Method> mb = aMethod -> true;
        MyAopInterceptor.resetInit();
        MethodInterceptor interceptor = new MyAopInterceptor();
        adapter.setCustomerProvider(InstanceProvider.of(new AopBindInfoAdapter(ma, mb, interceptor)));
        //
        PojoBean bean = container.providerOnlyType(PojoBean.class, appContext, null).get();
        //
        assert !MyAopInterceptor.isCalled();
        bean.setUuid("abc");
        assert MyAopInterceptor.isCalled();
        MyAopInterceptor.resetInit();
    }

    @Test
    public void aopTest2() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        BeanContainer container = new BeanContainer(mockEnv);
        container.preInitialize();
        //
        DefaultBindInfoProviderAdapter<AopBindInfoAdapter> adapter = container.getBindInfoContainer().createInfoAdapter(AopBindInfoAdapter.class, null);
        Predicate<Class<?>> ma = aClass -> false;
        Predicate<Method> mb = aMethod -> true;
        MyAopInterceptor.resetInit();
        MethodInterceptor interceptor = new MyAopInterceptor();
        adapter.setCustomerProvider(InstanceProvider.of(new AopBindInfoAdapter(ma, mb, interceptor)));
        //
        PojoBean bean = container.providerOnlyType(PojoBean.class, appContext, null).get();
        //
        assert !MyAopInterceptor.isCalled();
        bean.setUuid("abc");
        assert !MyAopInterceptor.isCalled();
        MyAopInterceptor.resetInit();
    }

    @Test
    public void aopTest3() {
        Environment mockEnv = PowerMockito.mock(Environment.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        BeanContainer container = new BeanContainer(mockEnv);
        container.preInitialize();
        //
        DefaultBindInfoProviderAdapter<AopBindInfoAdapter> adapter = container.getBindInfoContainer().createInfoAdapter(AopBindInfoAdapter.class, null);
        Predicate<Class<?>> ma = aClass -> true;
        Predicate<Method> mb = aMethod -> true;
        MethodInterceptor interceptor = new MyAopInterceptor();
        adapter.setCustomerProvider(InstanceProvider.of(new AopBindInfoAdapter(ma, mb, interceptor)));
        //
        {
            GrandFatherBean bean1 = container.providerOnlyType(GrandFatherBean.class, appContext, null).get();
            JamesBean bean2 = container.providerOnlyType(JamesBean.class, appContext, null).get();
            JamesSonBean bean3 = container.providerOnlyType(JamesSonBean.class, appContext, null).get();
            WilliamBean bean4 = container.providerOnlyType(WilliamBean.class, appContext, null).get();
            WilliamSonBean bean5 = container.providerOnlyType(WilliamSonBean.class, appContext, null).get();
            assert bean1 instanceof DynamicClass;
            assert !(bean2 instanceof DynamicClass);
            assert bean3 instanceof DynamicClass;
            assert !(bean4 instanceof DynamicClass);
            assert !(bean5 instanceof DynamicClass);
        }
        //
        {
            L2FooFunction bean1 = container.providerOnlyType(L2FooFunction.class, appContext, null).get();
            LevelFooFunction bean2 = container.providerOnlyType(LevelFooFunction.class, appContext, null).get();
            ThreadFooFunction bean3 = container.providerOnlyType(ThreadFooFunction.class, appContext, null).get();
            assert !(bean1 instanceof DynamicClass);
            assert bean2 instanceof DynamicClass;
            assert !(bean3 instanceof DynamicClass);
        }
        //
        //
    }
}