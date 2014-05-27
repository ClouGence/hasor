package org.dev.toos.dbmapping.policies;
import org.dev.toos.dbmapping.commands.ConnectionReconnect;
import org.dev.toos.dbmapping.commands.CreateConnection;
import org.dev.toos.dbmapping.model.Connection;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
/**
 * 
 * @version : 2013-3-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class ElementGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {
    @Override
    protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
        /*当有连接从元素上创建时。*/
        Element source = (Element) getHost().getModel();
        int style = ((Integer) request.getNewObjectType()).intValue();
        CreateConnection cmd = new CreateConnection(source, null, style);
        request.setStartCommand(cmd);//保存命令在getConnectionCompleteCommand时候使用。
        return cmd;
    }
    @Override
    protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
        /*当连接创建结束时候（连接到目标对象上）。*/
        CreateConnection cmd = (CreateConnection) request.getStartCommand();
        cmd.setTarget((Element) getHost().getModel());
        return cmd;
    }
    @Override
    protected Command getReconnectTargetCommand(ReconnectRequest request) {
        /*当有连接连入元素时的处理。*/
        Connection conn = (Connection) request.getConnectionEditPart().getModel();
        Element newTarget = (Element) getHost().getModel();
        return new ConnectionReconnect(null, newTarget, conn);
    }
    @Override
    protected Command getReconnectSourceCommand(ReconnectRequest request) {
        /*当有连接连从该元素连出时的处理。*/
        Connection conn = (Connection) request.getConnectionEditPart().getModel();
        Element newSource = (Element) getHost().getModel();
        return new ConnectionReconnect(newSource, null, conn);
    }
}
