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
import net.hasor.tconsole.launcher.hosts.HostTelService;
import net.hasor.test.beans.TestExecutor;
import net.hasor.utils.StringUtils;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class HostsCommandTest {
    public static void main(String[] args) throws Exception {
        HostTelService server = new HostTelService( //
                new InputStreamReader(System.in),   //
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8)//
        );
        server.addCommand("test", new TestExecutor());
        //
        server.silent();
        server.init();
        server.sendMessage("exit -next");
        server.sendMessage(StringUtils.join(args, " "));
        if (server.isInit()) {
            server.close();
        }
    }
}