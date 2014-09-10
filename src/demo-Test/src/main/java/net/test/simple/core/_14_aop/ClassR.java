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
package net.test.simple.core._14_aop;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import org.more.classcode.AopInterceptor;
import org.more.classcode.AopInvocation;
import org.more.classcode.ClassConfig;
import org.more.classcode.objects.SimplePropertyDelegate;
/**
 * 
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClassR {
    public static void main(String[] args) throws Exception {
        //1.基本信息
        ClassConfig cc = new ClassConfig(Bean.class);
        cc.addAopInterceptor(new TestAopInterceptor());
        cc.addProperty("name", new TestSimplePropertyDelegate(int.class));
        //
        Class<?> ccType = cc.toClass();
        //
        FileOutputStream fos = new FileOutputStream(ccType.getSimpleName() + ".class");
        fos.write(cc.toBytes());
        fos.flush();
        fos.close();
        //
        Bean bean = (Bean) ccType.newInstance();
        //
        bean.print(0, 0);
        //
        PropertyDescriptor pd = new PropertyDescriptor("name", ccType);
        Object wData = pd.getWriteMethod().invoke(bean, 12);
        Object rData = pd.getReadMethod().invoke(bean);
        System.out.println(rData);
    }
}
class TestSimplePropertyDelegate extends SimplePropertyDelegate {
    public TestSimplePropertyDelegate(Class<?> targetType) {
        super(targetType);
    }
}
class TestAopInterceptor implements AopInterceptor {
    public Object invoke(AopInvocation invocation) throws Throwable {
        try {
            System.out.println("before...");
            return invocation.proceed();
        } catch (Exception e) {
            System.out.println("throws...");
            throw e;
        } finally {
            System.out.println("after...");
        }
    }
}