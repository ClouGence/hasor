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
package net.hasor.tconsole.launcher;
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.tconsole.commands.GetSetExecutor;
import net.hasor.tconsole.commands.HelpExecutor;
import net.hasor.tconsole.commands.QuitExecutor;
import net.hasor.test.beans.TestExecutor;
import org.junit.Test;

public class TelServerTest extends AbstractTelTest {
    @Test
    public void allowTest_1() throws Exception {
        //
        TelConsoleServer server = new TelConsoleServer("127.0.0.1", 8082, s -> true);
        server.addCommand("get", new GetSetExecutor());
        server.addCommand("set", new GetSetExecutor());
        server.addCommand("help", new HelpExecutor());
        server.addCommand("quit", new QuitExecutor());
        server.addCommand("test", new TestExecutor());
        //
        server.init();
        server.close();
        //Hasor.create().asCore().build().join(50, TimeUnit.SECONDS);
    }
}