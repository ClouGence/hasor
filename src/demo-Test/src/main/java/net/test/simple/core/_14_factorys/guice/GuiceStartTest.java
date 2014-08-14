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
package net.test.simple.core._14_factorys.guice;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.context.factorys.guice.GuiceRegisterFactoryCreater;
import org.junit.Test;
import com.google.inject.Injector;
/**
 * 本示列演示如何启动 Hasor 框架。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class GuiceStartTest {
    @Test
    public void guiceStartTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>guiceStartTest<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext(new GuiceRegisterFactoryCreater());
        //
        Injector guice = appContext.getInstance(Injector.class);
        //
        GuiceStartTest a = guice.getInstance(GuiceStartTest.class);
        System.out.println(a);
    }
}