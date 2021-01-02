package net.example.hasor.execs;
import net.example.hasor.daos.ListOptionQuery;
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