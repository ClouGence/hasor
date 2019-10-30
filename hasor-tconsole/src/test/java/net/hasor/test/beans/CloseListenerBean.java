package net.hasor.test.beans;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.spi.TelCloseListener;

public class CloseListenerBean implements TelCloseListener {
    private TelCommand trigger;
    private int        afterSeconds;

    public TelCommand getTrigger() {
        return trigger;
    }

    public int getAfterSeconds() {
        return afterSeconds;
    }

    @Override
    public void onClose(TelCommand trigger, int afterSeconds) {
        this.trigger = trigger;
        this.afterSeconds = afterSeconds;
    }
}