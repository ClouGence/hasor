package test.net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;

import static net.hasor.core.AppContext.ContextEvent_Started;
import static org.mockito.Matchers.anyObject;
public class BeanContainerTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment(null, null);
    }
    @Test
    public void containerTest1() {
        MyBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(MyBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.initMethod("init");
        adapter.setSingleton(true);
        //
        container.doInitializeCompleted(env);
        //
        BindInfo<?> info = container.findBindInfo("12345");
        Object instance1 = container.getInstance(info, appContext);
        Object instance2 = container.getInstance(info, appContext);
        //
        assert instance1 instanceof MyBean;
        assert ((MyBean) instance1).isInit();
        assert instance1 == instance2; // 非单列Bean

    }
    @Test
    public void containerTest2() throws Throwable {
        MyBean.resetInit();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getInstance((BindInfo) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return container.getInstance((BindInfo) invocationOnMock.getArguments()[0], appContext);
            }
        });
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(MyBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.initMethod("init");
        adapter.setSingleton(true);
        //
        container.doInitializeCompleted(env);  // 初始化 BeanContainer，不会构造bean
        assert !MyBean.isStaticInit();
        //
        env.getEventContext().fireSyncEvent(ContextEvent_Started, appContext); // 引发Started事件，带有 initMethod 的单列bean 会被初始化
        assert MyBean.isStaticInit();
    }
    @Test
    public void containerTest3() {
        MyBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(MyBean.class);
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
        assert instance1 instanceof MyBean;
        assert ((MyBean) instance1).isInit();
        assert instance1 != instance2; // 非单列Bean
    }
    @Test
    public void containerTest4() throws Throwable {
        MyBean.resetInit();
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getInstance((BindInfo) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return container.getInstance((BindInfo) invocationOnMock.getArguments()[0], appContext);
            }
        });
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.addInject("name", InstanceProvider.of("testValue"));
        //
        TestBean testBean = new TestBean();
        container.justInject(testBean, adapter, appContext);
        //
        assert "testValue".equals(testBean.getName());
    }
}
