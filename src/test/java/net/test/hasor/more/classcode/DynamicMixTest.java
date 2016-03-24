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
package net.test.hasor.more.classcode;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.more.classcode.AbstractClassConfig;
import org.more.classcode.aop.AopClassConfig;
import org.more.classcode.aop.AopInterceptor;
import org.more.classcode.aop.AopInvocation;
import org.more.classcode.delegate.faces.DefaultMethodDelegate;
import org.more.classcode.delegate.faces.MethodClassConfig;
import org.more.classcode.delegate.property.PropertyClassConfig;
import org.more.classcode.delegate.property.SimplePropertyDelegate;
import net.test.hasor.more.classcode.beans.Bean;
/**
 * 
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class DynamicMixTest {
    public static void write(AbstractClassConfig cc) throws IOException {
        FileOutputStream fos = new FileOutputStream(cc.getSimpleName() + ".class");
        fos.write(cc.buildBytes());
        fos.flush();
        fos.close();
    }
    //
    public static void main(String[] args) throws Exception {
        AopClassConfig aCC = new AopClassConfig(Bean.class);
        aCC.addAopInterceptor(new TestAopInterceptor());
        //
        PropertyClassConfig pCC = new PropertyClassConfig(aCC.toClass());
        pCC.addProperty("name", new TestSimplePropertyDelegate(String.class));
        //
        MethodClassConfig mCC = new MethodClassConfig(pCC.toClass());
        mCC.addDelegate(List.class, new DefaultMethodDelegate());
        //
        write(aCC);
        write(pCC);
        write(mCC);
        //
        Class<?> ccType = mCC.toClass();
        Bean bean = (Bean) ccType.newInstance();
        //
        bean.print(0, 0);
        //
        PropertyDescriptor pd = new PropertyDescriptor("name", ccType);
        Object wData = pd.getWriteMethod().invoke(bean, "new Value");
        Object rData = pd.getReadMethod().invoke(bean);
        System.out.println(rData);
        //
        List beanList = (List) bean;
        beanList.set(0, new Object());
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