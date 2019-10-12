package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelSession;

import java.util.EventListener;

public interface TelSessionListener extends EventListener {
    /** Receives notification that a session has been created. */
    public void sessionCreated(TelSession telSession);

    /** Receives notification that a session is about to be invalidated. */
    public void sessionDestroyed(TelSession telSession);
}