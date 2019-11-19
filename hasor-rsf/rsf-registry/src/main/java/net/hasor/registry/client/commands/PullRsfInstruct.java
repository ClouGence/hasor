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
package net.hasor.registry.client.commands;
import net.hasor.core.Singleton;
import net.hasor.registry.client.RsfCenterRegister;
import net.hasor.registry.client.RsfCenterResult;
import net.hasor.registry.client.domain.ServiceID;
import net.hasor.registry.common.InstanceInfo;
import net.hasor.registry.server.commonds.AbstractCenterInstruct;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.tconsole.TelCommand;
import net.hasor.utils.StringUtils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 请求center重新推送地址。
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class PullRsfInstruct extends AbstractCenterInstruct {
    //
    @Override
    public String helpInfo() {
        return "pull or request service address list form center.\r\n"//
                + " - pull or request       (show pull help info.)\r\n"// 
                + " - pull or request -all  (pull all services addressSet.)\r\n" //
                + " - pull or request XXXX  (pull service addressSet of XXXX.)\r\n";
    }

    @Override
    public String doCommand(InstanceInfo instance, TelCommand telCommand) throws Throwable {
        StringWriter sw = new StringWriter();
        String[] args = telCommand.getCommandArgs();
        if (args != null && args.length > 0) {
            //
            // .准备参数
            String doArg = args[0];
            RsfContext rsfContext = appContext.getInstance(RsfContext.class);
            List<String> servicesList = Collections.emptyList();
            //
            // .确定拉取地址的服务列表
            if ("-all".equalsIgnoreCase(doArg)) {
                //
                telCommand.writeMessageLine("detail Message:");
                servicesList = rsfContext.getServiceIDs();
            } else {
                //
                RsfBindInfo<Object> info = rsfContext.getServiceInfo(doArg);
                if (info == null) {
                    return "[ERROR] the service '" + doArg + "' is Undefined.";
                } else {
                    if (info.getServiceType() == RsfServiceType.Provider) {
                        return "[FAILED] the service '" + doArg + "' is Provider.";
                    }
                    servicesList = Collections.singletonList(info.getBindID());
                }
            }
            //
            // .地址拉取
            RsfCenterRegister register = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
            if (servicesList == null || servicesList.isEmpty()) {
                return "[FAILED] no service on this application is registered.";
            }
            for (String serviceID : servicesList) {
                telCommand.writeMessageLine(" ->");
                telCommand.writeMessageLine(" ->ServiceID : " + serviceID);
                //
                RsfBindInfo<?> info = rsfContext.getServiceInfo(serviceID);
                if (info == null) {
                    telCommand.writeMessageLine(" ->  [IGNORE] service is Undefined.");
                    continue;
                }
                if (info.getServiceType() == RsfServiceType.Provider) {
                    telCommand.writeMessageLine(" ->  [IGNORE] service is Provider.");
                    continue;
                }
                //
                if ("request".equalsIgnoreCase(telCommand.getCommandBody())) {
                    // -request
                    processRequest(telCommand, register, info, instance, rsfContext);
                } else {
                    // -pull
                    processPull(telCommand, register, info, instance, rsfContext);
                }
            }
        } else {
            //
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  " + telCommand.getCommandName() + "  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
        }
        return sw.toString();
    }

    //
    private void processPull(TelCommand telCommand, RsfCenterRegister register, RsfBindInfo<?> serviceInfo, InstanceInfo instance, RsfContext rsfContext) {
        // .1of4
        String protocol = rsfContext.getDefaultProtocol();
        telCommand.writeMessageLine(" ->  this machine is the default protocol is " + protocol);
        telCommand.writeMessageLine(" ->  (1of4) pull address form rsfCenter ...");
        List<String> runProtocol = new ArrayList<>(serviceInfo.getBindProtocols());
        RsfCenterResult<List<String>> result = register.pullProviders(instance, ServiceID.of(serviceInfo), runProtocol);
        if (result == null || !result.isSuccess() || result.getResult() == null) {
            String failedInfo = (result == null || result.getResult() == null) ?//
                    "EmptyResult." ://
                    "MESSAGE[" + result.getMessageID() + "] - (" + result.getErrorCode() + ")" + result.getErrorMessage();
            telCommand.writeMessageLine(" ->  (4of4) [FAILED] " + failedInfo);
            return;
        }
        // .2of4
        List<String> addressSet = result.getResult();
        List<InterAddress> finalAddressList = new ArrayList<>();
        List<String> finalAddressStrList = new ArrayList<>();
        for (String address : addressSet) {
            try {
                InterAddress inter = new InterAddress(address);
                finalAddressList.add(inter);
                finalAddressStrList.add(inter.toHostSchema());
            } catch (Exception e) { /**/ }
        }
        telCommand.writeMessageLine(" ->  (2of4) pull addressSet is " + StringUtils.join(finalAddressStrList.toArray(), ", "));
        // .3of4
        telCommand.writeMessageLine(" ->  (3of4) prepare refreshAddress addressSet.");
        // .4of4
        rsfContext.getUpdater().refreshAddress(serviceInfo.getBindID(), finalAddressList);
        telCommand.writeMessageLine(" ->  (4of4) done.");
    }

    private void processRequest(TelCommand telCommand, RsfCenterRegister register, RsfBindInfo<?> serviceInfo, InstanceInfo instance, RsfContext rsfContext) {
        // .1of2
        String protocol = rsfContext.getDefaultProtocol();
        InterAddress callBackAddress = rsfContext.bindAddress(protocol);
        String callBackTo = callBackAddress.toHostSchema();
        telCommand.writeMessageLine(" ->  this machine is the default protocol is " + protocol);
        telCommand.writeMessageLine(" ->  (1of2) request data form rsfCenter ,callBack is " + callBackTo);
        List<String> runProtocol = new ArrayList<String>(serviceInfo.getBindProtocols());
        RsfCenterResult<Boolean> result = register.requestPushProviders(instance, ServiceID.of(serviceInfo), runProtocol);
        if (result == null || !result.isSuccess() || result.getResult() == null) {
            String failedInfo = (result == null || result.getResult() == null) ?//
                    "EmptyResult." ://
                    "MESSAGE[" + result.getMessageID() + "] - (" + result.getErrorCode() + ")" + result.getErrorMessage();
            telCommand.writeMessageLine(" ->  (2of2) [FAILED] " + failedInfo);
            return;
        }
        // .2of2
        boolean requestResult = result.getResult();
        String mark = requestResult ? "SUCCEED" : "FAILED";
        telCommand.writeMessageLine(String.format(" ->  (2of2) [%s] results is %s.", mark, mark.toLowerCase()));
    }
}