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
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfUpdater;
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
import net.hasor.rsf.console.RsfInstruct;

import java.io.StringWriter;
import java.util.List;
/**
 * 查看服务详细信息
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCommand("detail")
public class DetailRsfInstruct implements RsfInstruct {
    //
    @Override
    public String helpInfo() {
        return "show service info.\r\n"//
                + " - detail         (show help info.)\r\n"//
                + " - detail -h      (show help info.)\r\n"//
                + " - detail xxxx    (show service info of XXXX.)\r\n"//
                + " - detail -a xxxx (show service info of XXXX. if service is Consumer then show Providers)";
    }
    @Override
    public boolean inputMultiLine(RsfCommandRequest request) {
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        StringWriter sw = new StringWriter();
        String[] args = request.getRequestArgs();
        // .help
        if (args == null || args.length == 0 || (args.length > 0 && "-h".equalsIgnoreCase(args[0]))) {
            //
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  " + request.getCommandString() + "  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
            return sw.toString();
        }
        //
        // .准备参数
        String serviceID = args[args.length - 1].trim();
        RsfContext rsfContext = request.getRsfContext();
        RsfBindInfo<Object> info = rsfContext.getServiceInfo(serviceID);
        boolean isProvider = rsfContext.getServiceProvider(info) != null;
        if (info == null) {
            return "[ERROR] the service '" + serviceID + "' is Undefined.";
        }
        //
        sw.write(">>\r\n");
        sw.write(">>----- Service Info ------\r\n");
        sw.write(">>         ID : " + info.getBindID() + "\r\n");
        sw.write(">>      Group : " + info.getBindGroup() + "\r\n");
        sw.write(">>       Name : " + info.getBindName() + "\r\n");
        sw.write(">>    Version : " + info.getBindVersion() + "\r\n");
        sw.write(">>    Timeout : " + info.getClientTimeout() + "\r\n");
        sw.write(">>  Serialize : " + info.getSerializeType() + "\r\n");
        sw.write(">>   javaType : " + info.getBindType().getName() + "\r\n");
        sw.write(">>    Message : " + info.isMessage() + "\r\n");
        sw.write(">>     Shadow : " + info.isShadow() + "\r\n");
        sw.write(">> SinglePool : " + info.isSharedThreadPool() + "\r\n");
        sw.write(">>       Type : " + ((isProvider) ? "Provider" : "Consumer") + "\r\n");
        //
        if (!isProvider && (args.length == 2 && "-a".equalsIgnoreCase(args[0]))) {
            RsfUpdater updater = rsfContext.getUpdater();
            List<InterAddress> allList = updater.queryAllAddresses(serviceID);
            List<InterAddress> availableList = updater.queryAvailableAddresses(serviceID);
            List<InterAddress> unitList = updater.queryLocalUnitAddresses(serviceID);
            sw.write(">>\r\n");
            sw.write(">>---- Subscribe Info -----\r\n");
            sw.write(">>       unit : " + evalAddress(unitList) + "\r\n");
            sw.write(">>  available : " + evalAddress(availableList) + "\r\n");
            sw.write(">>        all : " + evalAddress(allList) + "\r\n");
        }
        return sw.toString();
    }
    private static final String evalAddress(List<InterAddress> addressSet) {
        StringBuffer addrList = new StringBuffer("");
        for (InterAddress inter : addressSet) {
            if (inter == null)
                continue;
            if (addrList.length() > 0) {
                addrList.append(" , ");
            }
            addrList.append(inter.toHostSchema());
        }
        addrList.insert(0, '[');
        addrList.append(']');
        return addrList.toString();
    }
}