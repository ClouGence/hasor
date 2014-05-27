package org.dev.toos.dbmapping.commands;
import org.dev.toos.dbmapping.model.Connection;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
/**
 * 
 * @version : 2013-3-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConnectionReconnect extends Command {
    private Element    newSourceElement = null;
    private Element    newTargetElement = null;
    private Connection atConn           = null;
    //
    // 
    public ConnectionReconnect(Element newSourceElement, Element newTargetElement, Connection atConn) {
        this.newSourceElement = atConn.getSource();
        this.newTargetElement = atConn.getTarget();
        this.atConn = atConn;
        //
        if (newSourceElement != null)
            this.newSourceElement = newSourceElement;
        if (newTargetElement != null)
            this.newTargetElement = newTargetElement;
    }
    //
    //
    public String getLabel() {
        return "Connection Reconnect";
    }
    public void execute() {
        if (newSourceElement != null) {
            atConn.reConnectOutput(this.newSourceElement);
            return;
        }
        if (newSourceElement != null) {
            atConn.reConnectInput(this.newTargetElement);
            return;
        }
    }
}
