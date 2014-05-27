package org.dev.toos.constcode.ui.editor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;
/**
 * 
 * @version : 2013-2-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class CC_ConstTypeCellEditor extends ComboBoxCellEditor {
    public CC_ConstTypeCellEditor(Composite parent) {
        super(parent, new String[] { "No", "Group", "School" });
    }
    @Override
    protected Object doGetValue() {
        Object obj = super.doGetValue();
        if ((Integer) obj == 0)
            return "No";
        else if ((Integer) obj == 1)
            return "Group";
        else if ((Integer) obj == 2)
            return "School";
        else
            return null;
    }
    @Override
    protected void doSetValue(Object value) {
        if ("No".equals(value) == true)
            super.doSetValue(0);
        else if ("Group".equals(value) == true)
            super.doSetValue(1);
        else if ("School".equals(value) == true)
            super.doSetValue(2);
    }
}
