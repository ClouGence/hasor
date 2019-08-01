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
import net.hasor.test.beans.binder.TestBinder;
import net.hasor.test.beans.binder.TestBinderCreater;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyObject;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HasorUtils.class })
public class InvocationHandlerTest {
    @Test
    public void handlerTest() {
        Map<Class<?>, Object> supportMap = new HashMap<>();
        ApiBinderInvocationHandler handler1 = new ApiBinderInvocationHandler(supportMap);
        assert handler1.supportMap().isEmpty();
        //
        Object val = new Object();
        supportMap.put(Object.class, val);
        ApiBinderInvocationHandler handler2 = new ApiBinderInvocationHandler(supportMap);
        assert handler2.supportMap().size() == 1;
        assert handler2.supportMap().get(Object.class) == val;
        //
        try {
            supportMap.put(ApiBinderInvocationHandler.class, null);
            new ApiBinderInvocationHandler(supportMap);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().startsWith("this method is not support -> ");
        }
    }

    @Test
    public void binderTest() throws Throwable {
        PowerMockito.mockStatic(HasorUtils.class);
        ArrayList<Object> ref1 = new ArrayList<>();
        ArrayList<Object> ref2 = new ArrayList<>();
        PowerMockito.when(HasorUtils.pushStartListener(anyObject(), (EventListener) anyObject())).then(invocationOnMock -> {
            ref1.add(invocationOnMock.getArguments()[1]);
            return null;
        });
        PowerMockito.when(HasorUtils.pushShutdownListener(anyObject(), (EventListener) anyObject())).then(invocationOnMock -> {
            ref2.add(invocationOnMock.getArguments()[1]);
            return null;
        });
        //
        //
        Environment env = new StandardEnvironment(null);
        BeanContainer container = new BeanContainer(env);
        ApiBinderWrap binder = new ApiBinderWrap(new AbstractBinder(env) {
            protected BindInfoBuilderFactory containerFactory() {
                return container;
            }
        });
        container.preInitialize();
        //
        Map<Class<?>, Object> supportMap = new HashMap<>();
        supportMap.put(ApiBinder.class, binder);
        supportMap.put(TestBinder.class, new TestBinderCreater().createBinder(binder));
        //
        ApiBinder binderProxy = (ApiBinder) Proxy.newProxyInstance(  //
                Thread.currentThread().getContextClassLoader(), //
                supportMap.keySet().toArray(new Class<?>[0]),   //
                new ApiBinderInvocationHandler(supportMap)      //
        );
        //
        binderProxy.installModule((Module) apiBinder -> {
            apiBinder.tryCast(TestBinder.class).hello();
            //
            assert apiBinder.toString().startsWith("count = 2 - [");
        });
        //
        container.init();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        BindInfo<String> bindInfo = container.getBindInfoContainer().findBindInfo("", String.class);
        String message = container.providerOnlyBindInfo(bindInfo, appContext).get();
        assert "hello Binder".equals(message);
        assert ref1.size() == 1;
        assert ref2.size() == 1;
    }
}