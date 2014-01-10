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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.AppContext;
import net.hasor.core.context.FileAppContext;
import org.junit.Test;
/**
 * Hasor 内核启动测试
 * @version : 2014-1-10
 * @author 赵永春(zyc@hasor.net)
 */
public class FileKernel_Test {
    @Test
    public void testFileKernel() throws IOException, URISyntaxException {
        //Hasor 仅加载一个配置文件的容器
        //---该容器不具备解析 @AnnoModule 注解功能，也不会加载“hasor-config.xml”、“static-config.xml”配置文件。
        //---不同于 SimpleAppContext 的是 FileAppContext 通过 File 的形式传入需要加载的配置文件。
        File configFile = new File("src/main/resources/net/test/simple/_05_kernel/hasor-config.xml");
        AppContext kernel = new FileAppContext(configFile);
        kernel.start();
    }
}