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
package net.test.simple.context;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.core.context.SimpleAppContext;
import net.hasor.core.context.StandardAppContext;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultAppContext_Test {
    @Test
    public void testDefaultAppContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testDefaultAppContext<<--");
        SimpleAppContext appContext = new SimpleAppContext();
        //
        appContext.addModule(new AnnoTestMod_2());
        appContext.addModule(new AnnoTestMod_3());
        appContext.addModule(new AnnoTestMod_1());
        //
        appContext.start();
    }
    @Test
    public void testStandardAppContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testStandardAppContext<<--");
        StandardAppContext appContext = new StandardAppContext("org/hasor/test/simple/context/hasor-config.xml");
        //
        appContext.addModule(new AnnoTestMod_2());
        appContext.addModule(new AnnoTestMod_3());
        appContext.addModule(new AnnoTestMod_1());
        //
        appContext.start();
    }
    @Test
    public void testAnnoAppContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testAnnoAppContext<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("org/hasor/test/simple/context/hasor-config.xml");
        //
        appContext.start();
    }
}