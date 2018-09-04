package net.example.hasor.commands;
import net.example.hasor.MyCmd;
import net.example.hasor.OptionDAO;
import net.example.hasor.OptionDO;
import net.hasor.boot.CommandLauncher;
import net.hasor.core.Inject;

import java.sql.SQLException;
//
@MyCmd("set")
public class SetOption implements CommandLauncher {
    @Inject
    private OptionDAO optionDao;
    @Override
    public void run(String[] args) throws SQLException {
        OptionDO optionDO = optionDao.queryOption(args[1]);
        boolean ok = false;
        if (optionDO == null) {
            ok = optionDao.insertOption(args[1], args[2]);
            System.out.print("inserted ");
        } else {
            ok = optionDao.updateOption(args[1], args[2]);
            System.out.print("updated ");
        }
        if (ok) {
            System.out.println("ok.");
        } else {
            System.out.println("failed.");
        }
    }
}
