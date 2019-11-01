package net.hasor.test.tconsole;
import net.hasor.tconsole.TelSession;
import net.hasor.tconsole.spi.TelSessionCreateListener;
import net.hasor.tconsole.spi.TelSessionDestroyListener;

import java.util.HashMap;

public class TelSessionListenerBean extends HashMap<String, TelSession> implements TelSessionCreateListener, TelSessionDestroyListener {
    @Override
    public void sessionCreated(TelSession telSession) {
        this.put(telSession.getSessionID(), telSession);
    }

    @Override
    public void sessionDestroyed(TelSession telSession) {
        this.remove(telSession.getSessionID());
    }
}