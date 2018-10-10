package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.SingletonMode;
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

import static net.hasor.core.AppContext.ContextEvent_Started;
import static org.mockito.Matchers.anyObject;
public class BeanContainerTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment(null, null);
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
                return container.getInstance((BindInfo) invocationOnMock.getArguments()[0], appContext);
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
        Object instance1 = container.getInstance(info, appContext);
        Object instance2 = container.getInstance(info, appContext);
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
        ConstructorBean instance = (ConstructorBean) container.getInstance(adapter, appContext);
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
        ConstructorMultiBean instance = (ConstructorMultiBean) container.getInstance(adapter, appContext);
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
        AnnoConstructorMultiBean instance = container.getInstance(AnnoConstructorMultiBean.class, appContext);
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
        Object instance1 = container.getInstance(info, appContext);
        Object instance2 = container.getInstance(info, appContext);
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
            container.getInstance(ConstructorMultiBean.class, appContext);
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
        assert container.getInstance((Class<Object>) null, appContext) == null;
        assert container.getInstance((Constructor<Object>) null, appContext) == null;
        assert container.getInstance((BindInfo<Object>) null, appContext) == null;
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
        BindInfoAwareBean instance = (BindInfoAwareBean) container.getInstance(info, appContext);
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
        AppContextAwareBean instance = (AppContextAwareBean) container.getInstance(info, appContext);
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
        assert container.getInstance(CallInitBean2.class, appContext).isInit();
        assert !container.getInstance(CallInitBean3.class, appContext).isInit();
    }
}