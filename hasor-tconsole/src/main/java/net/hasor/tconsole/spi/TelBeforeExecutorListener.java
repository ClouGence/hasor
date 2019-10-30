package net.hasor.tconsole.spi;
import net.hasor.tconsole.TelCommandOption;

@FunctionalInterface
public interface TelBeforeExecutorListener extends java.util.EventListener {
    public void beforeExecCommand(TelCommandOption telCommand);
}