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
package net.test.simple.core._05_modules;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import net.hasor.core.Hasor;
import net.hasor.core.context.StandardAppContext;
import net.test.simple.core._05_modules.mods.Mod_1;
import net.test.simple.core._05_modules.mods.Mod_2;
import net.test.simple.core._05_modules.mods.Mod_3;
import org.junit.Test;
/**
 * Hasor 添加多个模块的演示程序
 * @version : 2014-1-10
 * @author 赵永春(zyc@hasor.net)
 */
public class ModuleTest {
    @Test
    public void moduleTest() throws IOException, URISyntaxException {
        System.out.println("--->>moduleTest<<--");
        StandardAppContext appContext = new StandardAppContext();
        appContext.addModule(new Mod_1());//模块1
        appContext.addModule(new Mod_2());//模块2
        appContext.addModule(new Mod_3());//模块3
        //
        appContext.start();
        //
        List<String> says = appContext.findBindingBean(String.class);
        Hasor.logInfo("all modules say:%s.", says);
    }
}