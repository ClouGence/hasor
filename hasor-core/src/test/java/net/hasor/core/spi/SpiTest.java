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
package net.hasor.core.spi;
import net.hasor.core.*;
import net.hasor.test.core.event.AppContextListener;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SpiTest {
    @Test
    public void awareTest1() {
        AtomicReference<AppContext> reference1 = new AtomicReference<>();
        AtomicReference<AppContext> reference2 = new AtomicReference<>();
        AppContextAware aware1 = reference1::set;
        AppContextAware aware2 = reference2::set;
        //
        AppContext appContext = Hasor.create().build(apiBinder -> {
            assert aware1 == HasorUtils.autoAware(apiBinder.getEnvironment(), aware1);
            Supplier<AppContextAware> awareSupplier = () -> aware2;
            assert awareSupplier == HasorUtils.autoAware(apiBinder.getEnvironment(), awareSupplier);
        });
        //
        assert reference1.get() != null;
        assert reference1.get() == appContext;
        assert reference2.get() != null;
        assert reference2.get() == appContext;
    }

    @Test
    public void startListenerTest1() {
        AppContextListener listener1 = new AppContextListener();
        AppContextListener listener2 = new AppContextListener();
        //
        AppContext appContext = Hasor.create().build(apiBinder -> {
            assert listener1 == HasorUtils.pushStartListener(apiBinder.getEnvironment(), listener1);
            BindInfo<EventListener> bindInfo = apiBinder.bindType(EventListener.class).toInstance(listener2).toInfo();
            assert bindInfo == HasorUtils.pushStartListener(apiBinder.getEnvironment(), bindInfo);
        });
        //
        assert listener1.getAppContext() == appContext;
        assert listener2.getAppContext() == appContext;
        assert listener1.getCount() == 1;
        assert listener2.getCount() == 1;
    }

    @Test
    public void stopListenerTest1() {
        AppContextListener listener1 = new AppContextListener();
        AppContextListener listener2 = new AppContextListener();
        //
        AppContext appContext = Hasor.create().build(apiBinder -> {
            assert listener1 == HasorUtils.pushShutdownListener(apiBinder.getEnvironment(), listener1);
            BindInfo<EventListener> bindInfo = apiBinder.bindType(EventListener.class).toInstance(listener2).toInfo();
            assert bindInfo == HasorUtils.pushShutdownListener(apiBinder.getEnvironment(), bindInfo);
        });
        //
        assert listener1.getAppContext() == null;
        assert listener2.getAppContext() == null;
        assert listener1.getCount() == 0;
        assert listener2.getCount() == 0;
        //
        appContext.shutdown();
        //
        assert listener1.getAppContext() == appContext;
        assert listener2.getAppContext() == appContext;
        assert listener1.getCount() == 1;
        assert listener2.getCount() == 1;
    }
}