package net.example.hasor.commands;
import net.example.hasor.MyCmd;
import net.example.hasor.OptionDAO;
import net.example.hasor.OptionDO;
import net.hasor.boot.CommandLauncher;
import net.hasor.core.Inject;

import java.util.List;
//
@MyCmd("list")
public class ListOption implements CommandLauncher {
    @Inject
    private OptionDAO optionDao;
    @Override
    public void run(String[] args) throws Throwable {
        List<OptionDO> doList = optionDao.queryList();
        if (doList.isEmpty()) {
            System.out.println("empty.");
        }
        for (OptionDO optionDO : doList) {
            System.out.println(optionDO.getKey() + " = " + optionDO.getValue());
        }
    }
}
