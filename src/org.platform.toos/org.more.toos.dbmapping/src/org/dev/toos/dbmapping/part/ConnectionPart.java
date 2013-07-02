package org.dev.toos.dbmapping.part;
import org.dev.toos.dbmapping.policies.ConnectionEditPolicy;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
/**
 * 
 * @version : 2013-3-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConnectionPart extends AbstractDBMappingGraphicalEditPart {
    protected IFigure createFigure() {
        /*创建一条连接线*/
        PolylineConnection conn = new PolylineConnection();
        /*从原点开始画一个空心三角形。*/
        PolygonDecoration polygon = new PolygonDecoration();
        polygon.setFill(false);
        //polygon.setTemplate(PolygonDecoration.INVERTED_TRIANGLE_TIP);
        conn.setSourceDecoration(polygon);
        /*向目标点画一个实心三角形。*/
        conn.setTargetDecoration(new PolygonDecoration());
        /*BendpointConnectionRouter类用于确定画出的线是一个怎样的路径。*/
        conn.setConnectionRouter(new BendpointConnectionRouter());
        return conn;
    }
    protected void createEditPolicies() {
        //        /**允许选择改变连接起始端点策略。*/
        //        installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new ConnectionBendpointEditPolicy((Connection) this.getModel()));
        /**允许选择改变连接终止端点策略。*/
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        /**允许连接被删除策略*/
        installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy());
    }
    @Override
    public void setSelected(int value) {
        super.setSelected(value);
        if (value != EditPart.SELECTED_NONE)
            ((PolylineConnection) getFigure()).setLineWidth(2);
        else
            ((PolylineConnection) getFigure()).setLineWidth(1);
    }
}