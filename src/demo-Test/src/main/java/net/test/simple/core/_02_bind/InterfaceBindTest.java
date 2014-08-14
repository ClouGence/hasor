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
package net.test.simple.core._02_bind;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.test.simple.core._03_beans.pojo.PojoBean;
import net.test.simple.core._03_beans.pojo.PojoInfo;
import org.junit.Test;
/**
 * 本示列演示如何通过接口绑定Bean。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class InterfaceBindTest {
    @Test
    public void faceBindTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>faceBindTest<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                /*绑定一个接口的实现类*/
                apiBinder.bindType(PojoInfo.class).to(PojoBean.class);
            }
        });
        //
        //通过接口获取绑定的Bean
        PojoInfo myBean2 = appContext.getInstance(PojoInfo.class);
        System.out.println(myBean2.getName() + "\t" + myBean2);
    }
}