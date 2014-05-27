package org.dev.toos.dbmapping.commands;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
/**
 * 改名操作
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
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