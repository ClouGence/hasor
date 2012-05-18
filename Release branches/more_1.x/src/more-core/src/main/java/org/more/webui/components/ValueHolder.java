package org.more.webui.components;
import org.more.util.StringConvertUtil;
import org.more.webui.context.ViewContext;
/**
 * 
 * @version : 2012-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ValueHolder {
    private Object newValue = null;
    @Override
    public String toString() {
        Object var = this.value();
        return (var == null) ? "null" : var.toString();
    }
    public <T> T valueTo(Class<T> toType) {
        return StringConvertUtil.changeType(this.value(), toType);
    }
    /**返回模型上的属性值。*/
    public Object value() {
        return this.newValue;
    }
    /**写入属性值，被写入的属性值会在调用{@link #updateModule(ViewContext)}被*/
    public void value(Object newValue) {
        this.newValue = newValue;
    }
    /**将写入{@link ValueHolder}的属性的值更新到模型中。*/
    public abstract void updateModule(ViewContext viewContext);
}