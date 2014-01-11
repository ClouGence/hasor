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
package net.test.simple._09_kernel;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.context.FileAppContext;
import net.hasor.core.plugin.PluginHelper;
import net.hasor.plugins.aop.AopPlugin;
import net.hasor.plugins.bean.BeanPlugin;
import net.hasor.plugins.event.ListenerPlugin;
import net.test.simple._09_kernel.mods.Mod_1;
import net.test.simple._09_kernel.mods.Mod_2;
import net.test.simple._09_kernel.mods.Mod_3;
import org.junit.Test;
/**
 * Hasor 内核启动测试
 * @version : 2014-1-10
 * @author 赵永春(zyc@hasor.net)
 */
public class FileKernel_Test {
    private static String config = "src/main/resources/net/test/simple/_09_kernel/hasor-config.xml";
    @Test
    public void test_kernel() throws IOException, URISyntaxException {
        System.out.println("--->>test_kernel<<--");
        //Hasor 仅加载一个配置文件的容器
        //---该容器不具备解析 @AnnoModule 注解功能，也不会加载“hasor-config.xml”、“static-config.xml”配置文件。
        //---不同于 SimpleAppContext 的是 FileAppContext 通过 File 的形式传入需要加载的配置文件。
        File configFile = new File(config);
        AppContext kernel = new FileAppContext(configFile);
        kernel.start();
    }
    /*2.添加模块（由于微内核不支持 @AnnoModule 注解因此模块的添加需要通过编码）*/
    @Test
    public void test_module() {
        System.out.println("--->>test_module<<--");
        File configFile = new File(config);
        FileAppContext kernel = new FileAppContext(configFile);
        kernel.addModule(new Mod_1());//模块1
        kernel.addModule(new Mod_2());//模块2
        kernel.addModule(new Mod_3());//模块3
        //
        kernel.start();
    }
    /*3.手动添加插件（由于微内核不支持 @Plugin 注解因此添加需要通过编码）*/
    @Test
    public void test_plugins() throws InstantiationException, IllegalAccessException {
        System.out.println("--->>test_plugins<<--");
        File configFile = new File(config);
        FileAppContext kernel = new FileAppContext(configFile);
        kernel.addModule(PluginHelper.toModule(BeanPlugin.class));//@Bean 插件
        kernel.addModule(PluginHelper.toModule(ListenerPlugin.class));//@Listener 插件
        kernel.addModule(PluginHelper.toModule(AopPlugin.class));//@Aop @GlobalAop 插件
        //
        kernel.start();
        // 
        Set<Class<?>> modSet = kernel.findClass(Module.class);
        Hasor.logInfo("mod set %s.", modSet);
    }
}