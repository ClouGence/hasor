package net.example.hasor.execs;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;

@Tel("save")
public class SaveOption extends AbstractTelExecutor {
    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        String[] commandArgs = telCommand.getCommandArgs();
        return doQuery(new SaveOptionQuery(dataQL).execute(commandArgs));
    }
}
