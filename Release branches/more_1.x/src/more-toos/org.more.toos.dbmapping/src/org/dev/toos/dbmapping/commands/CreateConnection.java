package org.dev.toos.dbmapping.commands;
import org.dev.toos.dbmapping.model.Connection;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.commands.Command;
/**
 * ´´½¨²Ù×÷
 * @version : 2013-3-12
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class CreateConnection extends Command {
    private int     lineStyle = 0;
    private Element source    = null;
    private Element target    = null;
    //
    //
    public CreateConnection(Element sourceElement, Element targetElement, int lineStyle) {
        this.source = sourceElement;
        this.target = targetElement;
        this.lineStyle = lineStyle;
    }
    public void setSource(Element source) {
        this.source = source;
    }
    public void setTarget(Element target) {
        this.target = target;
    }
    //
    //
    public String getLabel() {
        return "Create Connection";
    }
    public void execute() {
        if (this.source != null && this.target != null) {
            Connection connection = this.source.createConnection(this.target);
            connection.connect();
        }
    }
}
