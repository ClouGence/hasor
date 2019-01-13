package net.hasor.core.aop;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.exts.aop.AopModule;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
public class AopTest {
    private AppContext appContext;
    @Before
    public void testBefore() throws IOException {
        appContext = Hasor.create().asSmaller().build(new Module() {
            @Override
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.installModule(new AopModule());
            }
        });
    }
    //
    @Test
    public void aopTest0() throws IOException {
        //
        ArrayList<String> events = new ArrayList<String>();
        AopBean aopBean = this.appContext.getInstance(AopBean.class);
        //
        aopBean.doInit(events);
        //
        assert events.size() == 3;
        assert events.get(0).equals("BEFORE") && events.get(1).equals("DO") && events.get(2).equals("AFTER");
    }
    //
    //    @Test
    //    public void aopTest0() throws IOException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    //        System.setProperty("net.hasor.core.container.classengine.debug", "true");
    //        new AopTestAopBean();
    //        //
    //        AopClassConfig classConfig = new AopClassConfig(AopBean.class);
    //        classConfig.addAopInterceptor(new Matcher<Method>() {
    //            @Override
    //            public boolean matches(Method target) {
    //                return target.getName().equals("checkBaseType1");
    //            }
    //        }, new MethodInterceptor() {
    //            @Override
    //            public Object invoke(MethodInvocation invocation) throws Throwable {
    //                try {
    //                    System.out.println("before");
    //                    Object obj = invocation.proceed();
    //                    System.out.println("after");
    //                    return obj;
    //                } catch (Exception e) {
    //                    throw e;
    //                } finally {
    //                    System.out.println("after");
    //                }
    //            }
    //        });
    //        Class<?> aClass = classConfig.buildClass();
    //        AopBean o = (AopBean) aClass.newInstance();
    //        //        o.checkBaseType0(true, (byte) 1, (short) 2, 3, 4, 5.0f, 6.0d);
    //        //        o.checkBaseType1(12L, 1.2d);
    //    }
    //
    //    public void aopTest1() {
    //        AopBean aopBean = this.appContext.getInstance(AopBean.class);
    //
    //        //
    //        //        List<?> objects = aopBean.checkBaseType(true, (byte) 1, (short) 2, 3, 4, 5.0f, 6.0d);
    //        events.clear();
    //        List<?> objects = aopBean.checkBaseType0(true, (byte) 1, (short) 2, 3, 4, 5.0f, 6.0d);
    //        assert objects.size() == 7;
    //        assert (Boolean) objects.get(0);
    //        assert (Byte) objects.get(1) == 1;
    //        assert (Short) objects.get(2) == 2;
    //        assert (Integer) objects.get(3) == 3;
    //        assert (Long) objects.get(4) == 4;
    //        assert (Float) objects.get(5) == 5.0f;
    //        assert (Double) objects.get(6) == 6.0d;
    //        //        ClassWriter cw = new ClassWriter(new);
    //    }
}