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
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
import net.hasor.rsf.console.RsfInstruct;
import net.hasor.rsf.utils.StringUtils;
/**
 * 在本次Telnet中设置环境变量，当Telnet连接断开变量失效
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCommand({ "set", "get" })
public class GetSetRsfInstruct implements RsfInstruct {
    @Override
    public String helpInfo() {
        return "set/get environment variables of console .\r\n"//
                + " - get variableName                (returns variable Value.)\r\n"// 
                + " - set variableName variableValue  (set new values to variable.)";//
    }
    @Override
    public boolean inputMultiLine(RsfCommandRequest request) {
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        request.setAttr(RsfCommandRequest.WITHOUT_AFTER_CLOSE_SESSION, true);//不关闭Session
        String[] args = request.getRequestArgs();
        String argsJoin = StringUtils.join(args, "");
        argsJoin = argsJoin.replace("\\s+", " ");
        args = argsJoin.split("=");
        //
        if (args.length > 0) {
            String cmd = request.getCommandString();
            String varName = args[0].trim();
            //
            if ("set".equalsIgnoreCase(cmd)) {
                if (args.length > 1) {
                    String varValue = args[1].trim();
                    request.setSessionAttr(varName, varValue);
                    return "[SUCCEED] set the new value.";
                } else {
                    return "[ERROR] args count error.";
                }
            }
            if ("get".equalsIgnoreCase(cmd)) {
                Object obj = request.getSessionAttr(varName);
                if (obj == null) {
                    return "";
                } else {
                    return obj.toString();//TODO may be is object
                }
            }
            //
            return "[ERROR] does not support command '" + request.getCommandString() + "'.";
        } else {
            return "[ERROR] args count error.";
        }
    }
}