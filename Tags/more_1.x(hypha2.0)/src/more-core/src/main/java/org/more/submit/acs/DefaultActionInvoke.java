package org.more.submit.acs;
import java.lang.reflect.Method;
import org.more.core.error.InvokeException;
import org.more.submit.ActionInvoke;
import org.more.submit.ActionStack;
/**
 * 
 * @version : 2012-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultActionInvoke implements ActionInvoke {
    private Object target = null;
    private String method = null;
    public DefaultActionInvoke(Object target, String method) {
        this.target = target;
        this.method = method;
    }
    public Object invoke(ActionStack stack) throws Throwable {
        Class<?> type = this.target.getClass();
        Method[] m = type.getMethods();
        Method method = null;
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals(this.method) == false)
                continue; //名称不一致忽略
            if (m[i].getParameterTypes().length != 1)
                continue; //参数长度不一致忽略
            if (ActionStack.class.isAssignableFrom(m[i].getParameterTypes()[0]) == true) {
                method = m[i];//符合条件
                break;
            }
        }
        if (method == null)//如果找不到方法则引发异常
            throw new InvokeException("无法执行[" + this.method + "]，找不着匹配的方法。");
        return method.invoke(target, stack);
    }
}