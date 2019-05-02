package net.hasor.web.invoker;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.AsyncContext;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class InvokerTest {
    @Test
    public void asyncInvocationWorkerTest() throws Throwable {
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        final Method targetMethod = PowerMockito.mock(Method.class);
        //
        AsyncInvocationWorker worker = new AsyncInvocationWorker(asyncContext, targetMethod) {
            @Override
            public void doWork(Method method) throws Throwable {
                assert method == targetMethod;
            }
            @Override
            public void doWorkWhenError(Method targetMethod, Throwable e) {
                assert false;
            }
        };
        //
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        PowerMockito.doAnswer((Answer<Void>) invocationOnMock -> {
            atomicBoolean.set(true);
            return null;
        }).when(asyncContext).complete();
        //
        worker.run();
        assert atomicBoolean.get();
    }
    @Test
    public void errorAsyncInvocationWorkerTest() throws Throwable {
        AsyncContext asyncContext = PowerMockito.mock(AsyncContext.class);
        final Method targetMethod = PowerMockito.mock(Method.class);
        final Exception error = new Exception();
        //
        AsyncInvocationWorker worker = new AsyncInvocationWorker(asyncContext, targetMethod) {
            @Override
            public void doWork(Method method) throws Throwable {
                throw error;
            }
            @Override
            public void doWorkWhenError(Method targetMethod, Throwable e) {
                assert error == e;
            }
        };
        //
        worker.run();
    }
}