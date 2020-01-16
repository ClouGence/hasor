package net.example.hasor.commands;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;
import net.hasor.utils.ResourcesUtils;

@Tel("read")
public class ReadOption extends AbstractTelExecutor {
    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        return doQuery(telCommand, dataQL.createQuery(ResourcesUtils.getResourceAsStream("ReadOption.ql")));
    }
}