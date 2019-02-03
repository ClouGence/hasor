package net.hasor.core.container;
import net.hasor.core.*;
import net.hasor.core.container.anno.AnnoCallInitBean;
import net.hasor.core.container.anno.AnnoConstructorMultiBean;
import net.hasor.core.container.aware.AppContextAwareBean;
import net.hasor.core.container.aware.BindInfoAwareBean;
import net.hasor.core.container.beans.*;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.hasor.core.AppContext.ContextEvent_Started;
import static org.mockito.Matchers.anyObject;
public class BeanContainerTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment();
    }
    @Test
    public void containerTest1() throws Throwable {
        CallInitBean.resetInit();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.getInstance((BindInfo) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return container.getProvider((BindInfo) invocationOnMock.getArguments()[0], appContext).get();
            }
        });
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(CallInitBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.initMethod("init");
        adapter.setSingletonMode(SingletonMode.Singleton);
        //
        container.doInitializeCompleted(env);  // 初始化 BeanContainer，不会构造bean
        assert !CallInitBean.isStaticInit();
        //
        env.getEventContext().fireSyncEvent(ContextEvent_Started, appContext); // 引发Started事件，带有 initMethod 的单列bean 会被初始化
        assert CallInitBean.isStaticInit();
    }
    @Test
    public void containerTest2() {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(CallInitBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.initMethod("init");
        //
        container.doInitializeCompleted(env);
        //
        BindInfo<?> info = container.findBindInfo("12345");
        Object instance1 = container.getProvider(info, appContext).get();
        Object instance2 = container.getProvider(info, appContext).get();
        //
        assert instance1 instanceof CallInitBean;
        assert ((CallInitBean) instance1).isInit();
        assert instance1 != instance2; // 非单列Bean
    }
    @Test
    public void containerTest3() throws Throwable {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(ConstructorBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.initMethod("init");
        adapter.setConstructor(0, String.class, InstanceProvider.of("testValue"));
        //
        container.doInitializeCompleted(env);
        //
        ConstructorBean instance = (ConstructorBean) container.getProvider(adapter, appContext).get();
        //
        assert instance.isInit();
        assert "testValue".equals(instance.getName());
    }
    @Test
    public void containerTest4() throws Throwable {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(ConstructorMultiBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.initMethod("init");
        adapter.setConstructor(0, String.class, InstanceProvider.of("paramUUID"));
        adapter.setConstructor(1, String.class, InstanceProvider.of("paramName"));
        //
        container.doInitializeCompleted(env);
        //
        ConstructorMultiBean instance = (ConstructorMultiBean) container.getProvider(adapter, appContext).get();
        //
        assert instance.isInit();
        assert "paramUUID".equals(instance.getUuid());
        assert "paramName".equals(instance.getName());
    }
    @Test
    public void containerTest5() throws Throwable {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AnnoConstructorMultiBean instance = container.getProvider(AnnoConstructorMultiBean.class, appContext).get();
        //
        assert instance.getUuid() == null;
        assert instance.getName() == null;
    }
    //
    //
    @Test
    public void containerTest6() {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(AnnoCallInitBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        //
        container.doInitializeCompleted(env);
        //
        BindInfo<?> info = container.findBindInfo("12345");
        Object instance1 = container.getProvider(info, appContext).get();
        Object instance2 = container.getProvider(info, appContext).get();
        //
        assert instance1 instanceof CallInitBean;
        assert ((CallInitBean) instance1).isInit();
        assert instance1 != instance2; // 非单列Bean
    }
    @Test
    public void containerTest7() {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        //
        try {
            container.getProvider(ConstructorMultiBean.class, appContext).get();
            assert false;
        } catch (Exception e) {
            assert "No default constructor found.".equals(e.getMessage());
        }
    }
    @Test
    public void containerTest8() {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        assert container.getProvider((Class<Object>) null, appContext) == null;
        assert container.getProvider((Constructor<Object>) null, appContext) == null;
        assert container.getProvider((BindInfo<Object>) null, appContext) == null;
    }
    //
    @Test
    public void containerTest9() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(BindInfoAwareBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        //
        container.doInitializeCompleted(env);
        //
        BindInfo<?> info = container.findBindInfo("12345");
        BindInfoAwareBean instance = (BindInfoAwareBean) container.getProvider(info, appContext).get();
        assert instance != null;
        assert instance.getBindInfo() == adapter;
    }
    //
    @Test
    public void containerTest10() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(AppContextAwareBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        //
        container.doInitializeCompleted(env);
        //
        BindInfo<?> info = container.findBindInfo("12345");
        AppContextAwareBean instance = (AppContextAwareBean) container.getProvider(info, appContext).get();
        assert instance != null;
        assert instance.getAppContext() == appContext;
    }
    @Test
    public void containerTest11() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        container.doInitializeCompleted(env);
        //
        assert container.getProvider(CallInitBean2.class, appContext).get().isInit();
        assert !container.getProvider(CallInitBean3.class, appContext).get().isInit();
    }
    @Test
    public void containerTest12() {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        BeanCreaterListener<Object> listener = new BeanCreaterListener<Object>() {
            @Override
            public void beanCreated(Object newObject, BindInfo bindInfo) throws Throwable {
                atomicBoolean.set(true);
            }
        };
        Provider<? extends BeanCreaterListener<?>> createrProvider = InstanceProvider.of(listener);
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(AnnoCallInitBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.setCreaterListener(createrProvider);
        //
        container.doInitializeCompleted(env);
        //
        BindInfo<?> info = container.findBindInfo("12345");
        container.getProvider(info, appContext).get();
        //
        assert atomicBoolean.get();
    }
}