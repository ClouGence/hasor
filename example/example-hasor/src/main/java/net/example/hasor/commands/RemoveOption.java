package net.example.hasor.commands;
import net.example.hasor.MyCmd;
import net.example.hasor.OptionDAO;
import net.example.hasor.OptionDO;
import net.hasor.boot.CommandLauncher;
import net.hasor.core.Inject;

import java.sql.SQLException;
//
@MyCmd("remove")
public class RemoveOption implements CommandLauncher {
    @Inject
    private OptionDAO optionDao;
    @Override
    public void run(String[] args) throws SQLException {
        OptionDO optionDO = optionDao.queryOption(args[1]);
        if (optionDO == null) {
            System.out.print("no exist.");
        } else {
            boolean ok = optionDao.deleteOption(args[1]);
            if (ok) {
                System.out.println("remove ok.");
            } else {
                System.out.println("remove failed.");
            }
        }
    }
}
