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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.test.core.aop.anno.AopBean;
import net.hasor.test.core.aop.anno.ClassAnnoInterceptor;
import net.hasor.test.core.aop.anno.MethodAnnoInterceptor;
import net.hasor.test.core.aop.custom.ClassMyAopBean;
import net.hasor.test.core.aop.custom.MethodMyAopBean;
import net.hasor.test.core.aop.custom.MyAop;
import net.hasor.test.core.aop.custom.MyAopInterceptor;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AnnoTest {
    @Test
    public void sampleAopTest1() throws Exception {
        try (AppContext appContext = Hasor.create().build()) {
            AopBean instance = appContext.getInstance(AopBean.class);
            assert instance instanceof DynamicClass;
        }
    }

    @Test
    public void aopTest1() throws Exception {
        ClassAnnoInterceptor classInterceptor = new ClassAnnoInterceptor();
        MethodAnnoInterceptor methodInterceptor = new MethodAnnoInterceptor();
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(ClassAnnoInterceptor.class, classInterceptor);
            apiBinder.bindType(MethodAnnoInterceptor.class, methodInterceptor);
        });
        //
        AopBean instance = appContext.getInstance(AopBean.class);
        assert instance instanceof DynamicClass;
        //
        instance.doInit(new ArrayList<>());
        assert methodInterceptor.getCallInfo().get("doInit") == null;
        assert classInterceptor.getCallInfo().get("doInit").size() == 2;
        //
        List<?> objects = instance.checkBaseType(true, (byte) 1, (short) 2, 3, 4, 5.0f, 6.0d, 'c');
        assert objects.size() == 8;
        assert (Boolean) objects.get(0);
        assert (Byte) objects.get(1) == 1;
        assert (Short) objects.get(2) == 2;
        assert (Integer) objects.get(3) == 3;
        assert (Long) objects.get(4) == 4;
        assert (Float) objects.get(5) == 5.0f;
        assert (Double) objects.get(6) == 6.0d;
        assert (Character) objects.get(7) == 'c';
        //
        assert instance.aBooleanValue(true) &&//
                classInterceptor.getCallInfo().get("aBooleanValue").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aBooleanValue").size() == 2;
        //
        assert instance.aByteValue((byte) 1) == 1 &&//
                classInterceptor.getCallInfo().get("aByteValue").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aByteValue").size() == 2;
        //
        assert instance.aShort((short) 2) == 2 &&//
                classInterceptor.getCallInfo().get("aShort").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aShort").size() == 2;
        //
        assert instance.aIntValue(3) == 3 &&//
                classInterceptor.getCallInfo().get("aIntValue").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aIntValue").size() == 2;
        //
        assert instance.aLongValue(4) == 4 &&//
                classInterceptor.getCallInfo().get("aLongValue").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aLongValue").size() == 2;
        //
        assert instance.aFloatValue(5.5f) == 5.5f &&//
                classInterceptor.getCallInfo().get("aFloatValue").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aFloatValue").size() == 2;
        //
        assert instance.aDoubleValue(6.0d) == 6.0d &&//
                classInterceptor.getCallInfo().get("aLongValue").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aLongValue").size() == 2;
        //
        assert instance.aCharValue('a') == 'a' &&//
                classInterceptor.getCallInfo().get("aCharValue").size() == 2 &&//
                methodInterceptor.getCallInfo().get("aCharValue").size() == 2;
    }

    @Test
    public void aopTest2() {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            //1.任意类
            Predicate<Class<?>> atClass = Matchers.anyClass();
            //2.有MyAop注解的方法
            Predicate<Method> atMethod = Matchers.annotatedWithMethod(MyAop.class);
            //3.让@MyAop注解生效
            apiBinder.bindInterceptor(atClass, atMethod, new MyAopInterceptor());
        });
        //
        MyAopInterceptor.resetInit();
        assert !MyAopInterceptor.isCalled();
        MethodMyAopBean myAopBean1 = appContext.getInstance(MethodMyAopBean.class);
        assert myAopBean1.fooCall("abc").equals("call back : abc");
        assert MyAopInterceptor.isCalled();
        //
        MyAopInterceptor.resetInit();
        assert !MyAopInterceptor.isCalled();
        ClassMyAopBean myAopBean2 = appContext.getInstance(ClassMyAopBean.class);
        assert myAopBean2.fooCall("abc").equals("call back : abc");
        assert MyAopInterceptor.isCalled();
    }
}