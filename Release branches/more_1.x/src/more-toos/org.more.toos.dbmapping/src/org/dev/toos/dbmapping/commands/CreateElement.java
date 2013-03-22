package org.dev.toos.dbmapping.commands;
import org.dev.toos.dbmapping.model.Diagram;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
/**
 * ´´½¨²Ù×÷
 * @version : 2013-3-12
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class CreateElement extends Command {
    private Diagram diagram  = null;
    private Point   location = null;
    //
    // 
    public CreateElement(Diagram diagram, Point location) {
        this.diagram = diagram;
        this.location = location;
    }
    //
    //
    public String getLabel() {
        return "Create Element";
    }
    public void execute() {
        Element newElement = this.diagram.createElement();
        if (this.location != null) {
            newElement.setLocation(this.location);
            newElement.create();
        }
    }
}
