package net.hasor.web.invoker.beans;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerCreater;
import org.powermock.api.mockito.PowerMockito;
public class TestInvokerCreater implements InvokerCreater {
    @Override
    public Invoker createExt(Invoker invoker) {
        TestInvoker2 inv = PowerMockito.mock(TestInvoker2.class);
        PowerMockito.when(inv.hello()).thenReturn("hello");
        PowerMockito.when(inv.word()).thenReturn("word");
        return inv;
    }
}
