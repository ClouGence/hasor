package net.hasor.test.beans;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.spi.TelContextListener;

public class TelContextListenerBean implements TelContextListener {
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
