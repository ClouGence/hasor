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
package net.test.simple.core._06_dependency;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.StandardAppContext;
import net.test.simple.core._06_dependency.reverse.ReverseMode1;
import net.test.simple.core._06_dependency.reverse.ReverseMode2;
import net.test.simple.core._06_dependency.reverse.ReverseMode3;
import org.junit.Test;
/**
 * 本示列演示如何使用 Hasor的 Module依赖。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class ReverseDependencyTest {
    /*测试依赖反转*/
    @Test
    public void reverseDependencyTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>reverseDependencyTest<<--");
        StandardAppContext appContext = new StandardAppContext();
        appContext.addModule(new ReverseMode1());
        appContext.addModule(new ReverseMode2());
        appContext.addModule(new ReverseMode3());
        //
        appContext.start();
    }
}