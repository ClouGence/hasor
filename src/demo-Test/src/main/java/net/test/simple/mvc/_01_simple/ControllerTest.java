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
package net.test.simple.mvc._01_simple;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.mvc.support.ControllerModule;
import net.hasor.mvc.support.RootController;
import org.junit.Test;
/**
 * 被测试的控制器
 * @version : 2014年8月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class ControllerTest {
    @Test
    public void controllerTest() throws Throwable {
        System.out.println("--->>controllerTest<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                /*绑定一个接口的实现类*/
                apiBinder.installModule(new ControllerModule());
            }
        });
        //
        RootController root = appContext.getInstance(RootController.class);
        //F
        root.findMapping("/oper/add").invoke();
        root.findMapping("/oper/del").invoke();
        //
    }
}