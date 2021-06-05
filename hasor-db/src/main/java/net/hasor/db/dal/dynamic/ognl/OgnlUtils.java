package net.hasor.db.dal.dynamic.ognl;
import net.hasor.utils.ExceptionUtils;
import ognl.Ognl;
import ognl.OgnlContext;

public class OgnlUtils {
    public static Object evalOgnl(String exprString, Object root) {
        try {
            OgnlContext context = new OgnlContext(null, null, new OgnlMemberAccess(true));
            return Ognl.getValue(exprString, context, root);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}
