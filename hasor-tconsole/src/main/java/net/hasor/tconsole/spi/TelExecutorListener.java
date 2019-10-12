package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelCommand;

public interface TelExecutorListener extends java.util.EventListener {
    public void beforeExecCommand(TelCommand telCommand);

    public void afterExecCommand(TelCommand telCommand);
}
