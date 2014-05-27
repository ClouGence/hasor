package org.dev.toos.constcode.ui.editor;
import org.dev.toos.constcode.metadata.LatType;
import org.dev.toos.constcode.model.bridge.ConstBeanBridge;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.more.util.StringConvertUtil;
/**
 * 
 * @version : 2013-2-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class CC_CellModifier implements ICellModifier {
    private TreeViewer treeViewer = null;
    public CC_CellModifier(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }
    @Override
    public boolean canModify(Object element, String property) {
        ConstBeanBridge constBridge = (ConstBeanBridge) element;
        return constBridge.isActivateModify();
    }
    @Override
    public Object getValue(Object element, String property) {
        ConstBeanBridge constBridge = (ConstBeanBridge) element;
        Object returnData = null;
        if (property.equals("Code") == true)
            returnData = constBridge.getCode();
        else if (property.equals("LatType") == true)
            returnData = constBridge.getLatType().name();
        return (returnData == null) ? "" : returnData;
    }
    @Override
    public void modify(Object element, String property, Object value) {
        TreeItem treeItem = (TreeItem) element;
        ConstBeanBridge constBridge = (ConstBeanBridge) treeItem.getData();
        boolean results = false;
        if (property.equals("Code") == true)
            results = constBridge.setCode((String) value);
        else if (property.equals("LatType") == true) {
            LatType latType = StringConvertUtil.changeType(value, LatType.class, LatType.No);
            results = constBridge.setLatType(latType);
        }
        if (results == true)
            treeViewer.refresh();
    }
}