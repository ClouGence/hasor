package net.example.hasor.commands;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;
import net.hasor.utils.ResourcesUtils;

@Tel("save")
public class SaveOption extends AbstractTelExecutor {
    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        String[] commandArgs = telCommand.getCommandArgs();
        return doQuery(new SaveOptionQuery(dataQL).execute(commandArgs));
    }
}