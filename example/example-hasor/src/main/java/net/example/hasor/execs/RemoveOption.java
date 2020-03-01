package net.example.hasor.execs;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;

@Tel("remove")
public class RemoveOption extends AbstractTelExecutor {
    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        String[] commandArgs = telCommand.getCommandArgs();
        return doQuery(new RemoveOptionQuery(dataQL).execute(commandArgs));
    }
}
