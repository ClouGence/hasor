package net.hasor.tconsole.binder;
import net.hasor.core.AppContext;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.spi.TelStopContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TriggerOnStopToContext implements TelStopContextListener {
    private static Logger     logger = LoggerFactory.getLogger(TriggerOnStopToContext.class);
    private        AppContext appContext;
    private        boolean    enable = true;

    public TriggerOnStopToContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public void onStop(TelContext telContext) {
        if (this.enable && this.appContext.isStart()) {
            logger.info("tConsole -> answer quit command, shutdown Hasor.");
            this.appContext.shutdown();
        }
    }

    public void disable() {
        this.enable = false;
    }
}