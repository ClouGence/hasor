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
import net.hasor.tconsole.AbstractTelTest;
import net.hasor.tconsole.client.TelClient;
import net.hasor.tconsole.launcher.telnet.TelnetTelService;
import net.hasor.test.tconsole.TestExecutor;
import org.junit.Test;

import java.net.InetSocketAddress;

public class GetSetCmdTest extends AbstractTelTest {
    @Test
    public void getsetTest_1() throws Exception {
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            //
            server.init();
            //
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            //
            String vat_a = client.sendCommand("get a");
            assert vat_a.equals("");
            client.sendCommand("set a=asd");
            //
            vat_a = client.sendCommand("get a");
            assert vat_a.equals("asd");
        }
    }

    @Test
    public void getsetTest_2() throws Exception {
        try (TelnetTelService server = new TelnetTelService("127.0.0.1", 8082, s -> true)) {
            server.addCommand("test", new TestExecutor());
            server.init();
            TelClient client = new TelClient(new InetSocketAddress("127.0.0.1", 8082));
            client.init();
            //
            String setResult = client.sendCommand("set a");
            assert setResult.contains("java.lang.Exception: args count error.");
            //
            setResult = client.sendCommand("set");
            assert setResult.contains("var name undefined.");
            //
            setResult = client.sendCommand("set a=asd");
            assert setResult.equals("");
            setResult = client.sendCommand("get a");
            assert setResult.equals("asd");
        }
    }
}