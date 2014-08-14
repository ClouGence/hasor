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
package net.test.simple.core._06_aop;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import net.test.simple.core._06_aop.objs.CustomAnnoFooBean;
import net.test.simple.core._06_aop.objs.FooBean;
import net.test.simple.core._06_aop.objs.MyAop;
import net.test.simple.core._06_aop.objs.SimpleInterceptor;
import org.junit.Test;
/**
 * 测试 Aop
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class CustomAnnoAopTest {
    @Test
    public void customAnnoAopTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>customAnnoAopTest<<--");
        AppContext appContext = Hasor.createAppContext(new WarpAnnoAop());
        //
        FooBean fooBean1 = appContext.getInstance(FooBean.class);
        FooBean fooBean2 = appContext.getInstance(CustomAnnoFooBean.class);
        //
        System.out.println("---fooBean1.fooCall()---");
        fooBean1.fooCall();
        System.out.println("---fooBean2.fooCall()---");
        fooBean2.fooCall();
    }
}
class WarpAnnoAop implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        /*绑定类型*/
        apiBinder.bindType(FooBean.class);
        apiBinder.bindType(CustomAnnoFooBean.class);
        /*当类配置了MyAop注解时，该类的任意方法使用Aop拦截*/
        apiBinder.bindInterceptor(AopMatchers.annotatedWithClass(MyAop.class),//
                AopMatchers.anyMethod(), new SimpleInterceptor());
    }
}