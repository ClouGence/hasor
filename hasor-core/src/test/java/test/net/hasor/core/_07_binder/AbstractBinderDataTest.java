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
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.container.BeanBuilder;
import net.hasor.core.container.ScopManager;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Matchers.anyObject;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AbstractBinderDataTest {
    protected Logger                                          logger = LoggerFactory.getLogger(getClass());
    protected Set<Class<?>>                                   ignoreType;
    protected AtomicReference<DefaultBindInfoProviderAdapter> reference;
    protected AppContext                                      mockApp;
    protected ScopManager                                     scopManager;
    protected ApiBinder                                       binder;
    //
    public void beforeTest() throws IOException {
        this.ignoreType = new HashSet<Class<?>>();
        this.reference = new AtomicReference<DefaultBindInfoProviderAdapter>();
        this.mockApp = PowerMockito.mock(AppContext.class);
        this.scopManager = PowerMockito.mock(ScopManager.class);
        StandardEnvironment env = new StandardEnvironment(null, null);
        final BeanBuilder builder = PowerMockito.mock(BeanBuilder.class);
        PowerMockito.when(builder.createInfoAdapter((Class<?>) anyObject(), (Class<?>) anyObject())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Class<Object> targetType = (Class<Object>) invocationOnMock.getArguments()[0];
                if (ignoreType.contains(targetType)) {
                    return new DefaultBindInfoProviderAdapter<Object>(targetType);
                }
                reference.set(new DefaultBindInfoProviderAdapter<Object>(targetType));
                return reference.get();
            }
        });
        //
        this.binder = new AbstractBinder(env) {
            @Override
            protected BeanBuilder getBeanBuilder() {
                return builder;
            }
            @Override
            protected ScopManager getScopManager() {
                return scopManager;
            }
        };
    }
}