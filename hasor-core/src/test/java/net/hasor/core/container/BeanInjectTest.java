package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.container.beans.TestBean;
import net.hasor.core.container.beans.TestBeanRef;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
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
        //
        //
        final AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.addInject("uuid", InstanceProvider.of("paramUUID"));
        adapter.addInject("name", InstanceProvider.of("paramName"));
        //
        PowerMockito.when(appContext.getInstance(TestBean.class)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return container.getInstance(adapter, appContext);
            }
        });
        //
        TestBeanRef instance = container.getInstance(TestBeanRef.class, appContext);
        assert instance.getTestBean() != null;
        assert "paramUUID".equals(instance.getTestBean().getUuid());
        assert "paramName".equals(instance.getTestBean().getName());
    }
}
