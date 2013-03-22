package org.dev.toos.dbmapping.commands;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
/**
 * ¸ÄÃû²Ù×÷
 * @version : 2013-3-12
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class RenameElement extends Command {
    private Element targetElement = null;
    private String  newName       = null;
    //
    //
    public RenameElement(Element targetElement, String newName) {
        this.targetElement = targetElement;
        this.newName = newName;
    }
    //
    //
    public String getLabel() {
        return "Rename Element";
    }
    public void execute() {
        if (this.newName != null)
            this.targetElement.setName(this.newName);
    }
}