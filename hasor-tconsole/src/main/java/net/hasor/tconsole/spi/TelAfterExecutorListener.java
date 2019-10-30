package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelCommand;

@FunctionalInterface
public interface TelAfterExecutorListener extends java.util.EventListener {
    public void afterExecCommand(TelCommand telCommand);
}
