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
import net.hasor.core.Singleton;
import net.hasor.tconsole.CommandExecutor;
import net.hasor.tconsole.launcher.CmdRequest;
/**
 *
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class QuitExecutor implements CommandExecutor {
    @Override
    public String helpInfo() {
        return "out of console.";
    }
    @Override
    public boolean inputMultiLine(CmdRequest request) {
        return false;
    }
    @Override
    public String doCommand(CmdRequest request) throws Throwable {
        request.closeSession();
        return "logout of console";
    }
}