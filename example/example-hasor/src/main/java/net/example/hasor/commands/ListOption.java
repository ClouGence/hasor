package net.example.hasor.commands;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;

@Tel("list")
public class ListOption extends AbstractTelExecutor {
    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        String[] commandArgs = telCommand.getCommandArgs();
        return doQuery(new ListOptionQuery(dataQL).execute(commandArgs));
    }
}