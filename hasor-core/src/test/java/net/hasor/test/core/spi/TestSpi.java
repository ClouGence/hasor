package net.hasor.test.core.spi;
import java.util.EventListener;

public interface TestSpi extends EventListener {
    public Object doSpi(Object param);
}
