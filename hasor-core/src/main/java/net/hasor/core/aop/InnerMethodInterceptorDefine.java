package net.hasor.core.aop;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.lang.reflect.Method;
import java.util.function.Predicate;
/**
 *
 * @version : 2014年9月8日
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerMethodInterceptorDefine implements MethodInterceptor, Predicate<Method> {
    private Predicate<Method> aopMatcher     = null;
    private MethodInterceptor aopInterceptor = null;
    //
    public InnerMethodInterceptorDefine(Predicate<Method> aopMatcher, MethodInterceptor aopInterceptor) {
        this.aopMatcher = aopMatcher;
        this.aopInterceptor = aopInterceptor;
    }
    //
    public boolean test(Method target) {
        return this.aopMatcher.test(target);
    }
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return this.aopInterceptor.invoke(invocation);
    }
}