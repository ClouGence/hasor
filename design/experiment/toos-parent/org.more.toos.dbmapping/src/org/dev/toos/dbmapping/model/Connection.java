package org.dev.toos.dbmapping.model;
import org.eclipse.draw2d.Graphics;
/**
 * 代表节和节点之间的连接
 * @version : 2013-3-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class Connection extends AbstractModel {
    private static final long   serialVersionUID  = 5517578085071368020L;
    public static final Integer SOLID_CONNECTION  = new Integer(Graphics.LINE_SOLID);
    public static final Integer DASHED_CONNECTION = new Integer(Graphics.LINE_DASH);
    public static String        Prop_reOutput     = "reOutput";
    public static String        Prop_reInput      = "reInput";
    //private int     lineStyle = Graphics.LINE_SOLID;
    private Element             source;
    private Element             target;
    //----------------------------------------------------------------------
    protected Connection(Element source, Element target) {
        this.source = source;
        this.target = target;
    }
    public Element getSource() {
        return source;
    }
    public Element getTarget() {
        return target;
    }
    //
    //
    /**重定向连接的输出端点。*/
    public void reConnectOutput(Element newSource) {
        this.firePropertyChange(Prop_reOutput, this.source, newSource);
        this.source = newSource;
        this.disconnect();
        this.connect();
        this.source.fireStructureChange(Element.Prop_OutputConnection, this);
    }
    /**重定向连接的输入端点。*/
    public void reConnectInput(Element newTarget) {
        this.firePropertyChange(Prop_reInput, this.target, newTarget);
        this.target = newTarget;
        this.disconnect();
        this.connect();
        this.target.fireStructureChange(Element.Prop_InputConnection, this);
    }
    /**执行连接，将连接线连接到两个Element上。*/
    public void connect() {
        this.source.addOutput(this);
        this.target.addInput(this);
        this.source.fireStructureChange(Element.Prop_OutputConnection, this);
        this.target.fireStructureChange(Element.Prop_InputConnection, this);
    }
    /**断开这个连接*/
    public void disconnect() {
        this.source.removeOutput(this);
        this.target.removeInput(this);
        this.source.fireStructureChange(Element.Prop_OutputConnection, this);
        this.target.fireStructureChange(Element.Prop_InputConnection, this);
    }
    //
    //
    //    public int getLineStyle() {
    //        return lineStyle;
    //    }
    //    public void setLineStyle(int lineStyle) {
    //        this.lineStyle = lineStyle;
    //    }
    //    public Element getSource() {
    //        return source;
    //    }
    //    public Element getTarget() {
    //        return target;
    //    }
}