package org.hasor.test.mvc.plugin.log.support;
import java.lang.reflect.Method;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.ModuleSettings;
import net.hasor.core.anno.DefineModule;
import net.hasor.core.module.AbstractHasorModule;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hasor.test.mvc.plugin.log.OutLog;
import com.google.inject.matcher.AbstractMatcher;
/**
 * 日志服务。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
@DefineModule(displayName = "LogPlatformListener", description = "org.test.plugin.log软件包功能支持。")
public class LogPlatformListener extends AbstractHasorModule {
    public void configuration(ModuleSettings info) {}
    public void init(ApiBinder apiBinder) {
        //1.挂载Aop
        apiBinder.getGuiceBinder().bindInterceptor(new ClassOutLogMatcher(), new MethodOutLogMatcher(), new OutLogInterceptor());
    }
    /*-------------------------------------------------------------------------------------*/
    /*负责检测类是否匹配。规则：只要类型或方法上标记了@OutLog。*/
    private class ClassOutLogMatcher extends AbstractMatcher<Class<?>> {
        public boolean matches(Class<?> matcherType) {
            if (matcherType.isAnnotationPresent(OutLog.class) == true)
                return true;
            Method[] m1s = matcherType.getMethods();
            Method[] m2s = matcherType.getDeclaredMethods();
            for (Method m1 : m1s) {
                if (m1.isAnnotationPresent(OutLog.class) == true)
                    return true;
            }
            for (Method m2 : m2s) {
                if (m2.isAnnotationPresent(OutLog.class) == true)
                    return true;
            }
            return false;
        }
    }
    /*负责检测方法是否匹配。规则：方法或方法所处类上标记了@OutLog。*/
    private class MethodOutLogMatcher extends AbstractMatcher<Method> {
        public boolean matches(Method matcherType) {
            if (matcherType.isAnnotationPresent(OutLog.class) == true)
                return true;
            if (matcherType.getDeclaringClass().isAnnotationPresent(OutLog.class) == true)
                return true;
            return false;
        }
    }
    /*拦截器*/
    private class OutLogInterceptor implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            //1.获取缓存数据
            Method targetMethod = invocation.getMethod();
            OutLog cacheAnno = targetMethod.getAnnotation(OutLog.class);
            if (cacheAnno == null)
                cacheAnno = targetMethod.getDeclaringClass().getAnnotation(OutLog.class);
            if (cacheAnno == null)
                return invocation.proceed();
            //2.获取Key
            StringBuilder logKey = new StringBuilder(targetMethod.toString());
            //            Object[] args = invocation.getArguments();
            //            if (args != null)
            //                for (Object arg : args) {
            //                    if (arg == null) {
            //                        logKey.append("NULL");
            //                        continue;
            //                    }
            //                    /*保证arg参数不为空*/
            //                    logKey.append(ObjectKeyBuilder.serializeKey(arg));
            //                }
            //4.操作缓存
            String key = logKey.toString();
            long t = System.currentTimeMillis();
            Object returnData = invocation.proceed();
            System.out.println(String.format("log invoke at %s, use time: %s.", key, (System.currentTimeMillis() - t)));
            return returnData;
        }
    }
}