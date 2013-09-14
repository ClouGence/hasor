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
package org.hasor.test.simple.mod;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.AnnoAppContext;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class Mod_Test {
    @Test
    public void testDep1() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testDep1<<--");
        /*
         * Mode1
         *   Mode2
         *     Mode4
         *       Mode5
         *   Mode3
         *     Mode4
         *       Mode5
         *     Mode6
         *       Mode7
         *       Mode8
         *         Mode1 **
         *   Mode9
         */
        AnnoAppContext appContext = new AnnoAppContext("org/hasor/test/core/mod/dependency/dep1-config.xml");
        //
        appContext.start();
    }
    @Test
    public void testDep2() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testDep2<<--");
        /*
         * Mode1      F
         *   Mode2    T
         *     Mode3  F
         *   Mode3    F
         */
        AnnoAppContext appContext = new AnnoAppContext("org/hasor/test/core/mod/dependency/dep2-config.xml");
        //
        appContext.start();
    }
    @Test
    public void testGuice() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testGuice<<--");
        AnnoAppContext appContext = new AnnoAppContext("org/hasor/test/simple/mod/guice/gucie-config.xml");
        //
        appContext.start();
    }
}