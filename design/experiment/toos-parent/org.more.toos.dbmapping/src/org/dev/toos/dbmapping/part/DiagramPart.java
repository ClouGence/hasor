package org.dev.toos.dbmapping.part;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.dev.toos.dbmapping.model.Diagram;
import org.dev.toos.dbmapping.policies.DiagramXYLayoutEditPolicy;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
/**
 * 
 * @version : 2013-3-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class DiagramPart extends AbstractDBMappingGraphicalEditPart {
    @Override
    protected IFigure createFigure() {
        Figure f = new FreeformLayer();
        f.setBorder(new MarginBorder(3));
        f.setLayoutManager(new FreeformLayout());
        // Create the static router for the connection layer
        ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
        connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));
        return f;
    }
    @SuppressWarnings("rawtypes")
    protected List getModelChildren() {
        return ((Diagram) this.getModel()).getElements();
    }
    /**属性变更更新模型*/
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (Diagram.Prop_Element.equals(prop))
            refreshChildren();
    }
    @Override
    protected void createEditPolicies() {
        /*上面选用的是XY自由布局管理器，那么这里在设置布局事件接收对象时就要使用XYLayoutEditPolicy*/
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
    }
}