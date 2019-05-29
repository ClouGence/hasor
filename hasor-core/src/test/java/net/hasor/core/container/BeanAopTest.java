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
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.container.aop.TestInterceptor;
import net.hasor.core.container.aop.multilayer.l2.FooFunction;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.core.info.AopBindInfoAdapter;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
public class BeanAopTest {
    @Test
    public void builderTest1() throws IOException {
        final StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        ApiBinder apiBinder = new AbstractBinder(environment) {
            @Override
            protected BeanBuilder getBeanBuilder() {
                return container;
            }
            @Override
            protected ScopManager getScopManager() {
                return container;
            }
        };
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        PowerMockito.when(appContext.findBindingRegister(AopBindInfoAdapter.class)).then((Answer<Object>) invocationOnMock -> {
            return container.findBindInfoList(AopBindInfoAdapter.class);
        });
        //
        //
        apiBinder.bindInterceptor(Matchers.anyClass(), Matchers.anyMethod(), new TestInterceptor());
        //
        TestInterceptor.resetInit();
        FooFunction instance = container.getProvider(FooFunction.class, appContext, null).get();
        assert !TestInterceptor.isCalled();
        assert !TestInterceptor.isThrowed();
        instance.fooCall("sss");
        assert TestInterceptor.isCalled();
        assert !TestInterceptor.isThrowed();
        //
        //
        TestInterceptor.resetInit();
        assert !TestInterceptor.isCalled();
        assert !TestInterceptor.isThrowed();
        try {
            instance.throwError("sss");
            assert false;
        } catch (Exception e) {
            assert true;
        }
        assert TestInterceptor.isCalled();
        assert TestInterceptor.isThrowed();
    }
}
