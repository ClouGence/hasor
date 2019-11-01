package net.hasor.test.tconsole;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelCommandOption;
import net.hasor.tconsole.spi.TelAfterExecutorListener;
import net.hasor.tconsole.spi.TelBeforeExecutorListener;

import java.util.ArrayList;
import java.util.List;

public class ExecutorListenerBean implements TelBeforeExecutorListener, TelAfterExecutorListener {
    private List<TelCommand> beforeExecCommand = new ArrayList<>();
    private List<TelCommand> afterExecCommand  = new ArrayList<>();

    public List<TelCommand> getBeforeExecCommand() {
        return beforeExecCommand;
    }

    public List<TelCommand> getAfterExecCommand() {
        return afterExecCommand;
    }

    @Override
    public void beforeExecCommand(TelCommandOption telCommand) {
        beforeExecCommand.add(telCommand);
    }

    @Override
    public void afterExecCommand(TelCommand telCommand) {
        afterExecCommand.add(telCommand);
    }
}