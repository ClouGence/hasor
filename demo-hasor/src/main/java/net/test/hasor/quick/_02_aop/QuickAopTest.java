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
package net.test.hasor.quick._02_aop;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.test.hasor.quick.example.AopBean;
import org.junit.Test;
/**
 * 
 * @version : 2015年1月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class QuickAopTest {
    @Test
    public void startTest1() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>startTest1<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(AopBean.class);
            }
        });
        AopBean aopBean = appContext.getInstance(AopBean.class);
        //
        aopBean.print();
    }
}