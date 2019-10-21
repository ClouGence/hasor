package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelContext;

public interface TelContextListener extends java.util.EventListener {
    /** Receives notification that a session has been created. */
    public void onStart(TelContext telContext);

    /** Receives notification that a session is about to be invalidated. */
    public void onStop(TelContext telContext);
}