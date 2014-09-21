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
package net.test.simple.core._09_variables;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import org.junit.Test;
/**
 * 读取环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class SystemVariablesTest {
    @Test
    public void systemVariablesTest() throws Exception {
        System.out.println("--->>systemVariablesTest<<--");
        //
        AppContext appContext = Hasor.createAppContext();
        Environment env = appContext.getEnvironment();
        //
        //设置属性
        System.setProperty("MyVar", "hello");
        env.refreshVariables();/*刷新变量列表*/
        System.out.println(env.getEnvVar("MyVar"));
        System.out.println(env.evalString("i say %MyVar%."));
    }
}