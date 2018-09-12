package test.net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.lang.reflect.Constructor;
public class BeanBuilderTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment(null, null);
    }
    @Test
    public void builderTest1() throws NoSuchMethodException {
        MyBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        //
        MyBean instance = container.getInstance(MyBean.class, appContext);
        assert instance != null;
        //
        assert container.getInstance((Class<Object>) null, appContext) == null;
        //
        Constructor<ConstructorBean> constructor = ConstructorBean.class.getConstructor(String.class);
        ConstructorBean constructorBean = container.getInstance(constructor, appContext);
        assert constructorBean != null;
        //
        assert container.getInstance((Constructor<Object>) null, appContext) == null;
    }
    //
    @Test
    public void builderTest2() {
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        //
        AbstractBindInfoProviderAdapter<TestBean> adapter = container.createInfoAdapter(TestBean.class);
        adapter.setBindID("ID:123456");
        adapter.setBindName("Name:dddd");
        adapter.setSourceType(MyBean.class);
        //
        TestBean instance = container.getInstance(adapter, appContext);
        //
        assert instance instanceof MyBean;
    }
}
