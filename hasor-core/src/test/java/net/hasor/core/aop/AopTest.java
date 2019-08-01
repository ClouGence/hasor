/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.core.aop;
import net.hasor.test.beans.aop.AopBeanInterceptor;
import net.hasor.test.beans.aop.GenericsMethodAopBean;
import net.hasor.test.beans.aop.ThrowAopBean;
import net.hasor.test.beans.basic.inject.constructor.ConstructorBean;
import net.hasor.test.beans.basic.inject.property.PropertyBean;
import net.hasor.test.beans.enums.SelectEnum;
import net.hasor.utils.CommonCodeUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class AopTest {
    @Test
    public void aopTest1() throws Exception {
        AopBeanInterceptor aopInterceptor = new AopBeanInterceptor();
        AopClassConfig classConfig = new AopClassConfig(PropertyBean.class);
        classConfig.addAopInterceptor(aopInterceptor);
        Class<?> buildClass = classConfig.buildClass();
        PropertyBean instance = (PropertyBean) buildClass.newInstance();
        //
        //
        assert AopClassLoader.isDynamic(instance);
        assert AopClassLoader.isDynamic(instance.getClass());
        assert AopClassLoader.getPrototypeType(instance) == PropertyBean.class;
        assert AopClassLoader.getPrototypeType(instance.getClass()) == PropertyBean.class;
        //
        assert !AopClassLoader.isDynamic(PropertyBean.class);
        assert !AopClassLoader.isDynamic(new PropertyBean());
        //
        {
            instance.setByteValue((byte) 1);
            assert aopInterceptor.getCallInfo().get("setByteValue").size() == 2;
            assert aopInterceptor.getCallInfo().get("setByteValue").contains("BEFORE");
            assert aopInterceptor.getCallInfo().get("setByteValue").contains("AFTER");
            assert instance.getByteValue() == 1;
        }
        //
        //
        instance.setByteValue((byte) 1);
        assert instance.getByteValue() == 1;
        instance.setByteValue2(null);
        assert instance.getByteValue2() == null;
        instance.setByteValue2((byte) 2);
        assert instance.getByteValue2() == 2;
        //
        //
        instance.setShortValue((short) 2);
        assert instance.getShortValue() == 2;
        instance.setShortValue2((short) 2);
        assert instance.getShortValue2() == 2;
        instance.setShortValue2(null);
        assert instance.getShortValue2() == null;
        //
        //
        instance.setIntValue(3);
        assert instance.getIntValue() == 3;
        instance.setIntValue2(3);
        assert instance.getIntValue2() == 3;
        instance.setIntValue2(null);
        assert instance.getIntValue2() == null;
        //
        //
        instance.setLongValue(4);
        assert instance.getLongValue() == 4;
        instance.setLongValue2((long) 4);
        assert instance.getLongValue2() == 4;
        instance.setLongValue2(null);
        assert instance.getLongValue2() == null;
        //
        //
        instance.setFloatValue(5.5f);
        assert instance.getFloatValue() == 5.5f;
        instance.setFloatValue2(5.5f);
        assert instance.getFloatValue2() == 5.5f;
        instance.setFloatValue2(null);
        assert instance.getFloatValue2() == null;
        //
        //
        instance.setDoubleValue(6.6d);
        assert instance.getDoubleValue() == 6.6d;
        instance.setDoubleValue2(6.6d);
        assert instance.getDoubleValue2() == 6.6d;
        instance.setDoubleValue2(null);
        assert instance.getDoubleValue2() == null;
        //
        //
        instance.setBooleanValue(true);
        assert instance.isBooleanValue();
        instance.setBooleanValue2(true);
        assert instance.getBooleanValue2();
        instance.setBooleanValue2(null);
        assert instance.getBooleanValue2() == null;
        //
        //
        instance.setCharValue('$');
        assert instance.getCharValue() == '$';
        instance.setCharValue2('#');
        assert instance.getCharValue2() == '#';
        instance.setCharValue2(null);
        assert instance.getCharValue2() == null;
        //
        //
        instance.setStringValue("abc");
        assert instance.getStringValue().equals("abc");
    }

    @Test
    public void aopTest2() throws Exception {
        Class[] initTypes = new Class[] {   //
                Byte.TYPE                   //
                , Byte.class                //
                , Short.TYPE                //
                , Short.class               //
                , Integer.TYPE              //
                , Integer.class             //
                , Long.TYPE                 //
                , Long.class                //
                , Float.TYPE                //
                , Float.class               //
                , Double.TYPE               //
                , Double.class              //
                , Boolean.TYPE              //
                , Boolean.class             //
                , Character.TYPE            //
                , Character.class           //
                , Date.class                //
                , java.sql.Date.class       //
                , Time.class                //
                , Timestamp.class           //
                , String.class              //
                , SelectEnum.class          //
        };
        Object[] initParams = new Object[] {//
                (byte) 1                    //
                , (byte) 1                  //
                , (short) 2                 //
                , (short) 2                 //
                , (int) 3                   //
                , (int) 3                   //
                , (long) 4                  //
                , (long) 4                  //
                , (float) 5.5               //
                , (float) 5.5               //
                , (double) 6.6              //
                , (double) 6.6              //
                , (boolean) true            //
                , (boolean) true            //
                , (char) 'a'                //
                , (char) 'a'                //
                , new Date()                //
                , new java.sql.Date(System.currentTimeMillis())     //
                , new Time(System.currentTimeMillis())              //
                , new Timestamp(System.currentTimeMillis())         //
                , "abc"                                             //
                , SelectEnum.Three                                  //
        };
        //
        AopBeanInterceptor aopInterceptor = new AopBeanInterceptor();
        AopClassConfig classConfig = new AopClassConfig(ConstructorBean.class);
        classConfig.addAopInterceptor(aopInterceptor);
        Class<?> buildClass = classConfig.buildClass();
        Constructor<?> constructor = buildClass.getConstructor(initTypes);
        ConstructorBean instance = (ConstructorBean) constructor.newInstance(initParams);
        //
        //
        assert AopClassLoader.isDynamic(instance);
        assert AopClassLoader.isDynamic(instance.getClass());
        assert AopClassLoader.getPrototypeType(instance) == ConstructorBean.class;
        assert AopClassLoader.getPrototypeType(instance.getClass()) == ConstructorBean.class;
        //
        assert !AopClassLoader.isDynamic(ConstructorBean.class);
        assert !AopClassLoader.isDynamic(PowerMockito.mock(ConstructorBean.class));
        //
        {
            assert instance.getByteValue() == 1;
            assert aopInterceptor.getCallInfo().get("getByteValue").size() == 2;
            assert aopInterceptor.getCallInfo().get("getByteValue").contains("BEFORE");
            assert aopInterceptor.getCallInfo().get("getByteValue").contains("AFTER");
        }
        //
        //
        assert instance.getByteValue() == 1;
        assert instance.getByteValue2() == 1;
        //
        //
        assert instance.getShortValue() == 2;
        assert instance.getShortValue2() == 2;
        //
        //
        assert instance.getIntValue() == 3;
        assert instance.getIntValue2() == 3;
        //
        //
        assert instance.getLongValue() == 4;
        assert instance.getLongValue2() == 4;
        //
        //
        assert instance.getFloatValue() == 5.5f;
        assert instance.getFloatValue2() == 5.5f;
        //
        //
        assert instance.getDoubleValue() == 6.6d;
        assert instance.getDoubleValue2() == 6.6d;
        //
        //
        assert instance.isBooleanValue();
        assert instance.getBooleanValue2();
        //
        //
        assert instance.getCharValue() == 'a';
        assert instance.getCharValue2() == 'a';
        //
        //
        assert instance.getStringValue().equals("abc");
    }

    @Test
    public void aopTest3() throws Exception {
        AopBeanInterceptor aopInterceptor = new AopBeanInterceptor();
        AopClassConfig classConfig = new AopClassConfig(PropertyBean.class);
        classConfig.addAopInterceptor(aopInterceptor);
        Class<?> buildClass = classConfig.buildClass();
        PropertyBean instance = (PropertyBean) buildClass.newInstance();
        //
        //
        ClassLoader loader = instance.getClass().getClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream(instance.getClass().getName().replace(".", "/") + ".class");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(resourceAsStream, out);
        //
        byte[] byteArray1 = out.toByteArray();
        byte[] byteArray2 = classConfig.getBytes();
        //
        String encodeMD51 = CommonCodeUtils.MD5.encodeMD5(byteArray1);
        String encodeMD52 = CommonCodeUtils.MD5.encodeMD5(byteArray2);
        assert encodeMD51.equals(encodeMD52);
        //
        assert buildClass.getName().equals(instance.getClass().getName());
        assert buildClass.getName().contains(PropertyBean.class.getName() + AopClassConfig.aopClassSuffix);
    }

    @Test
    public void aopTest4() throws Exception {
        AopBeanInterceptor aopInterceptor = new AopBeanInterceptor();
        AopClassConfig classConfig = new AopClassConfig(PropertyBean.class);
        classConfig.addAopInterceptor(aopInterceptor);
        //
        File templeFile = new File("target/tmp_" + System.currentTimeMillis());
        templeFile.deleteOnExit();
        templeFile.mkdirs();
        classConfig.classWriteToPath(templeFile);
        //
        Class<?> buildClass = classConfig.buildClass();
        PropertyBean instance = (PropertyBean) buildClass.newInstance();
        //
        ClassLoader loader = instance.getClass().getClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream(instance.getClass().getName().replace(".", "/") + ".class");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(resourceAsStream, out);
        //
        byte[] byteArray1 = out.toByteArray();
        byte[] byteArray2 = classConfig.getBytes();
        //
        String encodeMD51 = CommonCodeUtils.MD5.encodeMD5(byteArray1);
        String encodeMD52 = CommonCodeUtils.MD5.encodeMD5(byteArray2);
        assert encodeMD51.equals(encodeMD52);
        //
        assert buildClass.getName().equals(instance.getClass().getName());
        assert buildClass.getName().contains(PropertyBean.class.getName() + AopClassConfig.aopClassSuffix);
    }

    @Test
    public void aopTest5() throws Exception {
        AopBeanInterceptor aopInterceptor = new AopBeanInterceptor();
        AopClassConfig classConfig = new AopClassConfig(ThrowAopBean.class);
        classConfig.addAopInterceptor(aopInterceptor);
        //
        Class<?> buildClass = classConfig.buildClass();
        ThrowAopBean instance = (ThrowAopBean) buildClass.newInstance();
        //
        try {
            instance.fooCall("abc");
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("abc");
            assert e.getCause().getMessage().equals("abc");
            assert !e.getMessage().equals("abc");
        }
        //
        assert aopInterceptor.getCallInfo().get("fooCall").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall").contains("THROW");
    }

    @Test
    public void aopTest6() throws Exception {
        AopBeanInterceptor aopInterceptor = new AopBeanInterceptor();
        AopClassConfig classConfig = new AopClassConfig(GenericsMethodAopBean.class);
        classConfig.addAopInterceptor(aopInterceptor);
        //
        Class<?> buildClass = classConfig.buildClass();
        GenericsMethodAopBean instance = (GenericsMethodAopBean) buildClass.newInstance();
        //
        instance.fooCall1("abc1", "abc2", "abc3");
        assert aopInterceptor.getCallInfo().get("fooCall1").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall1").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall1").contains("AFTER");
        //
        instance.fooCall2(new Date(), new ArrayList());
        assert aopInterceptor.getCallInfo().get("fooCall2").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall2").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall2").contains("AFTER");
        //
        instance.fooCall3(Date.class, new ArrayList<>());
        assert aopInterceptor.getCallInfo().get("fooCall3").size() == 2;
        assert aopInterceptor.getCallInfo().get("fooCall3").contains("BEFORE");
        assert aopInterceptor.getCallInfo().get("fooCall3").contains("AFTER");
    }
}