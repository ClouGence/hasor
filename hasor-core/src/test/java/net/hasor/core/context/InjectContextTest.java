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
package net.hasor.core.context;
import net.hasor.core.*;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.test.beans.basic.inject.constructor.SingleConstructorPojoBeanRef;
import net.hasor.test.beans.basic.pojo.PojoBean;
import org.junit.Test;

import java.lang.reflect.Constructor;

public class InjectContextTest {
    @Test
    public void builderTest1() throws Throwable {
        AppContext appContext1 = new AppContextWarp(Hasor.create().asTiny().build());
        //
        String[] bindIDs = appContext1.getBindIDs();
        assert bindIDs.length == 4;
        assert bindIDs[0].equals("net.hasor.core.Environment");
        assert bindIDs[1].equals("net.hasor.core.AppContext");
        assert bindIDs[2].equals("net.hasor.core.EventContext");
        assert bindIDs[3].equals("net.hasor.core.Settings");
        assert appContext1.getProvider(PojoBean.class).get() instanceof PojoBean;
        //
        Environment env = new StandardEnvironment();
        AppContext appContext2 = new AppContextWarp(new StatusAppContext(env));
        appContext2.start((Module) apiBinder -> {
            apiBinder.bindType(PojoBean.class).idWith("pojobean");
            apiBinder.bindType(SingleConstructorPojoBeanRef.class)//
                    .toConstructor(SingleConstructorPojoBeanRef.class.getConstructor(PojoBean.class))//
                    .injectValue(0, new PojoBean());
        });
        //
        assert appContext2.getBindInfo("pojobean") != null;
        assert appContext2.getBindInfo("pojobean").getBindType().equals(PojoBean.class);
        assert appContext2.getBeanType("pojobean") == PojoBean.class;
        assert appContext2.getBeanType("abc") == null;
        //
        assert appContext2.containsBindID("pojobean");
        assert !appContext2.containsBindID("abc");
        //
        assert appContext2.getProvider("pojobean").get() instanceof PojoBean;
        assert appContext2.getProvider("abc") == null;
        //
        BindInfo<Object> bindInfo = appContext2.getBindInfo("pojobean");
        assert appContext2.getProvider(bindInfo).get() instanceof PojoBean;
        assert appContext2.getProvider((BindInfo) null) == null;
        assert appContext2.getProvider(PojoBean.class).get() instanceof PojoBean;
        //
        //
        Constructor<SingleConstructorPojoBeanRef> constructor = SingleConstructorPojoBeanRef.class.getConstructor(PojoBean.class);
        assert appContext1.getProvider(constructor).get() instanceof SingleConstructorPojoBeanRef;
        assert appContext2.getProvider(constructor).get() instanceof SingleConstructorPojoBeanRef;
        //
        //
        //
        //        ContextInjectBean injectBean = null;
        //        //
        //        injectBean = this.appContext.getInstance(ContextInjectBean.class);
        //        assert injectBean.getAppContext() == null;
        //        assert injectBean.getEnvironment() == null;
        //        assert injectBean.getEventContext() == null;
        //        assert injectBean.getSettings() == null;
        //        //
        //        this.appContext.start();
        //        injectBean = this.appContext.getInstance(ContextInjectBean.class);
        //        assert injectBean.getAppContext() == appContext;
        //        assert injectBean.getEnvironment() == appContext.getEnvironment();
        //        assert injectBean.getEventContext() == appContext.getEnvironment().getEventContext();
        //        assert injectBean.getSettings() == appContext.getEnvironment().getSettings();
        //    }
        //
        //    @Before
        //    public void testBefore() throws IOException {
        //        final StandardEnvironment env = new StandardEnvironment();
        //        final BeanContainer container = new BeanContainer();
        //        this.targetAppContext = new TemplateAppContext() {
        //            @Override
        //            protected BeanContainer getContainer() {
        //                return container;
        //            }
        //
        //            @Override
        //            public Environment getEnvironment() {
        //                return env;
        //            }
        //        };
        //        this.appContext = new AppContextWarp(targetAppContext);
    }
    //
    //    //
    //    @Test
    //    public void builderTest2() throws Throwable {
    //        appContext.shutdown();
    //        //
    //        assert appContext.getBindIDs().length == 0;
    //        //
    //        ApiBinder apiBinder = targetAppContext.newApiBinder();
    //        apiBinder.bindType(TestBean.class).idWith("abcdefg");
    //        apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq");
    //        //
    //        targetAppContext.doInitialize();
    //        targetAppContext.doInitializeCompleted();
    //        //
    //        assert appContext.getNames(TestBean.class).length == 0;
    //        assert appContext.getNames(SimpleInjectBean.class).length == 1;
    //        assert appContext.getNames(ArrayList.class).length == 0;
    //        //
    //        assert appContext.getBindIDs().length == 2;
    //        assert Arrays.asList(appContext.getBindIDs()).contains("abcdefg");
    //        assert Arrays.asList(appContext.getBindIDs()).contains("qqqq");
    //        //
    //        assert appContext.getBeanType("abcdefg") == TestBean.class;
    //        assert appContext.getBeanType("qqqq") == SimpleInjectBean.class;
    //        assert appContext.getBeanType("123456") == null;
    //        //
    //        assert appContext.containsBindID("abcdefg");
    //        assert !appContext.containsBindID("123456");
    //    }
    //
    //    //
    //    @Test
    //    public void builderTest3() throws Throwable {
    //        appContext.shutdown();
    //        assert appContext.getBindIDs().length == 0;
    //        ApiBinder apiBinder = targetAppContext.newApiBinder();
    //        apiBinder.bindType(TestBean.class).idWith("abcdefg").asEagerSingleton();
    //        apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq").asEagerSingleton();
    //        //
    //        targetAppContext.doInitialize();
    //        targetAppContext.doInitializeCompleted();
    //        //
    //        Object bean1 = appContext.getInstance("abcdefg");
    //        Object bean2 = appContext.getInstance("qqqq");
    //        assert bean1 instanceof TestBean;
    //        assert bean2 instanceof SimpleInjectBean;
    //        assert appContext.getInstance("123456") == null;
    //        assert appContext.getInstance(TestBean.class) == bean1;
    //        assert appContext.getInstance(SimpleInjectBean.class) != bean2;
    //        assert appContext.findBindingBean("qqqq", SimpleInjectBean.class) == bean2;
    //        //
    //        assert appContext.getInstance(TestBean.class.getConstructor()) == bean1;
    //        assert appContext.getInstance(SimpleInjectBean.class.getConstructor()) != bean2;
    //        //
    //        assert appContext.getInstance((BindInfo<?>) null) == null;
    //    }
    //
    //    //
    //    @Test
    //    public void builderTest4() throws Throwable {
    //        appContext.shutdown();
    //        assert appContext.getBindIDs().length == 0;
    //        ApiBinder apiBinder = targetAppContext.newApiBinder();
    //        apiBinder.bindType(TestBean.class).idWith("abcdefg").asEagerSingleton();
    //        apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq").asEagerSingleton();
    //        //
    //        targetAppContext.doInitialize();
    //        targetAppContext.doInitializeCompleted();
    //        //
    //        Object bean1 = appContext.getInstance("abcdefg");
    //        Object bean2 = appContext.getInstance("qqqq");
    //        assert bean1 instanceof TestBean;
    //        assert bean2 instanceof SimpleInjectBean;
    //        //
    //        assert appContext.getProvider("abcdefg").get() == bean1;
    //        assert appContext.getProvider("qqqq").get() == bean2;
    //        //
    //        assert appContext.getProvider(TestBean.class).get() == bean1;
    //        assert appContext.getProvider(SimpleInjectBean.class).get() != bean2;
    //        //
    //        assert appContext.getProvider(TestBean.class.getConstructor()).get() == bean1;
    //        assert appContext.getProvider(SimpleInjectBean.class.getConstructor()).get() != bean2;
    //        //
    //        BindInfo<?> info = appContext.getBindInfo("abcdefg");
    //        assert appContext.getProvider(info).get() == bean1;
    //        assert appContext.getProvider((BindInfo<?>) null) == null;
    //        //
    //        assert appContext.getProvider("abcdefg").get() instanceof TestBean;
    //        assert appContext.getProvider("123456") == null;
    //        assert appContext.getProvider((BindInfo<?>) null) == null;
    //        //
    //    }
    //
    //    @Test
    //    public void builderTest5() throws Throwable {
    //        //
    //        assert appContext.justInject(null) == null;
    //        assert appContext.justInject(new TestBeanRef(), (Class<?>) null).getTestBean() == null;
    //        assert appContext.justInject(new TestBeanRef(), (BindInfo<?>) null).getTestBean() == null;
    //        assert appContext.justInject(null, TestBeanRef.class) == null;
    //        //
    //        TestBeanRef ref1 = new TestBeanRef();
    //        //
    //        assert ref1.getTestBean() == null;
    //        appContext.justInject(ref1);
    //        assert ref1.getTestBean() != null;
    //        //
    //        ApiBinder apiBinder = targetAppContext.newApiBinder();
    //        TestBean testBean = new TestBean();
    //        BindInfo<TestBean> info = apiBinder.bindType(TestBean.class)//
    //                .idWith("abcdefg").toInstance(testBean).toInfo();
    //        //
    //        TestBeanRef ref2 = new TestBeanRef();
    //        assert ref2.getTestBean() == null;
    //        appContext.justInject(ref2);
    //        assert ref2.getTestBean() == testBean;
    //        //
    //        TestBeanRef ref3 = new TestBeanRef();
    //        appContext.justInject(ref3, info);
    //        assert ref3.getTestBean() == testBean;
    //        //
    //        apiBinder.bindType(TestBeanRef.class).injectValue("paramName", "value");
    //        TestBeanRef ref4 = new TestBeanRef();
    //        appContext.justInject(ref4);
    //        assert ref4.getTestBean() == testBean && "value".equals(ref4.getParamName());
    //    }
    //
    //    @Test
    //    public void builderTest6() throws Throwable {
    //        //
    //        ApiBinder apiBinder = targetAppContext.newApiBinder();
    //        TestBean testBean = new TestBean();
    //        BindInfo<?> info1 = apiBinder.bindType(TestBean.class).idWith("abcdefg").toInstance(testBean).toInfo();
    //        BindInfo<?> info2 = apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq").asEagerSingleton().toInfo();
    //        //
    //        List<Supplier<? extends TestBean>> bindingProviderList1 = appContext.findBindingProvider(TestBean.class);
    //        assert bindingProviderList1.size() == 1;
    //        assert bindingProviderList1.get(0).get() == testBean;
    //        //
    //        assert appContext.findBindingBean(TestBean.class).get(0) == testBean;
    //        assert appContext.findBindingBean(null, TestBean.class) == testBean;
    //        assert appContext.findBindingBean("", TestBean.class) == null;
    //        assert appContext.findBindingProvider(null, TestBean.class).get() == testBean;
    //        //
    //        List<Supplier<? extends List>> bindingProviderList2 = appContext.findBindingProvider(List.class);
    //        assert bindingProviderList2.size() == 0;
    //        assert appContext.findBindingBean(null, List.class) == null;
    //        assert appContext.findBindingProvider(null, List.class) == null;
    //        //
    //        BindInfo<?> info3 = apiBinder.bindType(TestBean.class) //
    //                .bothWith("123").toConstructor(CallInitBean.class.getConstructor()).toInfo();
    //        assert appContext.findBindingRegister("123", TestBean.class) == info3;
    //    }
    //
    //
    //    @Test
    //    public void builderTest9() throws Throwable {
    //        //
    //        AppContext appContext = Hasor.create().asTiny().build();
    //        ConstructorBean constructorBean = appContext.getInstance(ConstructorBean.class, "abcdefg");
    //        assert constructorBean.getName().equals("abcdefg");
    //        assert constructorBean.getUuid().equals("aaa");
    //    }
}