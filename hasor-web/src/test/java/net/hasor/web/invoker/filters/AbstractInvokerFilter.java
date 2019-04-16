package net.hasor.web.invoker.filters;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;
//
public abstract class AbstractInvokerFilter implements InvokerFilter {
    @Override
    public void init(InvokerConfig config) throws Throwable {
    }
    @Override
    public void destroy() {
    }
}
