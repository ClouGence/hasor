package net.hasor.core.context;
import net.hasor.core.*;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.container.beans.CallInitBean;
import net.hasor.core.container.beans.TestBean;
import net.hasor.core.container.inject.SimpleInjectBean;
import net.hasor.core.container.inject.TestBeanRef;
import net.hasor.core.environment.StandardEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
public class ContextTest {
    private AppContext         appContext;
    private TemplateAppContext targetAppContext;
    @Before
    public void testBefore() throws IOException {
        final StandardEnvironment env = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        this.targetAppContext = new TemplateAppContext() {
            @Override
            protected BeanContainer getContainer() {
                return container;
            }
            @Override
            public Environment getEnvironment() {
                return env;
            }
        };
        this.appContext = new AppContextWarp(targetAppContext);
    }
    //
    @Test
    public void builderTest1() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.doAnswer(invocationOnMock -> {
            if (atomicInteger.get() == 0) {
                atomicInteger.set(1);
            } else {
                atomicInteger.set(2);
                throw new Exception();
            }
            return null;
        }).when(appContext).shutdown();
        //
        ShutdownHook hook = new ShutdownHook(appContext);
        //
        hook.run();
        assert atomicInteger.get() == 1;
        //
        hook.run();
        assert atomicInteger.get() == 2;
    }
    //
    @Test
    public void builderTest2() throws Throwable {
        appContext.shutdown();
        //
        assert appContext.getBindIDs().length == 0;
        //
        ApiBinder apiBinder = targetAppContext.newApiBinder();
        apiBinder.bindType(TestBean.class).idWith("abcdefg");
        apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq");
        //
        targetAppContext.doInitialize();
        targetAppContext.doInitializeCompleted();
        //
        assert appContext.getNames(TestBean.class).length == 0;
        assert appContext.getNames(SimpleInjectBean.class).length == 1;
        assert appContext.getNames(ArrayList.class).length == 0;
        //
        assert appContext.getBindIDs().length == 2;
        assert Arrays.asList(appContext.getBindIDs()).contains("abcdefg");
        assert Arrays.asList(appContext.getBindIDs()).contains("qqqq");
        //
        assert appContext.getBeanType("abcdefg") == TestBean.class;
        assert appContext.getBeanType("qqqq") == SimpleInjectBean.class;
        assert appContext.getBeanType("123456") == null;
        //
        assert appContext.containsBindID("abcdefg");
        assert !appContext.containsBindID("123456");
    }
    //
    @Test
    public void builderTest3() throws Throwable {
        appContext.shutdown();
        assert appContext.getBindIDs().length == 0;
        ApiBinder apiBinder = targetAppContext.newApiBinder();
        apiBinder.bindType(TestBean.class).idWith("abcdefg").asEagerSingleton();
        apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq").asEagerSingleton();
        //
        targetAppContext.doInitialize();
        targetAppContext.doInitializeCompleted();
        //
        Object bean1 = appContext.getInstance("abcdefg");
        Object bean2 = appContext.getInstance("qqqq");
        assert bean1 instanceof TestBean;
        assert bean2 instanceof SimpleInjectBean;
        assert appContext.getInstance("123456") == null;
        assert appContext.getInstance(TestBean.class) == bean1;
        assert appContext.getInstance(SimpleInjectBean.class) != bean2;
        assert appContext.findBindingBean("qqqq", SimpleInjectBean.class) == bean2;
        //
        assert appContext.getInstance(TestBean.class.getConstructor()) == bean1;
        assert appContext.getInstance(SimpleInjectBean.class.getConstructor()) != bean2;
        //
        assert appContext.getInstance((BindInfo<?>) null) == null;
    }
    //
    @Test
    public void builderTest4() throws Throwable {
        appContext.shutdown();
        assert appContext.getBindIDs().length == 0;
        ApiBinder apiBinder = targetAppContext.newApiBinder();
        apiBinder.bindType(TestBean.class).idWith("abcdefg").asEagerSingleton();
        apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq").asEagerSingleton();
        //
        targetAppContext.doInitialize();
        targetAppContext.doInitializeCompleted();
        //
        Object bean1 = appContext.getInstance("abcdefg");
        Object bean2 = appContext.getInstance("qqqq");
        assert bean1 instanceof TestBean;
        assert bean2 instanceof SimpleInjectBean;
        //
        assert appContext.getProvider("abcdefg").get() == bean1;
        assert appContext.getProvider("qqqq").get() == bean2;
        //
        assert appContext.getProvider(TestBean.class).get() == bean1;
        assert appContext.getProvider(SimpleInjectBean.class).get() != bean2;
        //
        assert appContext.getProvider(TestBean.class.getConstructor()).get() == bean1;
        assert appContext.getProvider(SimpleInjectBean.class.getConstructor()).get() != bean2;
        //
        BindInfo<?> info = appContext.getBindInfo("abcdefg");
        assert appContext.getProvider(info).get() == bean1;
        assert appContext.getProvider((BindInfo<?>) null) == null;
        //
        assert appContext.getProvider("abcdefg").get() instanceof TestBean;
        assert appContext.getProvider("123456") == null;
        assert appContext.getProvider((BindInfo<?>) null) == null;
        //
    }
    @Test
    public void builderTest5() throws Throwable {
        //
        assert appContext.justInject(null) == null;
        assert appContext.justInject(new TestBeanRef(), (Class<?>) null).getTestBean() == null;
        assert appContext.justInject(new TestBeanRef(), (BindInfo<?>) null).getTestBean() == null;
        assert appContext.justInject(null, TestBeanRef.class) == null;
        //
        TestBeanRef ref1 = new TestBeanRef();
        //
        assert ref1.getTestBean() == null;
        appContext.justInject(ref1);
        assert ref1.getTestBean() != null;
        //
        ApiBinder apiBinder = targetAppContext.newApiBinder();
        TestBean testBean = new TestBean();
        BindInfo<TestBean> info = apiBinder.bindType(TestBean.class)//
                .idWith("abcdefg").toInstance(testBean).toInfo();
        //
        TestBeanRef ref2 = new TestBeanRef();
        assert ref2.getTestBean() == null;
        appContext.justInject(ref2);
        assert ref2.getTestBean() == testBean;
        //
        TestBeanRef ref3 = new TestBeanRef();
        appContext.justInject(ref3, info);
        assert ref3.getTestBean() == testBean;
        //
        apiBinder.bindType(TestBeanRef.class).injectValue("paramName", "value");
        TestBeanRef ref4 = new TestBeanRef();
        appContext.justInject(ref4);
        assert ref4.getTestBean() == testBean && "value".equals(ref4.getParamName());
    }
    @Test
    public void builderTest6() throws Throwable {
        //
        ApiBinder apiBinder = targetAppContext.newApiBinder();
        TestBean testBean = new TestBean();
        BindInfo<?> info1 = apiBinder.bindType(TestBean.class).idWith("abcdefg").toInstance(testBean).toInfo();
        BindInfo<?> info2 = apiBinder.bindType(SimpleInjectBean.class).bothWith("qqqq").asEagerSingleton().toInfo();
        //
        List<Provider<? extends TestBean>> bindingProviderList1 = appContext.findBindingProvider(TestBean.class);
        assert bindingProviderList1.size() == 1;
        assert bindingProviderList1.get(0).get() == testBean;
        //
        List<Provider<? extends List>> bindingProviderList2 = appContext.findBindingProvider(List.class);
        assert bindingProviderList2.size() == 0;
        //
        assert appContext.findBindingBean(TestBean.class).get(0) == testBean;
        assert appContext.findBindingBean("", TestBean.class) == testBean;
        assert appContext.findBindingBean("", List.class) == null;
        try {
            appContext.findBindingBean(null, TestBean.class);
            assert false;
        } catch (Exception e) {
            assert true;
        }
        //
        assert appContext.findBindingProvider("", TestBean.class).get() == testBean;
        assert appContext.findBindingProvider("", List.class) == null;
        //
        BindInfo<?> info3 = apiBinder.bindType(TestBean.class) //
                .bothWith("123").toConstructor(CallInitBean.class.getConstructor()).toInfo();
        assert appContext.findBindingRegister("", TestBean.class.getConstructor()) == info1;
        assert appContext.findBindingRegister("123", TestBean.class.getConstructor()) == info3;
    }
}