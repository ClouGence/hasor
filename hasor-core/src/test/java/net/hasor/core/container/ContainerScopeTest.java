package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.Scope;
import net.hasor.core.container.beans.CallInitBean;
import net.hasor.core.container.beans.MyScope;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.util.function.Supplier;
public class ContainerScopeTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment();
    }
    @Test
    public void containerTest1() {
        CallInitBean.resetInit();
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        Supplier<MyScope> myScope = InstanceProvider.of(new MyScope());
        container.registerScope("myScope", myScope);
        //
        AbstractBindInfoProviderAdapter<?> adapter = container.createInfoAdapter(CallInitBean.class);
        adapter.setBindID("12345");
        adapter.setBindName("myBean");
        adapter.initMethod("init");
        adapter.setScopeProvider(myScope);
        //
        Object instance1 = container.getProvider(adapter, appContext, null).get();
        Object instance2 = container.getProvider(adapter, appContext, null).get();
        //
        assert instance1 != null;
        assert instance1 == instance2;
    }
    @Test
    public void containerTest2() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        Supplier<MyScope> myScope = InstanceProvider.of(new MyScope());
        container.registerScope("myScope", myScope);
        //
        //
        Supplier<Scope> scope = container.findScope("myScope");
        assert scope.equals(myScope);
    }
}