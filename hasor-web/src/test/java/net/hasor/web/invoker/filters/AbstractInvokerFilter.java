package net.hasor.web.invoker.filters;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;
//
@FunctionalInterface
public interface AbstractInvokerFilter extends InvokerFilter {
    public default void init(InvokerConfig config) throws Throwable {
    }

    public default void destroy() {
    }
}
