package org.dev.toos.dbmapping.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
/**
 * 代表节点元素
 * @version : 2013-3-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class Element extends AbstractModel {
    private static final long serialVersionUID      = -6193319926276663305L;
    public static String      Prop_OutputConnection = "OutputConnection";
    public static String      Prop_InputConnection  = "InputConnection";
    public static String      Prop_Name             = "Name";
    public static String      Prop_Location         = "Location";
    private Diagram           diagram               = null;
    private Point             location              = new Point(0, 0);            //模型位置
    private Dimension         size                  = new Dimension(50, 50);      //模型大小
    private String            name                  = "New Element";
    private List<Connection>  outConnection         = new ArrayList<Connection>(); //连出节点
    private List<Connection>  inConnection          = new ArrayList<Connection>(); //连入节点
    //----------------------------------------------------------------------
    protected Element(Diagram diagram) {
        this.diagram = diagram;
    }
    public Diagram getDiagram() {
        return diagram;
    }
    /**在模型中创建该元素。*/
    public void create() {
        this.diagram.addEmenemt(this);
        for (Connection out : this.outConnection)
            out.connect();
        for (Connection in : this.inConnection)
            in.connect();
        this.diagram.fireStructureChange(Diagram.Prop_Element, this);
    }
    /**从模型中删除该元素。*/
    public void delete() {
        for (Connection out : this.outConnection)
            out.disconnect();
        for (Connection in : this.inConnection)
            in.disconnect();
        this.diagram.removeElement(this);
        this.diagram.fireStructureChange(Diagram.Prop_Element, this);
    }
    /**从当前元素创建一个到目标元素的连接。*/
    public Connection createConnection(Element targetElement) {
        return new Connection(this, targetElement);
    }
    /**获取连入的连接*/
    public List<Connection> getOutputList() {
        return Collections.unmodifiableList(outConnection);
    }
    /**获取连出的连接。*/
    public List<Connection> getInputList() {
        return Collections.unmodifiableList(inConnection);
    }
    /*当“连接”对象调用connect方法时会使用该方法。*/
    void addOutput(Connection connection) {
        this.outConnection.add(connection);
    }
    /*当“连接”对象调用disconnect方法时会使用该方法。*/
    void removeOutput(Connection connection) {
        this.outConnection.remove(connection);
    }
    /*当“连接”对象调用connect方法时会使用该方法。*/
    void addInput(Connection connection) {
        this.inConnection.add(connection);
    }
    /*当“连接”对象调用disconnect方法时会使用该方法。*/
    void removeInput(Connection connection) {
        this.inConnection.remove(connection);
    }
    //
    //
    //
    //
    public Point getLocation() {
        return location;
    }
    public void setLocation(Point location) {
        this.firePropertyChange(Prop_Location, this.location, location);
        this.location = location;
    }
    public Dimension getSize() {
        return size;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.firePropertyChange(Prop_Name, this.name, name);
        this.name = name;
    }
    //    /** 
    //     * Return a pictogram (small icon) describing this model element.
    //     * Children should override this method and return an appropriate Image.
    //     * @return a 16x16 Image or null
    //     */
    //    public abstract Image getIcon();
}