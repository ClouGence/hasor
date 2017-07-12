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
package net.hasor.rsf.console.commands;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.console.CommandManager;
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
import net.hasor.rsf.console.RsfInstruct;
import net.hasor.rsf.utils.StringUtils;

import java.io.StringWriter;
import java.util.List;
/**
 * 显示所有指令
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCommand("help")
public class HelpRsfInstruct implements RsfInstruct {
    //
    @Override
    public String helpInfo() {
        return "command help manual. the function like linux man.\r\n"//
                + " - help       (show all commands help info.)\r\n"// 
                + " - help -a    (show all commands help detail info.)\r\n"// 
                + " - help quit  (show the 'quit' command help info.)";
    }
    @Override
    public boolean inputMultiLine(RsfCommandRequest request) {
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        RsfContext rsfContext = request.getRsfContext();
        CommandManager commandManager = rsfContext.getAppContext().getInstance(CommandManager.class);
        List<String> cmdNames = commandManager.getCommandNames();
        StringWriter sw = new StringWriter();
        if (cmdNames == null || cmdNames.isEmpty()) {
            return "there is nothing  command to display information.";
        }
        //
        String[] args = request.getRequestArgs();
        boolean showDetail = false;
        if (args != null && args.length > 0) {
            String cmdName = args[0];
            if ("-a".equalsIgnoreCase(cmdName)) {
                showDetail = true;
            } else {
                RsfInstruct cmd = commandManager.findCommand(cmdName);
                if (cmd != null) {
                    sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  " + cmdName + "  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
                    sw.write(cmd.helpInfo() + "\r\n");
                } else {
                    sw.write("[ERROR] command '" + cmdName + "' does not exist.\r\n");
                }
                return sw.toString();
            }
        }
        int maxLength = 0;
        for (String name : cmdNames) {
            maxLength = name.length() > maxLength ? name.length() : maxLength;
        }
        maxLength = maxLength + 2;
        for (String name : cmdNames) {
            RsfInstruct cmd = commandManager.findCommand(name);
            if (showDetail) {
                sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  " + name + "  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
                sw.write(cmd.helpInfo() + "\r\n");
            } else {
                sw.write(" - " + StringUtils.rightPad(name, maxLength, " ") + cmd.helpInfo().split("\r\n")[0]);
            }
            if (cmdNames.size() > 1) {
                sw.write("\r\n");
            }
        }
        return sw.toString();
    }
}