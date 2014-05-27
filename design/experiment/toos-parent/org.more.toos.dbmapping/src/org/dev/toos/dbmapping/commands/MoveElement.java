package org.dev.toos.dbmapping.commands;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
/**
 * 创建操作
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoveElement extends Command {
    private Element targetElement = null;
    private Point   newLocation   = null;
    //
    //
    public MoveElement(Element targetElement, Point newLocation) {
        this.targetElement = targetElement;
        this.newLocation = newLocation;
    }
    //
    //
    public String getLabel() {
        return "Move Element";
    }
    public void execute() {
        if (this.targetElement != null && this.newLocation != null) {
            //Point oldLocation = this.targetElement.getLocation();
            this.targetElement.setLocation(newLocation);
        }
    }
}
