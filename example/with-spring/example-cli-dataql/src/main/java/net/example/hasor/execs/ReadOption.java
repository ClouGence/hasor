package net.example.hasor.execs;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;

@Tel("read")
public class ReadOption extends AbstractTelExecutor {
    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        String[] commandArgs = telCommand.getCommandArgs();
        return doQuery(new ReadOptionQuery(dataQL).execute(commandArgs));
    }
}
