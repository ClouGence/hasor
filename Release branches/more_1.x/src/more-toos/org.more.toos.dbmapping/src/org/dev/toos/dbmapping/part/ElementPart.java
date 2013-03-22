package org.dev.toos.dbmapping.part;
import java.beans.PropertyChangeEvent;
import org.dev.toos.dbmapping.figures.ElementFigure;
import org.dev.toos.dbmapping.model.Element;
import org.dev.toos.dbmapping.policies.ElementDirectEditPolicy;
import org.dev.toos.dbmapping.policies.ElementEditPolicy;
import org.dev.toos.dbmapping.policies.ElementGraphicalNodeEditPolicy;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
/**
 * 
 * @version : 2013-3-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class ElementPart extends AbstractDBMappingGraphicalEditPart {
    @Override
    protected IFigure createFigure() {
        IFigure f = new ElementFigure((Element) this.getModel());
        //  f.setOpaque(true); // non-transparent figure
        f.setBackgroundColor(ColorConstants.green);
        return f;
    }
    //    /**在执行更名的时候使用DirectEditManager*/
    //    protected DirectEditManager editManager;
    //    public void performRequest(Request req) {
    //        if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)) {
    //            if (this.editManager == null) {
    //                ElementFigure figure = (ElementFigure) getFigure();
    //                this.editManager = new ElementDirectEditManager(this, ComboBoxCellEditor.class, new ElementComboBoxCellEditorLocator(figure));
    //            }
    //            this.editManager.show();
    //        }
    //    }
    /**属性变更更新模型*/
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        /*刷新自身*/
        if (Element.Prop_Location.equals(prop))
            refreshVisuals();
        /*刷新自身*/
        else if (Element.Prop_Name.equals(prop))
            refreshVisuals();
        /*刷新链出的连接*/
        else if (Element.Prop_OutputConnection.equals(prop))
            refreshSourceConnections();
        /*刷新链入的连接*/
        else if (Element.Prop_InputConnection.equals(prop))
            refreshTargetConnections();
    }
    @Override
    protected void createEditPolicies() {
        /*点击进入编辑模式策略*/
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ElementDirectEditPolicy());
        /*模型的删除策略*/
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new ElementEditPolicy());
        /*图形化节点策略*/
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ElementGraphicalNodeEditPolicy());
    }
}