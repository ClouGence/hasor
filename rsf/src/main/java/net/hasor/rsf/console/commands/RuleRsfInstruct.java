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
import net.hasor.rsf.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
/**
 * 路由脚本的查看和更新指令。
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCommand("rule")
public class RuleRsfInstruct implements RsfInstruct {
    //
    @Override
    public String helpInfo() {
        return "service rule script show/update.\r\n"//
                + " - rule              (show help info.)\r\n"// 
                + " - rule -s XXXX      (show service Level rule info of XXXX.)\r\n"//
                + " - rule -m XXXX      (show method Level rule info of XXXX.)\r\n"//
                + " - rule -a XXXX      (show args Level rule info of XXXX.)\r\n"//
                //
                + " - rule -us XXXX     (update service Level rule info of XXXX.)\r\n"//
                + " - rule -um XXXX     (update method Level rule info of XXXX.)\r\n"//
                + " - rule -ua XXXX     (update args Level rule info of XXXX.)\r\n"//
                //
                + " - rule -cs XXXX     (clean service Level rule info of XXXX.)\r\n"//
                + " - rule -cm XXXX     (clean method Level rule info of XXXX.)\r\n"//
                + " - rule -ca XXXX     (clean args Level rule info of XXXX.)";
    }
    @Override
    public boolean inputMultiLine(RsfCommandRequest request) {
        String[] args = request.getRequestArgs();
        if (args != null && args.length > 0) {
            String mode = args[0];
            return mode.startsWith("-u");
        }
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        RsfContext rsfContext = request.getRsfContext();
        StringWriter sw = new StringWriter();
        String[] args = request.getRequestArgs();
        if (args != null && args.length > 1) {
            String mode = args[0];
            String nameArg = args[1];
            /*如果是 -u 系列指令检查一下内容是不是为空。*/
            if (mode != null && mode.startsWith("-u")) {
                String scriptBody = request.getRequestBody();
                if (StringUtils.isBlank(scriptBody)) {
                    return "[ERROR] updated content is empty, ignore.";
                }
            }
            /*执行指令*/
            if ("-s".equalsIgnoreCase(mode)) {
                this.showServiceRule(sw, nameArg, rsfContext);  //显示服务级路由脚本
            } else if ("-m".equalsIgnoreCase(mode)) {
                this.showMethodRule(sw, nameArg, rsfContext);   //显示方法级路由脚本
            } else if ("-a".equalsIgnoreCase(mode)) {
                this.showArgsRule(sw, nameArg, rsfContext);     //显示参数级路由脚本
            } else if ("-us".equalsIgnoreCase(mode)) {
                this.updateServiceRule(sw, nameArg, request);   //更新服务级路由脚本
            } else if ("-um".equalsIgnoreCase(mode)) {
                this.updateMethodRule(sw, nameArg, request);    //更新方法级路由脚本
            } else if ("-ua".equalsIgnoreCase(mode)) {
                this.updateArgsRule(sw, nameArg, request);      //更新参数级路由脚本
            } else if ("-cs".equalsIgnoreCase(mode)) {
                this.cleanServiceRule(sw, nameArg, request);    //清空服务级路由脚本
            } else if ("-cm".equalsIgnoreCase(mode)) {
                this.cleanMethodRule(sw, nameArg, request);     //清空方法级路由脚本
            } else if ("-ca".equalsIgnoreCase(mode)) {
                this.cleanArgsRule(sw, nameArg, request);       //清空参数级别路由脚本
            } else {
                sw.write("[ERROR] bad args.");
            }
            //
        } else {
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  rule  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
        }
        return sw.toString();
    }
    //
    //
    private void showArgsRule(StringWriter sw, String nameArg, RsfContext rsfContext) throws IOException {
        //1.body
        String body = rsfContext.getUpdater().argsRoute(nameArg);
        //2.write
        if (StringUtils.isBlank(body)) {
            sw.write("[SUCCEED] content is empty.");
        } else {
            writeBody(sw, body);
        }
    }
    private void showMethodRule(StringWriter sw, String nameArg, RsfContext rsfContext) throws IOException {
        //1.body
        String body = rsfContext.getUpdater().methodRoute(nameArg);
        //2.write
        if (StringUtils.isBlank(body)) {
            sw.write("[SUCCEED] content is empty.");
        } else {
            writeBody(sw, body);
        }
    }
    private void showServiceRule(StringWriter sw, String nameArg, RsfContext rsfContext) throws IOException {
        //1.body
        String body = rsfContext.getUpdater().serviceRoute(nameArg);
        //2.write
        if (StringUtils.isBlank(body)) {
            sw.write("[SUCCEED] content is empty.");
        } else {
            writeBody(sw, body);
        }
    }
    private void writeBody(StringWriter sw, String body) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(body));
        for (; ; ) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            sw.write(line + "\r\n");
        }
    }
    //
    private void updateArgsRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        String scriptBody = request.getRequestBody();
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
            return;
        } else {
            boolean result = rsfContext.getUpdater().updateArgsRoute(nameArg, scriptBody);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] update ArgsLevel route script of serviceID = " + nameArg);
            return;
        }
    }
    private void updateMethodRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        String scriptBody = request.getRequestBody();
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
            return;
        } else {
            boolean result = rsfContext.getUpdater().updateMethodRoute(nameArg, scriptBody);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] update MethodLevel route script of serviceID = " + nameArg);
            return;
        }
    }
    private void updateServiceRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        String scriptBody = request.getRequestBody();
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
            return;
        } else {
            boolean result = rsfContext.getUpdater().updateServiceRoute(nameArg, scriptBody);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] update ServiceLevel route script of serviceID = " + nameArg);
            return;
        }
    }
    //
    private void cleanArgsRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
            return;
        } else {
            boolean result = rsfContext.getUpdater().updateArgsRoute(nameArg, null);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] clean ArgsLevel route script of serviceID = " + nameArg);
            return;
        }
    }
    private void cleanMethodRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
            return;
        } else {
            boolean result = rsfContext.getUpdater().updateMethodRoute(nameArg, null);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] clean MethodLevel route script of serviceID = " + nameArg);
            return;
        }
    }
    private void cleanServiceRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        if (rsfContext.getServiceInfo(nameArg) == null) {
            sw.write("[ERROR] serviceID is not exist.");
            return;
        } else {
            boolean result = rsfContext.getUpdater().updateServiceRoute(nameArg, null);
            sw.write("[" + (result ? "SUCCEED" : "FAILED") + "] clean ArgsLevel route script of serviceID = " + nameArg);
            return;
        }
    }
}