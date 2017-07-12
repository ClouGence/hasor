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
package net.hasor.registry.commands;
import net.hasor.core.Singleton;
import net.hasor.registry.RegistryConstants;
import net.hasor.registry.RsfCenterRegister;
import net.hasor.registry.RsfCenterResult;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
import net.hasor.rsf.console.RsfInstruct;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.rsf.utils.StringUtils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * 请求center重新推送地址。
 * @version : 2016年4月3日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@RsfCommand({ "pull", "request" })
public class PullRsfInstruct implements RsfInstruct {
    //
    @Override
    public String helpInfo() {
        return "pull or request service address list form center.\r\n"//
                + " - pull or request       (show pull help info.)\r\n"// 
                + " - pull or request -all  (pull all services addressSet.)\r\n" //
                + " - pull or request XXXX  (pull service addressSet of XXXX.)\r\n";
    }
    @Override
    public boolean inputMultiLine(RsfCommandRequest request) {
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        StringWriter sw = new StringWriter();
        String[] args = request.getRequestArgs();
        if (args != null && args.length > 0) {
            //
            // .准备参数
            String doArg = args[0];
            RsfContext rsfContext = request.getRsfContext();
            List<String> servicesList = Collections.emptyList();
            //
            // .确定拉取地址的服务列表
            if ("-all".equalsIgnoreCase(doArg)) {
                //
                request.writeMessageLine("detail Message:");
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
                    servicesList = Arrays.asList(info.getBindID());
                }
            }
            //
            // .地址拉取
            RsfCenterRegister register = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
            if (servicesList == null || servicesList.isEmpty()) {
                return "[FAILED] no service on this application is registered.";
            }
            for (String serviceID : servicesList) {
                request.writeMessageLine(" ->");
                request.writeMessageLine(" ->ServiceID : " + serviceID);
                //
                RsfBindInfo<Object> info = rsfContext.getServiceInfo(serviceID);
                if (info == null) {
                    request.writeMessageLine(" ->  [IGNORE] service is Undefined.");
                    continue;
                }
                if (info.getServiceType() == RsfServiceType.Provider) {
                    request.writeMessageLine(" ->  [IGNORE] service is Provider.");
                    continue;
                }
                String registerID = (String) info.getMetaData(RegistryConstants.Center_Ticket);
                if ("request".equalsIgnoreCase(request.getCommandString())) {
                    // -request
                    processRequest(request, register, serviceID, registerID);
                } else {
                    // -pull
                    processPull(request, register, serviceID, registerID);
                }
            }
        } else {
            //
            sw.write(">>>>>>>>>>>>>>>>>>>>>>>>  " + request.getCommandString() + "  <<<<<<<<<<<<<<<<<<<<<<<<\r\n");
            sw.write(helpInfo());
        }
        return sw.toString();
    }
    //
    //
    private void processPull(RsfCommandRequest request, RsfCenterRegister register, String serviceID, String registerID) {
        if (StringUtils.isBlank(registerID)) {
            request.writeMessageLine(" ->  [FAILED] the service has not yet registered to the center.");
            return;
        }
        // .1of4
        String protocol = request.getRsfContext().getDefaultProtocol();
        request.writeMessageLine(" ->  this machine is the default protocol is " + protocol);
        request.writeMessageLine(" ->  (1of4) pull address form rsfCenter ...");
        RsfCenterResult<List<String>> result = register.pullProviders(registerID, serviceID, protocol);
        if (result == null || !result.isSuccess() || result.getResult() == null) {
            String failedInfo = (result == null || result.getResult() == null) ?//
                    "EmptyResult." ://
                    "MESSAGE[" + result.getMessageID() + "] - (" + result.getErrorCode() + ")" + result.getErrorMessage();
            request.writeMessageLine(" ->  (4of4) [FAILED] " + failedInfo);
            return;
        }
        // .2of4
        List<String> addressSet = result.getResult();
        List<InterAddress> finalAddressList = new ArrayList<InterAddress>();
        List<String> finalAddressStrList = new ArrayList<String>();
        for (String address : addressSet) {
            try {
                InterAddress inter = new InterAddress(address);
                finalAddressList.add(inter);
                finalAddressStrList.add(inter.toHostSchema());
            } catch (Exception e) { /**/ }
        }
        request.writeMessageLine(String.format(" ->  (2of4) pull addressSet is " + StringUtils.join(finalAddressStrList.toArray(), ", ")));
        // .3of4
        request.writeMessageLine(String.format(" ->  (3of4) prepare refreshAddress addressSet."));
        // .4of4
        request.getRsfContext().getUpdater().refreshAddress(serviceID, finalAddressList);
        request.writeMessageLine(String.format(" ->  (4of4) done."));
    }
    private void processRequest(RsfCommandRequest request, RsfCenterRegister register, String serviceID, String registerID) {
        if (StringUtils.isBlank(registerID)) {
            request.writeMessageLine(" ->  [FAILED] the service has not yet registered to the center.");
            return;
        }
        RsfContext rsfContext = request.getRsfContext();
        String protocol = rsfContext.getDefaultProtocol();
        InterAddress callBackAddress = rsfContext.publishAddress(protocol);
        String callBackTo = callBackAddress.toHostSchema();
        //
        // .1of2
        request.writeMessageLine(" ->  this machine is the default protocol is " + protocol);
        request.writeMessageLine(" ->  (1of2) request data form rsfCenter ,callBack is " + callBackTo);
        RsfCenterResult<Boolean> result = register.requestPushProviders(registerID, serviceID, protocol, callBackTo);
        if (result == null || !result.isSuccess() || result.getResult() == null) {
            String failedInfo = (result == null || result.getResult() == null) ?//
                    "EmptyResult." ://
                    "MESSAGE[" + result.getMessageID() + "] - (" + result.getErrorCode() + ")" + result.getErrorMessage();
            request.writeMessageLine(" ->  (2of2) [FAILED] " + failedInfo);
            return;
        }
        // .2of2
        boolean requestResult = result.getResult();
        String mark = requestResult ? "SUCCEED" : "FAILED";
        request.writeMessageLine(String.format(" ->  (2of2) [%s] results is %s.", mark, mark.toLowerCase()));
    }
}