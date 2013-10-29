package org.dev.toos.dbmapping.policies;
import org.dev.toos.dbmapping.commands.DeleteElement;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
/**
 * 
 * @version : 2013-3-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ElementEditPolicy extends ComponentEditPolicy {
    @Override
    protected Command createDeleteCommand(GroupRequest request) {
        return new DeleteElement((Element) this.getHost().getModel());
    }
}