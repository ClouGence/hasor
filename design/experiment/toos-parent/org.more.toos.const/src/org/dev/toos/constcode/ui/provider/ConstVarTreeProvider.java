package org.dev.toos.constcode.ui.provider;
import java.util.List;
import org.dev.toos.constcode.model.ConstGroup.FromType;
import org.dev.toos.constcode.model.bridge.ConstBeanBridge;
import org.dev.toos.constcode.model.bridge.VarBeanBridge;
import org.dev.toos.internal.util.ColorUtils;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
/**
 * 
 * @version : 2013-2-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstVarTreeProvider extends CellLabelProvider implements ITreeContentProvider {
    /**实现CellLabelProvider类型方法，用于向单元格中设置显示的数据*/
    @Override
    public void update(ViewerCell cell) {
        VarBeanBridge element = (VarBeanBridge) cell.getElement();
        ViewerRow row = cell.getViewerRow();
        row.setText(0, element.getKey());//key
        row.setText(1, element.getVar());//var
        row.setText(2, element.getLat());//lat
        //-------------------------------------------------------------------
        //1.父亲被删除
        if (element.getConst().isDelete() == true) {
            cell.setBackground(ColorUtils.getColor4Delete());
            return;
        }
        //2.新建
        if (element.isNew() == true) {
            cell.setBackground(ColorUtils.getColor4New());
            return;
        }
        //3.来源颜色
        FromType formType = element.getConst().getSource().getType();
        if (formType == FromType.DB)
            cell.setBackground(ColorUtils.getColor4DB());
        else if (formType == FromType.JAR)
            cell.setBackground(ColorUtils.getColor4Jar());
        else if (formType == FromType.Source)
            cell.setBackground(ColorUtils.getColor4Source());
        //4.修改过的
        if (element.isPropertyChanged() == true)
            cell.setBackground(ColorUtils.getColor4Changed());
        //5.具体的属性修改
        if (element.isKeyChanged() == true)
            row.setBackground(0, ColorUtils.getColor4Changed2());//key
        if (element.isVarChanged() == true)
            row.setBackground(1, ColorUtils.getColor4Changed2());//var
        if (element.isLatChanged() == true)
            row.setBackground(2, ColorUtils.getColor4Changed2());//lat
        //6.删除的条目
        FontData fontData = cell.getFont().getFontData()[0];
        if (element.isDelete() == true) {
            cell.setFont(SWTResourceManager.getFont(fontData.getName(), fontData.getHeight(), fontData.getStyle() | SWT.BOLD | SWT.ITALIC));
            cell.setBackground(ColorUtils.getColor4Delete());
        } else
            cell.setFont(SWTResourceManager.getFont(fontData.getName(), fontData.getHeight(), 0));
    }
    //
    //
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        //TODO Root Data Changed
    }
    /**根据类型获取根节点*/
    @Override
    public VarBeanBridge[] getElements(Object inputElement) {
        ConstBeanBridge inputData = (ConstBeanBridge) inputElement;
        List<VarBeanBridge> varList = inputData.getVarRoots();
        return varList.toArray(new VarBeanBridge[varList.size()]);
    }
    /**获取子节点数据*/
    @Override
    public VarBeanBridge[] getChildren(Object parentElement) {
        VarBeanBridge varElementBridge = (VarBeanBridge) parentElement;
        List<VarBeanBridge> varList = varElementBridge.getChildren();
        return varList.toArray(new VarBeanBridge[varList.size()]);
    }
    @Override
    public boolean hasChildren(Object element) {
        return true;
    }
    @Override
    public VarBeanBridge getParent(Object element) {
        VarBeanBridge varElementBridge = (VarBeanBridge) element;
        return varElementBridge.getParent();
    }
}