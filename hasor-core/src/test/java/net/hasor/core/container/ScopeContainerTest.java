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
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.scope.SingletonScope;
import net.hasor.core.spi.ScopeProvisionListener;
import net.hasor.test.core.basic.destroy.PrototypePublicCallDestroyBean;
import net.hasor.test.core.basic.destroy.SingletonPublicCallDestroyBean;
import net.hasor.test.core.basic.pojo.SampleBean;
import net.hasor.test.core.basic.pojo.SampleFace;
import net.hasor.test.core.basic.pojo.SingletonSampleBean;
import net.hasor.test.core.scope.AnnoMyBean;
import net.hasor.test.core.scope.CustomHashBean;
import net.hasor.test.core.scope.HashRemainderScope;
import net.hasor.test.core.scope.My;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.HashMap;
import java.util.function.Supplier;

public class ScopeContainerTest {
    @Test
    public void scopTest1() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        spiCallerContainer.init();
        //
        try {
            spiCallerContainer.init();
            assert false;
        } catch (Exception e) {
            assert "the Container has been inited.".equals(e.getMessage());
        }
        //
        assert scopeContainer.findScope(javax.inject.Singleton.class.getName()) == null;
        assert scopeContainer.findScope(net.hasor.core.Singleton.class.getName()) == null;
        assert scopeContainer.findScope(net.hasor.core.Prototype.class.getName()) == null;
        scopeContainer.init();
        //
        assert scopeContainer.findScope(javax.inject.Singleton.class.getName()) != null;
        assert scopeContainer.findScope(net.hasor.core.Singleton.class.getName()) != null;
        assert scopeContainer.findScope(net.hasor.core.Prototype.class.getName()) != null;
        //
        scopeContainer.close();
    }

    @Test
    public void scopTest2() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        spiCallerContainer.init();
        scopeContainer.init();
        //
        try {
            scopeContainer.collectScope(AnnoMyBean.class);
            assert false;
        } catch (IllegalStateException e) {
            assert e.getMessage().equals("the scope " + My.class.getName() + " undefined.");
        }
    }

    @Test
    public void scopTest3() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        spiCallerContainer.init();
        scopeContainer.init();
        //
        Scope mockScope = PowerMockito.mock(Scope.class);
        scopeContainer.registerScope(My.class.getName(), mockScope);
        //
        try {
            Supplier<Scope>[] scope = scopeContainer.collectScope(AnnoMyBean.class);
            assert scope.length == 1;
            assert scope[0].get() == mockScope;
        } catch (IllegalStateException e) {
            assert false;
        }
        //
        try {
            scopeContainer.registerScope(My.class.getName(), mockScope);// 重复注册会报错
            assert true;
        } catch (IllegalStateException e) {
            assert e.getMessage().equals("the scope " + My.class.getName() + " already exists.");
        }
    }

    @Test
    public void scopTest4() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        spiCallerContainer.init();
        scopeContainer.init();
        //
        scopeContainer.registerAlias(javax.inject.Singleton.class.getName(), My.class.getName());
        //
        Supplier<Scope> scope1 = scopeContainer.findScope(javax.inject.Singleton.class.getName());
        Supplier<Scope> scope2 = scopeContainer.findScope(net.hasor.core.Singleton.class.getName());
        Supplier<Scope> scope3 = scopeContainer.findScope(My.class.getName());
        assert scope1.get() == scope2.get();
        assert scope2.get() == scope3.get();
    }

    @Test
    public void scopTest5() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        spiCallerContainer.init();
        scopeContainer.init();
        //
        scopeContainer.registerAlias(javax.inject.Singleton.class.getName(), My.class.getName());
        //
        try {
            scopeContainer.registerAlias(javax.inject.Singleton.class.getName(), My.class.getName());
            assert true;
        } catch (IllegalStateException e) {
            assert e.getMessage().equals("the scope " + My.class.getName() + " already exists.");
        }
    }

    @Test
    public void scopTest6() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        spiCallerContainer.init();
        //
        try {
            scopeContainer.registerAlias(My.class.getName(), javax.inject.Singleton.class.getName());
            assert true;
        } catch (IllegalStateException e) {
            assert e.getMessage().equals("reference Scope does not exist.");
        }
    }

    @Test
    public void scopTest7() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        //
        assert scopeContainer.collectScope((Class<?>) null) == null;
    }

    @Test
    public void scopTest8() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        scopeContainer.init();
        //
        DefaultBindInfoProviderAdapter<SampleFace> adapter1 = new DefaultBindInfoProviderAdapter<>();
        adapter1.setBindID("1234");
        adapter1.setBindType(SampleFace.class);
        adapter1.setSourceType(SingletonSampleBean.class);
        assert scopeContainer.isSingleton(adapter1);
        //
        DefaultBindInfoProviderAdapter<SampleBean> adapter2 = new DefaultBindInfoProviderAdapter<>();
        adapter2.setBindID("1234");
        adapter2.setBindType(SampleBean.class);
        assert !scopeContainer.isSingleton(adapter2);
    }

    @Test
    public void scopTest9() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        scopeContainer.init();
        //
        DefaultBindInfoProviderAdapter<SingletonSampleBean> adapter = new DefaultBindInfoProviderAdapter<>();
        adapter.setBindID("1234");
        adapter.setBindType(SingletonSampleBean.class);
        //
        assert scopeContainer.isSingleton(adapter);
        //
        assert !scopeContainer.isSingleton(PrototypePublicCallDestroyBean.class);
        assert scopeContainer.isSingleton(SingletonPublicCallDestroyBean.class);
    }

    @Test
    public void scopTest10_1() {
        // 构造三个对象的 Supplier，每个对象按照要求返回指定的 hashCode
        Supplier<CustomHashBean> supplier1 = () -> new CustomHashBean(0);
        Supplier<CustomHashBean> supplier2 = () -> new CustomHashBean(1);
        Supplier<CustomHashBean> supplier3 = () -> new CustomHashBean(2);
        //
        // 构造三个HashRemainderScope，模数为3，余数分别为0，1，2。  依次对应上面三个 Supplier 对象。
        //   - 保证 HashRemainderScope 只缓存对应的 CustomHashBean
        HashRemainderScope myScope1 = new HashRemainderScope(3, 0); // cache -> supplier1
        HashRemainderScope myScope2 = new HashRemainderScope(3, 1); // cache -> supplier2
        HashRemainderScope myScope3 = new HashRemainderScope(3, 2); // cache -> supplier3
        Scope[] scopes = new Scope[] { myScope1, myScope2, myScope3 };
        //
        // Test Case 的意图是测试 mergeScope 的 Scope链式能力（调用myScope1的顺序和创建CustomHashBean的顺序一致）
        SingletonScope singletonScope = new SingletonScope();
        //
        singletonScope.chainScope("1", scopes, supplier1).get();
        assert myScope1.getScopeMap().size() == 1;
        assert myScope2.getScopeMap().size() == 0;
        assert myScope3.getScopeMap().size() == 0;
        assert singletonScope.getSingletonData().size() == 1;
        //
        singletonScope.chainScope("2", scopes, supplier2).get();
        assert myScope1.getScopeMap().size() == 1;
        assert myScope2.getScopeMap().size() == 1;
        assert myScope3.getScopeMap().size() == 0;
        assert singletonScope.getSingletonData().size() == 2;
        //
        singletonScope.chainScope("3", scopes, supplier3).get();
        assert myScope1.getScopeMap().size() == 1;
        assert myScope2.getScopeMap().size() == 1;
        assert myScope3.getScopeMap().size() == 1;
        assert singletonScope.getSingletonData().size() == 3;
    }

    @Test
    public void scopTest10_2() {
        // 构造三个对象的 Supplier，每个对象按照要求返回指定的 hashCode
        Supplier<CustomHashBean> supplier1 = () -> new CustomHashBean(0);
        Supplier<CustomHashBean> supplier2 = () -> new CustomHashBean(1);
        Supplier<CustomHashBean> supplier3 = () -> new CustomHashBean(2);
        //
        // 构造三个HashRemainderScope，模数为3，余数分别为0，1，2。  依次对应上面三个 Supplier 对象。
        //   - 保证 HashRemainderScope 只缓存对应的 CustomHashBean
        HashRemainderScope myScope1 = new HashRemainderScope(3, 0); // cache -> supplier1
        HashRemainderScope myScope2 = new HashRemainderScope(3, 1); // cache -> supplier2
        HashRemainderScope myScope3 = new HashRemainderScope(3, 2); // cache -> supplier3
        Scope[] scopes = new Scope[] { myScope1, myScope2, myScope3 };
        //
        // Test Case 的意图是测试 chainScope 的 Scope链式能力（调用myScope1的顺序和创建CustomHashBean的顺序相反）
        SingletonScope singletonScope = new SingletonScope();
        //
        singletonScope.chainScope("3", scopes, supplier3).get();
        assert myScope1.getScopeMap().size() == 0;
        assert myScope2.getScopeMap().size() == 0;
        assert myScope3.getScopeMap().size() == 1;
        assert singletonScope.getSingletonData().size() == 1;
        //
        singletonScope.chainScope("2", scopes, supplier2).get();
        assert myScope1.getScopeMap().size() == 0;
        assert myScope2.getScopeMap().size() == 1;
        assert myScope3.getScopeMap().size() == 1;
        assert singletonScope.getSingletonData().size() == 2;
        //
        singletonScope.chainScope("1", scopes, supplier1).get();
        assert myScope1.getScopeMap().size() == 1;
        assert myScope2.getScopeMap().size() == 1;
        assert myScope3.getScopeMap().size() == 1;
        assert singletonScope.getSingletonData().size() == 3;
    }

    @Test
    public void scopTest10_3() {
        // 构造2个对象的 Supplier，每个对象按照要求返回指定的 hashCode
        Supplier<CustomHashBean> supplier1 = () -> new CustomHashBean(0);
        Supplier<CustomHashBean> supplier2 = () -> new CustomHashBean(1);
        //
        // 保证 HashRemainderScope 只缓存 hashCode 为 0 的那个 CustomHashBean
        HashRemainderScope myScope = new HashRemainderScope(2, 0); // cache -> supplier1
        //
        new SingletonScope().chainScope("1", myScope, supplier1).get();
        assert myScope.getScopeMap().size() == 1;
        //
        new SingletonScope().chainScope("2", myScope, supplier2).get();// 不会命中 HashRemainderScope
        assert myScope.getScopeMap().size() == 1;
    }

    @Test
    public void scopTest11() {
        SpiCallerContainer spiCallerContainer = new SpiCallerContainer();
        ScopeContainer scopeContainer = new ScopeContainer(spiCallerContainer);
        spiCallerContainer.init();
        scopeContainer.init();
        //
        //
        HashMap<String, Object> spiTest = new HashMap<>();
        spiCallerContainer.addListener(ScopeProvisionListener.class, InstanceProvider.of((scopeName, scopeSupplier) -> {
            spiTest.put(scopeName, scopeSupplier.get());
            //
        }));
        //
        Scope mockScope = PowerMockito.mock(Scope.class);
        scopeContainer.registerScope(My.class.getName(), mockScope);
        //
        assert spiTest.size() == 1;
        assert spiTest.containsKey(My.class.getName());
    }
}