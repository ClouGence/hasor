/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.hasor.more._01_classcode;
import org.junit.Test;
import org.more.classcode.aop.AopClassConfig;
import org.more.classcode.aop.AopInterceptor;
import org.more.classcode.aop.AopInvocation;
import net.test.hasor.more._01_classcode.beans.TestBean;
import net.test.hasor.more._01_classcode.beans.TestBean2;
import net.test.hasor.more._01_classcode.beans.TestBean2_Face;
/**
 *
 * @version 2010-8-25
 * @author 赵永春 (zyc@hasor.net)
 */
public class DynamicProxyTest {
    @Test
    public void test_1() throws Exception {
        AopClassConfig ce = new AopClassConfig(TestBean.class);
        ce.addAopInterceptor(new Test_Filter(1));
        ce.addAopInterceptor(new Test_Filter(2));
        //
        //        FileOutputStream fos = new FileOutputStream(ce.builderClass().getSimpleName() + ".class");
        //        fos.write(ce.toBytes());
        //        fos.flush();
        //        fos.close();
        //
        TestBean obj = (TestBean) ce.toClass().newInstance();
        obj.setLong(123l);
        System.out.println(obj.p_long);
    };
    @Test
    public void test_2() throws Exception {
        AopClassConfig ce = new AopClassConfig(TestBean2.class);
        ce.addAopInterceptor(new Test_Filter(1));
        //
        //        FileOutputStream fos = new FileOutputStream(ce.builderClass().getSimpleName() + ".class");
        //        fos.write(ce.toBytes());
        //        fos.flush();
        //        fos.close();
        //
        TestBean2_Face face = (TestBean2_Face) ce.toClass().newInstance();
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
class Test_Filter implements AopInterceptor {
    private int i = 0;
    public Test_Filter(int i) {
        this.i = i;
    }
    @Override
    public Object invoke(AopInvocation invocation) throws Throwable {
        System.out.println("filter-" + i);
        return invocation.proceed();
    }
}