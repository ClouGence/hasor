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
import java.io.StringWriter;
import org.more.util.StringUtils;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.console.RsfCmd;
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
/**
 * 
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCmd("switch")
public class SwitchRsfCommand implements RsfCommand {
    //
    @Override
    public String helpInfo() {
        return "switching application service online/offline.\r\n"//
                + " - switch          (show help info.)\r\n"// 
                + " - switch info     (show application online status.)\r\n"//
                + " - switch online   (online application , publishing/subscription to registry.)\r\n"//
                + " - switch offline  (offline application , remove publishing/subscription form registry.)";
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
            String todoArg = args[0];
            if (StringUtils.equalsIgnoreCase("online", todoArg) == true) {
                rsfContext.online();
                sw.write("switch to online : " + rsfContext.isOnline());
            } else if (StringUtils.equalsIgnoreCase("offline", todoArg) == true) {
                rsfContext.offline();
                sw.write("switch to offline : " + rsfContext.isOnline());
            } else if (StringUtils.equalsIgnoreCase("info", todoArg) == true) {
                String status = rsfContext.isOnline() ? "online" : "offline";
                sw.write("application is " + status);
            } else {
                sw.write("bad args '" + todoArg + "' switch command need 'online' or 'offline'.");
            }
            //
        } else {
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  switch  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
            //
        }
        return sw.toString();
    }
}