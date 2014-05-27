package org.dev.toos.dbmapping.commands;
import org.dev.toos.dbmapping.model.Connection;
import org.eclipse.gef.commands.Command;
/**
 * 创建操作
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class DeleteConnection extends Command {
    private Connection connection = null;
    //
    //
    public DeleteConnection(Connection connection) {
        if (connection == null)
            throw new NullPointerException();
        this.connection = connection;
    };
    //
    //
    public String getLabel() {
        return "Delete Connection";
    }
    public void execute() {
        this.connection.disconnect();
    }
}