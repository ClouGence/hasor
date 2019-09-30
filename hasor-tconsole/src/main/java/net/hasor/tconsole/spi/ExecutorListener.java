package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelCommand;

public interface ExecutorListener extends java.util.EventListener {
    public void beforeExecCommand(TelCommand telCommand);

    public void afterExecCommand(TelCommand telCommand);
}
