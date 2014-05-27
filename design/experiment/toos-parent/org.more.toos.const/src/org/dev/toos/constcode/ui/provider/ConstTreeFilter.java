package org.dev.toos.constcode.ui.provider;
import org.dev.toos.constcode.model.bridge.ConstBeanBridge;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Combo;
import org.more.util.StringUtil;
/**
 * 用于view中表述ConstBean对象。
 * @version : 2013-2-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstTreeFilter extends ViewerFilter {
    private Combo constInput = null;
    public ConstTreeFilter(Combo constInput) {
        this.constInput = constInput;
    }
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        try {
            if (element instanceof ConstBeanBridge == true) {
                ConstBeanBridge elementBridge = (ConstBeanBridge) element;
                String constInputStr = "*" + constInput.getText().trim() + "*";
                if (constInputStr == null || constInputStr.equals(""))
                    return true;
                return StringUtil.matchWild(constInputStr.toLowerCase(), elementBridge.getCode().toLowerCase());
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}