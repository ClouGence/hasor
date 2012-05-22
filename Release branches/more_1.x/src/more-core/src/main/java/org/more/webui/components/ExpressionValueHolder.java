package org.more.webui.components;
import org.more.core.iatt.Attribute;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlException;
import org.more.webui.context.ViewContext;
/**
 * æ≤Ã¨÷µ
 * @version : 2012-5-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ExpressionValueHolder extends ValueHolder {
    private String expressionString = null;
    private Node   elNodeTree       = null;
    public ExpressionValueHolder(String expressionString) {
        this.expressionString = expressionString;
    }
    protected Node getNodeTree() throws OgnlException {
        if (this.elNodeTree == null)
            this.elNodeTree = (Node) Ognl.parseExpression(expressionString);
        return this.elNodeTree;
    }
    @Override
    public void updateModule(UIComponent component, ViewContext viewContext) throws OgnlException {
        Attribute<Object> elContext = viewContext.getUIContext().getAttribute();
        Ognl.setValue(this.getNodeTree(), elContext, this.value());
        this.value(null);
    }
    @Override
    public Object value() {
        Object var = super.value();
        if (var != null)
            return var;
        //
        try {
            ViewContext viewContext = ViewContext.getCurrentViewContext();
            Attribute<Object> elContext = viewContext.getUIContext().getAttribute();
            return Ognl.getValue(this.getNodeTree(), elContext);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }
}