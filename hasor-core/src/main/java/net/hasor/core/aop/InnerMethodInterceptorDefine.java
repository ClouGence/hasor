package net.hasor.core.aop;
import net.hasor.core.Matcher;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.lang.reflect.Method;
/**
 *
 * @version : 2014年9月8日
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerMethodInterceptorDefine implements MethodInterceptor, Matcher<Method> {
    private Matcher<Method>   aopMatcher     = null;
    private MethodInterceptor aopInterceptor = null;
    //
    public InnerMethodInterceptorDefine(Matcher<Method> aopMatcher, MethodInterceptor aopInterceptor) {
        this.aopMatcher = aopMatcher;
        this.aopInterceptor = aopInterceptor;
    }
    //
    public boolean matches(Method target) {
        return this.aopMatcher.matches(target);
    }
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return this.aopInterceptor.invoke(invocation);
    }
}