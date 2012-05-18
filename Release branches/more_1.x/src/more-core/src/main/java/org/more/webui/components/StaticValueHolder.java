package org.more.webui.components;
import org.more.webui.context.ViewContext;
/**
 * æ≤Ã¨÷µ
 * @version : 2012-5-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class StaticValueHolder extends ValueHolder {
    public StaticValueHolder() {
        this.value(null);
    }
    public StaticValueHolder(Object staticValue) {
        this.value(staticValue);
    }
    @Override
    public void updateModule(ViewContext viewContext) {}
}