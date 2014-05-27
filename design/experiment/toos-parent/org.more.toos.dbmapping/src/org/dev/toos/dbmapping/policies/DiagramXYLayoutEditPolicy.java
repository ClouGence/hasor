package org.dev.toos.dbmapping.policies;
import org.dev.toos.dbmapping.commands.CreateElement;
import org.dev.toos.dbmapping.model.Diagram;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
/**
 * 
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
public class DiagramXYLayoutEditPolicy extends XYLayoutEditPolicy {
    protected Command getCreateCommand(CreateRequest request) {
        Object childClass = request.getNewObjectType();
        if (childClass == Element.class) {
            // return a command that can add a Shape to a ShapesDiagram 
            return new CreateElement((Diagram) this.getHost().getModel(), request.getLocation());
        }
        return null;
    }
}