package org.more.classcode;
import java.lang.reflect.Method;
/**
 * 
 * @version : 2014年9月8日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerAopInterceptor implements AopInterceptor, AopMatcher {
    private AopMatcher     aopMatcher     = null;
    private AopInterceptor aopInterceptor = null;
    //
    public InnerAopInterceptor(AopMatcher aopMatcher, AopInterceptor aopInterceptor) {
        this.aopMatcher = aopMatcher;
        this.aopInterceptor = aopInterceptor;
    }
    // 
    public boolean matcher(Method target) {
        return this.aopMatcher.matcher(target);
    }
    public Object invoke(AopInvocation invocation) throws Throwable {
        return this.aopInterceptor.invoke(invocation);
    }
}