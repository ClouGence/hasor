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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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
@RsfCmd("rule")
public class RuleRsfCommand implements RsfCommand {
    //
    @Override
    public String helpInfo() {
        return "rule service rule info.\r\n"//
                + " - rule              (show help info.)\r\n"// 
                + " - rule -s default   (show default service Level rule info .)\r\n"//
                + " - rule -m default   (show default method Level rule info .)\r\n"//
                + " - rule -a default   (show default args Level rule info .)\r\n"//
                + " - rule -s XXXX      (show service Level rule info of XXXX.)\r\n"//
                + " - rule -m XXXX      (show method Level rule info of XXXX.)\r\n"//
                + " - rule -a XXXX      (show args Level rule info of XXXX.)\r\n"//
                //
                + " - rule -us default  (update default service Level rule info .)\r\n"//
                + " - rule -um default  (update default method Level rule info .)\r\n"//
                + " - rule -ua default  (update default args Level rule info .)\r\n"//
                + " - rule -us XXXX     (update service Level rule info of XXXX.)\r\n"//
                + " - rule -um XXXX     (update method Level rule info of XXXX.)\r\n"//
                + " - rule -ua XXXX     (update args Level rule info of XXXX.)";
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
            /*  */if ("-s".equalsIgnoreCase(mode)) {
                this.showServiceRule(sw, nameArg, rsfContext);
            } else if ("-m".equalsIgnoreCase(mode)) {
                this.showMethodRule(sw, nameArg, rsfContext);
            } else if ("-a".equalsIgnoreCase(mode)) {
                this.showArgsRule(sw, nameArg, rsfContext);
            } else if ("-us".equalsIgnoreCase(mode)) {
                this.updateServiceRule(sw, nameArg, request);
            } else if ("-um".equalsIgnoreCase(mode)) {
                this.updateMethodRule(sw, nameArg, request);
            } else if ("-ua".equalsIgnoreCase(mode)) {
                this.updateArgsRule(sw, nameArg, request);
            } else {
                sw.write("bad args.");
            }
            //
        } else {
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  rule  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
        }
        return sw.toString();
    }
    //
    private void showArgsRule(StringWriter sw, String nameArg, RsfContext rsfContext) throws IOException {
        //1.body
        String body = null;
        if ("default".equalsIgnoreCase(nameArg)) {
            body = rsfContext.getUpdater().defaultArgsRoute();
        } else {
            body = rsfContext.getUpdater().argsRoute(nameArg);
        }
        //2.write
        if (StringUtils.isBlank(body)) {
            sw.write(">> args rule of " + nameArg + " content is empty.");
        } else {
            writeBody(sw, body);
        }
    }
    private void showMethodRule(StringWriter sw, String nameArg, RsfContext rsfContext) throws IOException {
        //1.body
        String body = null;
        if ("default".equalsIgnoreCase(nameArg)) {
            body = rsfContext.getUpdater().defaultMethodRoute();
        } else {
            body = rsfContext.getUpdater().methodRoute(nameArg);
        }
        //2.write
        if (StringUtils.isBlank(body)) {
            sw.write(">> method rule of " + nameArg + " content is empty.");
        } else {
            writeBody(sw, body);
        }
    }
    private void showServiceRule(StringWriter sw, String nameArg, RsfContext rsfContext) throws IOException {
        //1.body
        String body = null;
        if ("default".equalsIgnoreCase(nameArg)) {
            body = rsfContext.getUpdater().defaultServiceRoute();
        } else {
            body = rsfContext.getUpdater().serviceRoute(nameArg);
        }
        //2.write
        if (StringUtils.isBlank(body)) {
            sw.write(">> service rule of " + nameArg + " content is empty.");
        } else {
            writeBody(sw, body);
        }
    }
    private void writeBody(StringWriter sw, String body) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(body));
        for (;;) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            sw.write(line + "\r\n");
        }
    }
    private void updateArgsRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        System.out.println();
        // TODO Auto-generated method stub
    }
    private void updateMethodRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        System.out.println();
        // TODO Auto-generated method stub
    }
    private void updateServiceRule(StringWriter sw, String nameArg, RsfCommandRequest request) {
        RsfContext rsfContext = request.getRsfContext();
        System.out.println();
        // TODO Auto-generated method stub
    }
}