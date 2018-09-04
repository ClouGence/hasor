package net.example.hasor.commands;
import net.example.hasor.MyCmd;
import net.example.hasor.OptionDAO;
import net.example.hasor.OptionDO;
import net.hasor.boot.CommandLauncher;
import net.hasor.core.Inject;
//
@MyCmd("get")
public class GetOption implements CommandLauncher {
    @Inject
    private OptionDAO optionDao;
    @Override
    public void run(String[] args) throws Throwable {
        OptionDO optionDO = optionDao.queryOption(args[1]);
        if (optionDO == null) {
            System.out.println("not found key '" + args[1] + "'");
        } else {
            System.out.println(optionDO.getKey() + " = " + optionDO.getValue());
        }
    }
}