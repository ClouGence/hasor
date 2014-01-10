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
package net.test.simple._05_kernel;
import java.util.Set;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.context.SimpleAppContext;
import net.hasor.core.plugin.PluginHelper;
import net.hasor.plugins.aop.AopPlugin;
import net.hasor.plugins.bean.BeanPlugin;
import net.hasor.plugins.event.ListenerPlugin;
import net.test.simple._05_kernel.mods.Mod_1;
import net.test.simple._05_kernel.mods.Mod_2;
import net.test.simple._05_kernel.mods.Mod_3;
import org.junit.Test;
/**
 * 微内核相关的演示程序
 * @version : 2014-1-10
 * @author 赵永春(zyc@hasor.net)
 */
public class MiniKernel_Test {
    /*1.启动微内核*/
    @Test
    public void test_startKernel() {
        System.out.println("--->>test_startMiniKernel<<--");
        AppContext kernel = new SimpleAppContext();
        kernel.start();
        //
    }
    /*2.添加模块（由于微内核不支持 @AnnoModule 注解因此模块的添加需要通过编码）*/
    @Test
    public void test_module() {
        SimpleAppContext kernel = new SimpleAppContext();
        kernel.addModule(new Mod_1());//模块1
        kernel.addModule(new Mod_2());//模块2
        kernel.addModule(new Mod_3());//模块3
        //
        kernel.start();
    }
    /*3.手动添加插件（由于微内核不支持 @Plugin 注解因此添加需要通过编码）*/
    @Test
    public void test_plugins() throws InstantiationException, IllegalAccessException {
        SimpleAppContext kernel = new SimpleAppContext();
        kernel.addModule(PluginHelper.toModule(BeanPlugin.class));//@Bean 插件
        kernel.addModule(PluginHelper.toModule(ListenerPlugin.class));//@Listener 插件
        kernel.addModule(PluginHelper.toModule(AopPlugin.class));//@Aop @GlobalAop 插件
        //
        kernel.getEnvironment().setSpanPackage(new String[] { "net.test.simple._05_kernel.*" });
        //
        kernel.start();
        // 
        Set<Class<?>> modSet = kernel.findClass(Module.class);
        Hasor.logInfo("mod set %s.", modSet);
    }
}