package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelContext;

@FunctionalInterface
public interface TelStartContextListener extends java.util.EventListener {
    /** Receives notification that a session has been created. */
    public void onStart(TelContext telContext);
}