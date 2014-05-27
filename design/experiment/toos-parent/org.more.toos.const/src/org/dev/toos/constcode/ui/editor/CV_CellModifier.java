package org.dev.toos.constcode.ui.editor;
import org.dev.toos.constcode.model.bridge.VarBeanBridge;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
/**
 * 
 * @version : 2013-2-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class CV_CellModifier implements ICellModifier {
    private TreeViewer treeViewer = null;
    public CV_CellModifier(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }
    @Override
    public boolean canModify(Object element, String property) {
        VarBeanBridge varBridge = (VarBeanBridge) element;
        return varBridge.isActivateModify();
    }
    @Override
    public Object getValue(Object element, String property) {
        VarBeanBridge varBridge = (VarBeanBridge) element;
        Object returnData = null;
        if (property.equals("Key") == true)
            returnData = varBridge.getKey();
        else if (property.equals("Var") == true)
            returnData = varBridge.getVar();
        else if (property.equals("Lat") == true)
            returnData = varBridge.getLat();
        return (returnData == null) ? "" : returnData;
    }
    @Override
    public void modify(Object element, String property, Object value) {
        TreeItem treeItem = (TreeItem) element;
        VarBeanBridge varBridge = (VarBeanBridge) treeItem.getData();
        boolean results = false;
        if (property.equals("Key") == true)
            results = varBridge.setKey((String) value);
        else if (property.equals("Var") == true)
            results = varBridge.setVar((String) value);
        else if (property.equals("Lat") == true)
            results = varBridge.setLat((String) value);
        //
        if (results == true)
            treeViewer.refresh();
    }
}