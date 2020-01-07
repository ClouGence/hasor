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
import net.hasor.core.Scope;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.spi.BindInfoProvisionListener;
import net.hasor.core.spi.ScopeProvisionListener;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.function.Supplier;

public class SpiCallerContainerTest {
    //
    @Test
    public void spiTest1() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        assert spiCallerContainer.getListenerTypeSize() == 0;
        assert spiCallerContainer.getListenerSize() == 0;
        //
        BindInfoProvisionListener listener1 = PowerMockito.mock(BindInfoProvisionListener.class);
        spiCallerContainer.addListener(BindInfoProvisionListener.class, InstanceProvider.of(listener1));
        spiCallerContainer.addListener(BindInfoProvisionListener.class, InstanceProvider.of(listener1));
        ScopeProvisionListener listener2 = PowerMockito.mock(ScopeProvisionListener.class);
        spiCallerContainer.addListener(ScopeProvisionListener.class, InstanceProvider.of(listener2));
        spiCallerContainer.addListener(ScopeProvisionListener.class, InstanceProvider.of(listener2));
        spiCallerContainer.addListener(ScopeProvisionListener.class, InstanceProvider.of(listener2));
        //
        assert spiCallerContainer.getListenerTypeSize() == 2;
        assert spiCallerContainer.getListenerSize() == 5;
        assert spiCallerContainer.getEventListenerList(BindInfoProvisionListener.class).size() == 2;
        assert spiCallerContainer.getEventListenerList(ScopeProvisionListener.class).size() == 3;
        assert spiCallerContainer.getEventListenerList(EventListener.class) == null;
        //
        HashSet<Object> keyHashSet = new HashSet<>();
        HashSet<Object> valueHashSet = new HashSet<>();
        spiCallerContainer.forEachListener(entry -> {
            keyHashSet.add(entry.getKey());
            valueHashSet.add(entry.getValue());
        });
        assert keyHashSet.size() == 2;
        assert valueHashSet.size() == 2;
        //
        spiCallerContainer.init();
        spiCallerContainer.close();
        assert spiCallerContainer.getListenerTypeSize() == 0;
        assert spiCallerContainer.getListenerSize() == 0;
    }

    @Test
    public void spiTest2() {
        ArrayList<Object> receive = new ArrayList<>();
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        spiCallerContainer.addListener(ScopeProvisionListener.class, InstanceProvider.of((scopeName, scopeSupplier) -> {
            receive.add(scopeSupplier);
        }));
        //
        assert receive.size() == 0;
        //
        Supplier<? extends Scope> mockScope = PowerMockito.mock(Supplier.class);
        spiCallerContainer.callSpi(ScopeProvisionListener.class, listener -> {
            listener.newScope("xxxx", mockScope);
        });
        //
        assert receive.size() == 1;
        assert receive.get(0) == mockScope;
    }

    @Test
    public void spiTest3() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        spiCallerContainer.init();
        //
        ScopeProvisionListener listener = PowerMockito.mock(ScopeProvisionListener.class);
        spiCallerContainer.addListener(ScopeProvisionListener.class, InstanceProvider.of(listener));
        //
        spiCallerContainer.forEachListener(entry -> {
            try {
                entry.setValue(null);
                assert false;
            } catch (Exception e) {
                assert e.getMessage().equals("this entry no support.");
            }
        });
    }
}