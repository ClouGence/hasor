package net.hasor.test.beans;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.spi.TelExecutorListener;

import java.util.ArrayList;
import java.util.List;

public class ExecutorListenerBean implements TelExecutorListener {
    private List<TelCommand> beforeExecCommand = new ArrayList<>();
    private List<TelCommand> afterExecCommand  = new ArrayList<>();

    public List<TelCommand> getBeforeExecCommand() {
        return beforeExecCommand;
    }

    public List<TelCommand> getAfterExecCommand() {
        return afterExecCommand;
    }

    @Override
    public void beforeExecCommand(TelCommand telCommand) {
        beforeExecCommand.add(telCommand);
    }

    @Override
    public void afterExecCommand(TelCommand telCommand) {
        afterExecCommand.add(telCommand);
    }
}