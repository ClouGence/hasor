package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.SingletonMode;
import net.hasor.core.container.anno.*;
import net.hasor.core.container.beans.CallDestroyBean;
import net.hasor.core.container.beans.CallDestroyBean2;
import net.hasor.core.container.beans.CallDestroyBean3;
import net.hasor.core.container.beans.SimpleBean;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.core.provider.SingleProvider;
import net.hasor.core.scope.SingletonScope;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.util.function.Supplier;
public class BeanDestroyTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment();
    }
    //
    //
    @Test
    public void builderTest1() throws Throwable {
        StandardEnvironment environment = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(environment.getClassLoader());
        container.registerScope(ScopManager.SINGLETON_SCOPE, InstanceProvider.of(new SingletonScope()));
        //
        AbstractBindInfoProviderAdapter<CallDestroyBean> infoAdapter = container.createInfoAdapter(CallDestroyBean.class);
        infoAdapter.destroyMethod("destroy");
        infoAdapter.setSingletonMode(SingletonMode.Singleton);
        //
        CallDestroyBean destroy1 = container.getProvider(infoAdapter, appContext, null).get();
        CallDestroyBean2 destroy2 = container.getProvider(CallDestroyBean2.class, appContext, null).get();
        CallDestroyBean3 destroy3 = container.getProvider(CallDestroyBean3.class, appContext, null).get();
        //
        assert !destroy1.isDestroy();
        assert !destroy2.isDestroy();
        assert !destroy3.isDestroy();
        //
        container.doShutdownCompleted();
        environment.getEventContext().fireSyncEvent(AppContext.ContextEvent_Shutdown, appContext);
        //
        //
        assert destroy1.isDestroy();
        assert destroy2.isDestroy();
        assert !destroy3.isDestroy();
    }
}