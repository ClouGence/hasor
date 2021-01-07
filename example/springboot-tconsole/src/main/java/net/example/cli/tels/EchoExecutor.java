package net.example.cli.tels;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;
import net.hasor.utils.StringUtils;
import org.springframework.stereotype.Component;

@Tel("echo")
@Component
public class EchoExecutor implements TelExecutor {
    @Override
    public String helpInfo() {
        return "when run 'help' command, this message will be show.";
    }

    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        return StringUtils.join(telCommand.getCommandArgs(), " ");
    }
}