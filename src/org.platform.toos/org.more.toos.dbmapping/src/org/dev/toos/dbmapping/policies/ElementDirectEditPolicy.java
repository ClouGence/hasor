package org.dev.toos.dbmapping.policies;
import org.dev.toos.dbmapping.commands.RenameElement;
import org.dev.toos.dbmapping.figures.ElementFigure;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
/**
 * 
 * @version : 2013-3-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class ElementDirectEditPolicy extends DirectEditPolicy {
    @Override
    protected Command getDirectEditCommand(DirectEditRequest request) {
        /**更名*/
        Element element = (Element) getHost().getModel();
        String newName = (String) request.getCellEditor().getValue();
        return new RenameElement(element, newName);
    }
    @Override
    protected void showCurrentEditValue(DirectEditRequest request) {
        //向Figure设置显示值(其实可以不用，原因是Figure创建时候已经设置了显示值)
        String value = (String) request.getCellEditor().getValue();
        ((ElementFigure) getHostFigure()).setName(value);
    }
}