/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.more.test.classcode;
import java.lang.annotation.Annotation;
import org.junit.Test;
import org.more.classcode.AopBeforeListener;
import org.more.classcode.AopFilterChain;
import org.more.classcode.AopInvokeFilter;
import org.more.classcode.AopReturningListener;
import org.more.classcode.AopThrowingListener;
import org.more.classcode.BuilderMode;
import org.more.classcode.ClassEngine;
import org.more.classcode.Method;
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public class AopTest {
    @Test
    public void test_1() throws Exception {
        ClassEngine ce = new ClassEngine(TestBean.class);
        ce.addListener(new Test_BeforeListener());
        ce.addListener(new Test_ReturningListener());
        ce.addListener(new Test_ThrowingListener());
        ce.addAopFilter(new Test_Filter(1));
        ce.addAopFilter(new Test_Filter(2));
        //
        //        FileOutputStream fos = new FileOutputStream(ce.builderClass().getSimpleName() + ".class");
        //        fos.write(ce.toBytes());
        //        fos.flush();
        //        fos.close();
        //
        TestBean obj = (TestBean) ce.newInstance(null);
        obj.setLong(123l);
        System.out.println(obj.p_long);
    };
    @Test
    public void test_2() throws Exception {
        ClassEngine ce = new ClassEngine(TestBean2.class);
        ce.setBuilderMode(BuilderMode.Super);
        //
        ce.addListener(new Test_BeforeListener());
        ce.addListener(new Test_ReturningListener());
        ce.addListener(new Test_ThrowingListener());
        ce.addAopFilter(new Test_Filter(1));
        ce.addAopFilter(new Test_Filter(2));
        //
        //        FileOutputStream fos = new FileOutputStream(ce.builderClass().getSimpleName() + ".class");
        //        fos.write(ce.toBytes());
        //        fos.flush();
        //        fos.close();
        Object obj = ce.newInstance(new TestBean2());
        TestBean2_Face face = (TestBean2_Face) obj;
        //
        face.setP_double(12);
        face.getP_double();
        face.setP_float(12.56f);
        face.getP_float();
        face.setP_long(13l);
        face.getP_long();
        //
        //
        System.out.println(face.getP_long());
    };
    public void print() {
        System.out.println("print method");
    }
}
class Test_Filter implements AopInvokeFilter {
    private int i = 0;
    public Test_Filter(int i) {
        this.i = i;
    }
    public Object doFilter(Object target, Method method, Object[] args, AopFilterChain chain) throws Throwable {
        System.out.println(i);
        return chain.doInvokeFilter(target, method, args);
    }
}
class Test_BeforeListener implements AopBeforeListener {
    public void beforeInvoke(Object target, Method method, Object[] args) {
        System.out.println("beforeInvoke");
        Annotation annos = method.getTargetMeyhod().getAnnotation(Test.class);
        System.out.println(annos);
    }
}
class Test_ReturningListener implements AopReturningListener {
    public void returningInvoke(Object target, Method method, Object[] args, Object result) {
        System.out.println("returningListener");
    }
}
class Test_ThrowingListener implements AopThrowingListener {
    public Throwable throwsException(Object target, Method method, Object[] args, Throwable e) {
        System.out.println("throwingListener");
        return e;
    }
}