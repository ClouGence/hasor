package net.example.cli.tels;
import net.example.cli.service.MyService;
import net.hasor.tconsole.Tel;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Tel("hello")
@Component
public class HelloExecutor implements TelExecutor {
    @Resource
    private MyService myService;

    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        return "call service : " + myService.myName();
    }
}