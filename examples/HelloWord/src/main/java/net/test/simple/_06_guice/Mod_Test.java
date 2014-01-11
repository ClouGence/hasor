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
package net.test.simple._06_guice;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.AnnoStandardAppContext;
import org.junit.Test;
/**
 * 将 Guice 模块注册到 Hasor 上演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class Mod_Test {
    /*测试，使用 @GuiceModule 注解注册 Guice 模块*/
    @Test
    public void test_annoGuice() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_annoGuice<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("net/test/simple/_06_guice/gucie-config.xml");
        //
        appContext.start();
        //获取模块中绑定的 int 值
        System.out.println(appContext.getInstance(Integer.class));
    }
    /*测试，使用代码手动注册 Guice 模块*/
    @Test
    public void test_codeGuice() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_codeGuice<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("net/test/simple/_06_guice/gucie-config.xml");
        //在 start 之前注册 Guice 模块
        appContext.addGuiceModule(new GuiceMod2());
        //
        appContext.start();
        //获取模块中绑定的值
        System.out.println(appContext.getInstance(Integer.class));
        System.out.println(appContext.getInstance(String.class));
    }
}