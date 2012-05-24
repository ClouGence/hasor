package org.more.webui.components;
import org.more.webui.context.ViewContext;
/**
 * æ≤Ã¨÷µ
 * @version : 2012-5-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class StaticValueHolder extends AbstractValueHolder {
    //
    public StaticValueHolder() {}
    public StaticValueHolder(Object staticValue) {
        this.setMetaValue(staticValue);
    }
    @Override
    public boolean isUpdate() {
        return false;
    }
    @Override
    public boolean isReadOnly() {
        return false;
    }
    @Override
    public void updateModule(UIComponent component, ViewContext viewContext) {}
}