package net.hasor.core.aop;
import net.hasor.core.*;
import net.hasor.core.aop.interceptor.TransparentInterceptor;
import net.hasor.core.exts.aop.AopModule;
import net.hasor.core.setting.SettingsWrap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.*;
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
        ArrayList<String> events = new ArrayList<String>();
        AopBean aopBean = this.appContext.getInstance(AopBean.class);
        //
        aopBean.doInit(events);
        assert events.size() == 3;
        assert events.get(0).equals("BEFORE") && events.get(1).equals("DO") && events.get(2).equals("AFTER");
        //
        List<?> objects = aopBean.checkBaseType0(true, (byte) 1, (short) 2, 3, 4, 5.0f, 6.0d, 'c');
        assert objects.size() == 10;
        assert "Before".equals(objects.get(0));
        assert (Boolean) objects.get(1);
        assert (Byte) objects.get(2) == 1;
        assert (Short) objects.get(3) == 2;
        assert (Integer) objects.get(4) == 3;
        assert (Long) objects.get(5) == 4;
        assert (Float) objects.get(6) == 5.0f;
        assert (Double) objects.get(7) == 6.0d;
        assert (Character) objects.get(8) == 'c';
        assert "After".equals(objects.get(9));
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aBooleanValue(true) && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aByteValue((byte) 1) == 1 && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aShort((short) 2) == 2 && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aIntValue(3) == 3 && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aLongValue(4) == 4 && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aFloatValue(5.0f) == 5.0f && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aDoubleValue(6.0d) == 6.0d && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        assert aopBean.aCharValue('a') == 'a' && TransparentInterceptor.isCalled();
        //
        TransparentInterceptor.resetInit();
        Map<String, Object> objectMap1 = aopBean.signatureMethod(new LinkedList<SettingsWrap>(), new SettingsWrap(null));
        assert objectMap1.size() == 2 && TransparentInterceptor.isCalled();
        //
        List<? super Date> param1 = new ArrayList<Serializable>();
        List<? extends Date> param2 = new ArrayList<Time>();
        TransparentInterceptor.resetInit();
        Map<String, Object> objectMap2 = aopBean.signatureMethod(param1, param2);
        assert objectMap2.size() == 2 && TransparentInterceptor.isCalled();
        //
        assert AopClassLoader.isDynamic(aopBean);
        assert AopClassLoader.isDynamic(aopBean.getClass());
        assert AopClassLoader.getPrototypeType(aopBean) == AopBean.class;
        assert AopClassLoader.getPrototypeType(aopBean.getClass()) == AopBean.class;
        //
        assert !AopClassLoader.isDynamic(AopBean.class);
        assert !AopClassLoader.isDynamic(new AopBean());
    }
    //
    @Test
    public void aopTest1() throws IOException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        AopClassConfig classConfig = new AopClassConfig(AopBean.class);
        classConfig.debug(true, new File("target"));
        classConfig.addAopInterceptor(new Matcher<Method>() {
            @Override
            public boolean matches(Method target) {
                return target.getName().equals("aBooleanValue");
            }
        }, new TransparentInterceptor());
        Class<?> aClass = classConfig.buildClass();
        AopBean o = (AopBean) aClass.newInstance();
        //
        //
        TransparentInterceptor.resetInit();
        assert o.aBooleanValue(true) && TransparentInterceptor.isCalled();
        assert classConfig.getSimpleName().startsWith(AopBean.class.getSimpleName() + AopClassConfig.aopClassSuffix);
    }
    //
    @Test
    public void aopTest2() throws NoSuchMethodException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        AopClassConfig classConfig = new AopClassConfig(ConstructorAopBean.class);
        classConfig.debug(true, new File("target"));
        classConfig.addAopInterceptor(new TransparentInterceptor());
        Class<?> aClass = classConfig.buildClass();
        //
        Class[] initTypes = new Class[] { Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, Character.TYPE };
        Object[] initParams = new Object[] { (byte) 1, (short) 2, (int) 3, (long) 4, (float) 5, (double) 6, (boolean) true, (char) 'a' };
        ConstructorAopBean o = (ConstructorAopBean) aClass.getConstructor(initTypes).newInstance(initParams);
        //
        //
        TransparentInterceptor.resetInit();
        assert o.getByteValue() == 1;
        assert o.getShortValue() == 2;
        assert o.getIntValue() == 3;
        assert o.getLongValue() == 4;
        assert o.getFloatValue() == 5;
        assert o.getDoubleValue() == 6;
        assert o.isBooleanValue();
        assert o.getCharValue() == 'a';
        assert TransparentInterceptor.isCalled();
    }
}