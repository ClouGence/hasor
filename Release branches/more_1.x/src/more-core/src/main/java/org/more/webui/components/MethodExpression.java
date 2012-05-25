package org.more.webui.components;
import java.util.Map;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlException;
import org.more.webui.context.ViewContext;
/**
 * 
 * @version : 2012-5-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class MethodExpression {
    private String expressionString = null;
    private Node   elNodeTree       = null;
    //
    public MethodExpression(String expressionString) {
        this.expressionString = expressionString;
    }
    protected Node getNodeTree() throws OgnlException {
        if (this.elNodeTree == null)
            this.elNodeTree = (Node) Ognl.parseExpression(expressionString);
        return this.elNodeTree;
    }
    public void execute(UIComponent component, ViewContext viewContext) throws OgnlException {
        Map<String, Object> viewEL = viewContext.getViewELContext();
        viewEL.put("this", component);
        Ognl.getValue(this.getNodeTree(), viewEL);
    }
}