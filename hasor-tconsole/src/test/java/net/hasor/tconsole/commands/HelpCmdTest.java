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
package net.hasor.tconsole.commands;
import net.hasor.tconsole.client.TelClient;
import net.hasor.tconsole.launcher.telnet.TelnetTelService;
import net.hasor.test.tconsole.TestExecutor;
import org.junit.Test;

import java.net.InetSocketAddress;

public class HelpCmdTest {
    @Test
    public void helpTest_1() throws Exception {
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            //
            String help = client.sendCommand("help");
            assert help.contains("- exit  out of console.");
            assert help.contains("- set   set/get environment variables of console.");
            assert help.contains("- test  hello help.");
            //
            client.close();
            assert !client.isInit();
        }
    }

    //
    @Test
    public void helpTest_2() throws Exception {
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            //
            String help1 = client.sendCommand("help test");
            assert help1.contains("hello help.");
            //
            String help2 = client.sendCommand("help help");
            assert help2.contains("help quit  (show the 'quit' command help info.)");
            //
            String help3 = client.sendCommand("help abc");
            assert help3.contains("[ERROR] command 'abc' does not exist.");
        }
    }
}