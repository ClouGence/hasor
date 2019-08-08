/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.tconsole;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.tconsole.client.TelnetClient;
import net.hasor.test.beans.HelloWordExecutor;
import org.junit.Test;

import java.net.InetSocketAddress;

//
public class LaunchTest {
    @Test
    public void baseicDefineTest() throws Throwable {
        //
        AppContext context = Hasor.create()      //
                .asCore()                        //
                .mainSettingWith("/META-INF/hasor-framework/tconsole-hconfig.xml")//
                .addVariable("TCONSOLE_PORT", "2222")//
                .build((Module) apiBinder -> {      //
                    apiBinder.tryCast(ConsoleApiBinder.class).addCommand("test").to(HelloWordExecutor.class);
                });
        //
        String command = TelnetClient.executeCommand(new InetSocketAddress("127.0.0.1", 2222), "test");
        assert command.contains("you say -> hello");
        context.shutdown();
    }
    //    public static void main() throws Throwable {
    //        //Server
    //        AppContext appContext = Hasor.create().build((Module) apiBinder -> {
    //            apiBinder.tryCast(ConsoleApiBinder.class).addCommand(new String[] { "hello" }, HelloWordExecutor.class);
    //        });
    //        //
    //        String name = ManagementFactory.getRuntimeMXBean().getName();
    //        String pid = name.split("@")[0];
    //        System.out.println("Pid is:" + pid);
    //        //
    //        System.out.println("server started. join wait signal.");
    //        appContext.joinSignal();
    //        System.out.println("server do shutdown.");
    //        appContext.shutdown();
    //    }
}