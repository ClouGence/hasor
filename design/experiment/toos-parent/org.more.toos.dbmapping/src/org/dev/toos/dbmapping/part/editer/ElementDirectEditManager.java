package org.dev.toos.dbmapping.part.editer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
/**
 * 
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
public class ElementDirectEditManager extends DirectEditManager {
    public ElementDirectEditManager(GraphicalEditPart source, Class editorType, CellEditorLocator locator) {
        super(source, editorType, locator);
    }
    @Override
    protected void initCellEditor() {
        // TODO Auto-generated method stub
    }
}
