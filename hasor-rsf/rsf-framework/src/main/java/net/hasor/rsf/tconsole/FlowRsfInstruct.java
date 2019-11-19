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
package net.hasor.rsf.tconsole;
import net.hasor.rsf.RsfContext;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;
import net.hasor.tconsole.TelReader;
import net.hasor.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 流量控制规则查看和更新指令。
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class FlowRsfInstruct implements TelExecutor {
    @Inject
    private RsfContext rsfContext;

    @Override
    public String helpInfo() {
        return "service flowControl show/update.\r\n"//
                + " - flow             (show help info.)\r\n"// 
                + " - rule -s XXXX     (show service flowControl info of XXXX.)\r\n"//
                + " - flow -u XXXX     (update service flowControl info of XXXX.)\r\n"//
                + " - flow -c XXXX     (clean service flowControl info of XXXX.)";
    }

    @Override
    public boolean readCommand(TelCommand telCommand, TelReader telReader) {
        String[] args = telCommand.getCommandArgs();
        if (args != null && args.length > 0) {
            if (args[0].startsWith("-u")) {
                return telReader.expectDoubleBlankLines();
            }
        }
        return true;
    }

    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        StringWriter sw = new StringWriter();
        String[] args = telCommand.getCommandArgs();
        if (args != null && args.length > 1) {
            String mode = args[0];
            String nameArg = args[1];
            /*如果是 -u 系列指令检查一下内容是不是为空。*/
            if (mode != null && mode.startsWith("-u")) {
                String scriptBody = telCommand.getCommandBody();
                if (StringUtils.isBlank(scriptBody)) {
                    return "[ERROR] updated content is empty, ignore.";
                }
            }
            /*执行指令*/
            /*  */
            if ("-s".equalsIgnoreCase(mode)) {
                this.showFlowControl(sw, nameArg, rsfContext);//显示流控规则
            } else if ("-u".equalsIgnoreCase(mode)) {
                this.updateFlowControl(sw, nameArg, telCommand, rsfContext);//更新流控规则
            } else if ("-c".equalsIgnoreCase(mode)) {
                this.cleanFlowControl(sw, nameArg, telCommand, rsfContext);//清空流控规则
            } else {
                sw.write("[ERROR] bad args.");
            }
            //
        } else {
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  flow  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
        }
        return sw.toString();
    }

    private void showFlowControl(StringWriter sw, String nameArg, RsfContext rsfContext) throws IOException {
        //1.body
        String body = rsfContext.getUpdater().flowControl(nameArg);
        //2.write
        if (StringUtils.isBlank(body)) {
            sw.write("[SUCCEED] content is empty.");
        } else {
            BufferedReader reader = new BufferedReader(new StringReader(body));
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sw.write(line + "\r\n");
            }
        }
    }

    private void updateFlowControl(StringWriter sw, String nameArg, TelCommand telCommand, RsfContext rsfContext) {
        String scriptBody = telCommand.getCommandBody();
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
        } else {
            boolean result = rsfContext.getUpdater().updateFlowControl(nameArg, scriptBody);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] update FlowControl of serviceID = " + nameArg);
        }
    }

    private void cleanFlowControl(StringWriter sw, String nameArg, TelCommand telCommand, RsfContext rsfContext) {
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
        } else {
            boolean result = rsfContext.getUpdater().updateFlowControl(nameArg, null);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] clean FlowControl of serviceID = " + nameArg);
        }
    }
}