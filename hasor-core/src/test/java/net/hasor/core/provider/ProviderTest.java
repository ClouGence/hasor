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
package net.hasor.core.provider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ProviderTest {
    @Test
    public void providerTest1() {
        Supplier<List> ofList = InstanceProvider.of(new ArrayList());
        ClassLoaderSingleProvider<List> provider = new ClassLoaderSingleProvider<>(ofList);
        provider.toString();
        //
        assert provider.get() != null;
        assert provider.get() == provider.get();
        //
        ArrayList obj = new ArrayList();
        Supplier<ArrayList> ofListWrap = InstanceProvider.wrap(obj);
        assert ofListWrap.get() == obj;
    }

    @Test
    public void providerTest2() {
        //
        Supplier<List> listProvider = InstanceProvider.of(new ArrayList());
        SingleProvider<List> singleProvider = new SingleProvider<>(listProvider);
        singleProvider.toString();
        //
        assert singleProvider.get() == singleProvider.get();
    }

    @Test
    public void providerTest3() {
        //
        InstanceProvider<List> listProvider = new InstanceProvider<>(new ArrayList());
        assert listProvider.get() instanceof ArrayList;
        //
        listProvider.set(new LinkedList());
        assert listProvider.get() instanceof LinkedList;
    }

    @Test
    public void providerTest5() throws Throwable {
        //
        final ThreadSingleProvider<Object> listProvider = new ThreadSingleProvider<>(Object::new);
        final ArrayList<Object> result = new ArrayList<>();
        //
        //
        final AtomicInteger atomicInteger = new AtomicInteger();
        final Runnable runnable = () -> {
            result.add(listProvider.get());
            if (listProvider.get() == listProvider.get()) { // 线程内单例
                atomicInteger.incrementAndGet();
            }
        };
        //
        runnable.run();
        new Thread(runnable).start();
        Thread.sleep(500);
        //
        assert atomicInteger.get() == 2;
        assert result.get(0) != result.get(1); // 跨线程不相等
        //
        listProvider.toString();
    }
}
