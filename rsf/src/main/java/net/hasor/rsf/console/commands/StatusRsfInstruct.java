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
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
import net.hasor.rsf.console.RsfInstruct;

import java.io.StringWriter;
/**
 * RSF 服务框架状态查看和更新指令
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCommand("status")
public class StatusRsfInstruct implements RsfInstruct {
    //
    @Override
    public String helpInfo() {
        return "switching application service online/offline.\r\n"//
                + " - status      (show help info.)\r\n"// 
                + " - status info (show application online status.)\r\n"//
                + " - status on   (online application , publishing/subscription to registry.)\r\n"//
                + " - status off  (offline application , remove publishing/subscription form registry.)";
    }
    @Override
    public boolean inputMultiLine(RsfCommandRequest request) {
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        RsfContext rsfContext = request.getRsfContext();
        StringWriter sw = new StringWriter();
        String[] args = request.getRequestArgs();
        if (args != null && args.length > 0) {
            String doArg = args[0];
            if ("on".equalsIgnoreCase(doArg)) {
                rsfContext.online();
                sw.write("[SUCCEED] switch to online -> Current Status isOnline : " + rsfContext.isOnline());
            } else if ("off".equalsIgnoreCase(doArg)) {
                rsfContext.offline();
                sw.write("[SUCCEED] switch to offline -> Current Status isOnline : " + rsfContext.isOnline());
            } else if ("info".equalsIgnoreCase(doArg)) {
                String status = rsfContext.isOnline() ? "online" : "offline";
                sw.write("[SUCCEED] application is " + status);
            } else {
                sw.write("[ERROR] bad args '" + doArg + "' switch command need 'online' or 'offline'.");
            }
            //
        } else {
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  status  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
            //
        }
        return sw.toString();
    }
}