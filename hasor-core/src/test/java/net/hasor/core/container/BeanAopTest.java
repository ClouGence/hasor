package net.hasor.core.container;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.core.classcode.matcher.Matchers;
import net.hasor.core.container.aop.multilayer.l2.FooFunction;
import net.hasor.core.container.aop.TestInterceptor;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.info.AopBindInfoAdapter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
public class BeanAopTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment(null, null);
    }
    @Test
    public void builderTest1() {
        final BeanContainer container = new BeanContainer();
        ApiBinder apiBinder = new AbstractBinder(env) {
            @Override
            protected BeanBuilder getBeanBuilder() {
                return container;
            }
            @Override
            protected ScopManager getScopManager() {
                return container;
            }
        };
        //
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        PowerMockito.when(appContext.findBindingRegister(AopBindInfoAdapter.class)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return container.findBindInfoList(AopBindInfoAdapter.class);
            }
        });
        //
        //
        apiBinder.bindInterceptor(Matchers.anyClass(), Matchers.anyMethod(), new TestInterceptor());
        //
        TestInterceptor.resetInit();
        FooFunction instance = container.getInstance(FooFunction.class, appContext);
        assert !TestInterceptor.isCalled();
        assert !TestInterceptor.isThrowed();
        instance.fooCall("sss");
        assert TestInterceptor.isCalled();
        assert !TestInterceptor.isThrowed();
        //
        //
        TestInterceptor.resetInit();
        assert !TestInterceptor.isCalled();
        assert !TestInterceptor.isThrowed();
        try {
            instance.throwError("sss");
            assert false;
        } catch (Exception e) {
            assert true;
        }
        assert TestInterceptor.isCalled();
        assert TestInterceptor.isThrowed();
    }
}
