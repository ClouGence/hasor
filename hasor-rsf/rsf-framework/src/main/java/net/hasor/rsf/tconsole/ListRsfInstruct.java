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
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;
import net.hasor.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.StringWriter;
import java.util.List;

/**
 * 列出服务列表
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class ListRsfInstruct implements TelExecutor {
    @Inject
    private RsfContext rsfContext;

    @Override
    public String helpInfo() {
        return "show service list.\r\n"//
                + " - list      (show all service id list.)\r\n"// 
                + " - list -h   (show help info.)\r\n";
    }

    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        StringWriter sw = new StringWriter();
        String[] args = telCommand.getCommandArgs();
        if (args != null && args.length > 0) {
            if ("-h".equalsIgnoreCase(args[0])) {
                sw.write(helpInfo());
                return sw.toString();
            }
        }
        //
        sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  list  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
        List<String> serviceList = rsfContext.getServiceIDs();
        int maxLength = 0;
        for (String serviceID : serviceList) {
            maxLength = (maxLength < serviceID.length()) ? serviceID.length() : maxLength;
        }
        for (String serviceID : serviceList) {
            RsfBindInfo<?> info = rsfContext.getServiceInfo(serviceID);
            boolean isProvider = rsfContext.getServiceProvider(info) != null;
            String itemStr = StringUtils.rightPad(serviceID, maxLength, " ") + "  -> " + ((isProvider) ? "Provider" : "Consumer");
            sw.write(">> " + itemStr + "\r\n");
        }
        //
        return sw.toString();
    }
}