package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelContext;

@FunctionalInterface
public interface TelStopContextListener extends java.util.EventListener {
    /** Receives notification that a session is about to be invalidated. */
    public void onStop(TelContext telContext);
}