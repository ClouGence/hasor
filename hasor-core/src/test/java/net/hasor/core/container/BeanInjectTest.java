package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.container.beans.TestBean;
import net.hasor.core.container.inject.*;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
public class BeanInjectTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment(null, null);
    }
    @Test
    public void builderTest1() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.addInject("uuid", InstanceProvider.of("paramUUID"));
        adapter.addInject("name", InstanceProvider.of("paramName"));
        //
        TestBean instance1 = (TestBean) container.getInstance(adapter, appContext);
        assert "paramUUID".equals(instance1.getUuid());
        assert "paramName".equals(instance1.getName());
        //
        TestBean instance2 = new TestBean();
        assert !"paramUUID".equals(instance2.getUuid());
        assert !"paramName".equals(instance2.getName());
        container.justInject(instance2, adapter, appContext);
        assert "paramUUID".equals(instance2.getUuid());
        assert "paramName".equals(instance2.getName());
    }
    //
    @Test
    public void builderTest2() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.getInstance(anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                BindInfo<Object> bindInfo = container.findBindInfo((String) invocationOnMock.getArguments()[0]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        PowerMockito.when(appContext.getInstance((Class<Object>) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                BindInfo<Object> bindInfo = container.findBindInfo(null, (Class<Object>) invocationOnMock.getArguments()[0]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        PowerMockito.when(appContext.findBindingBean(anyString(), (Class<Object>) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                BindInfo<Object> bindInfo = container.findBindInfo((String) arguments[0], (Class<Object>) arguments[1]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("12345");
        adapter.addInject("uuid", InstanceProvider.of("paramUUID"));
        adapter.addInject("name", InstanceProvider.of("paramName"));
        //
        TestBeanRef instance1 = container.getInstance(TestBeanRef.class, appContext);
        assert instance1.getTestBean() != null;
        assert "paramUUID".equals(instance1.getTestBean().getUuid());
        assert "paramName".equals(instance1.getTestBean().getName());
        //
        ConstructorTestBeanRef instance2 = container.getInstance(ConstructorTestBeanRef.class, appContext);
        assert instance2.getTestBean() != null;
        assert "paramUUID".equals(instance2.getTestBean().getUuid());
        assert "paramName".equals(instance2.getTestBean().getName());
    }
    //
    @Test
    public void builderTest3() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.findBindingBean(anyString(), (Class<Object>) anyObject())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                BindInfo<?> bindInfo = container.findBindInfo((String) arguments[0], (Class<?>) arguments[1]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("11111");
        adapter1.setBindName("myBean");
        adapter1.addInject("uuid", InstanceProvider.of("paramUUID_11"));
        adapter1.addInject("name", InstanceProvider.of("paramName_11"));
        //
        //
        ByNameTestBeanRef instance1 = container.getInstance(ByNameTestBeanRef.class, appContext);
        assert instance1.getTestBean() == null;
        ByNameConstructorTestBeanRef instance2 = container.getInstance(ByNameConstructorTestBeanRef.class, appContext);
        assert instance2.getTestBean() == null;
        //
        //
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        adapter2.setBindID("22222");
        adapter2.setBindName("testBean");
        adapter2.addInject("uuid", InstanceProvider.of("paramUUID_22"));
        adapter2.addInject("name", InstanceProvider.of("paramName_22"));
        //
        //
        ByNameTestBeanRef instance3 = container.getInstance(ByNameTestBeanRef.class, appContext);
        assert instance3.getTestBean() != null;
        assert "paramUUID_22".equals(instance3.getTestBean().getUuid());
        assert "paramName_22".equals(instance3.getTestBean().getName());
        ByNameConstructorTestBeanRef instance4 = container.getInstance(ByNameConstructorTestBeanRef.class, appContext);
        assert instance4.getTestBean() != null;
        assert "paramUUID_22".equals(instance4.getTestBean().getUuid());
        assert "paramName_22".equals(instance4.getTestBean().getName());
    }
    //
    @Test
    public void builderTest4() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.getInstance(anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                BindInfo<?> bindInfo = container.findBindInfo((String) arguments[0]);
                return container.getInstance(bindInfo, appContext);
            }
        });
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter1 = container.createInfoAdapter(TestBean.class);
        adapter1.setBindID("11111");
        adapter1.setBindName("11111");
        adapter1.addInject("uuid", InstanceProvider.of("paramUUID_11"));
        adapter1.addInject("name", InstanceProvider.of("paramName_11"));
        //
        //
        ByIDTestBeanRef instance1 = container.getInstance(ByIDTestBeanRef.class, appContext);
        assert instance1.getTestBean() == null;
        ByIDConstructorTestBeanRef instance2 = container.getInstance(ByIDConstructorTestBeanRef.class, appContext);
        assert instance2.getTestBean() == null;
        //
        //
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter2 = container.createInfoAdapter(TestBean.class);
        adapter2.setBindID("testBean");
        adapter2.setBindName("testBean");
        adapter2.addInject("uuid", InstanceProvider.of("paramUUID_22"));
        adapter2.addInject("name", InstanceProvider.of("paramName_22"));
        //
        //
        ByIDTestBeanRef instance3 = container.getInstance(ByIDTestBeanRef.class, appContext);
        assert instance3.getTestBean() != null;
        assert "paramUUID_22".equals(instance3.getTestBean().getUuid());
        assert "paramName_22".equals(instance3.getTestBean().getName());
        ByIDConstructorTestBeanRef instance4 = container.getInstance(ByIDConstructorTestBeanRef.class, appContext);
        assert instance4.getTestBean() != null;
        assert "paramUUID_22".equals(instance4.getTestBean().getUuid());
        assert "paramName_22".equals(instance4.getTestBean().getName());
    }
    //
    @Test
    public void builderTest5() {
        final BeanContainer container = new BeanContainer();
        final AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        InjectMembersBean.resetInit();
        container.getInstance(InjectMembersBean.class, appContext);
        assert InjectMembersBean.isStaticInit();
        //
        try {
            InjectMembersThrowBean.resetInit();
            container.getInstance(InjectMembersThrowBean.class, appContext);
            assert false;
        } catch (Exception e) {
            assert "testError".equals(e.getMessage());
        }
        //
        InjectAppContextAwareBean.resetInit();
        container.getInstance(InjectAppContextAwareBean.class, appContext);
        assert InjectAppContextAwareBean.isStaticInit();
    }
}
