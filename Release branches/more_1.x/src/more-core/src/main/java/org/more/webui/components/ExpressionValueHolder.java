package org.more.webui.components;
import org.more.webui.context.ViewContext;
/**
 * 静态值
 * @version : 2012-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class ExpressionValueHolder extends ValueHolder {
    private String expressionString = null;
    public ExpressionValueHolder(String expressionString) {
        this.expressionString = expressionString;
    }
    @Override
    public void updateModule(ViewContext viewContext) {
        //TODO 将newValue的值更新到expressionString表示的模型上
    }
}