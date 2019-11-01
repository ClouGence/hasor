package net.hasor.test.tconsole;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.spi.TelStartContextListener;
import net.hasor.tconsole.spi.TelStopContextListener;

public class TelContextListenerBean implements TelStartContextListener, TelStopContextListener {
    private Boolean contextListener;

    public Boolean getContextListener() {
        return contextListener;
    }

    @Override
    public void onStart(TelContext telContext) {
        this.contextListener = true;
    }

    @Override
    public void onStop(TelContext telContext) {
        this.contextListener = false;
    }
}
