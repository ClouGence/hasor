///*
// * Copyright 2008-2009 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package net.hasor.registry.commands;
//import net.hasor.core.Singleton;
//import net.hasor.rsf.InterAddress;
//import net.hasor.rsf.RsfBindInfo;
//import net.hasor.rsf.RsfContext;
//import net.hasor.rsf.RsfSettings;
//import net.hasor.rsf.console.RsfCommand;
//import net.hasor.rsf.console.RsfCommandRequest;
//import net.hasor.rsf.console.RsfInstruct;
//import net.hasor.rsf.domain.RsfServiceType;
//import net.hasor.rsf.utils.StringUtils;
//
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
///**
// * RSF框架工作信息。
// * @version : 2016年4月3日
// * @author 赵永春(zyc@hasor.net)
// */
//@Singleton
//@RsfCommand("info")
//public class InfoRsfInstruct implements RsfInstruct {
//    //
//    @Override
//    public String helpInfo() {
//        return "show server info.\r\n"//
//                + " - info -h   (show help info.)\r\n"//
//                + " - info      (show server info.)";
//    }
//    @Override
//    public boolean inputMultiLine(RsfCommandRequest request) {
//        return false;
//    }
//    @Override
//    public String doCommand(RsfCommandRequest request) throws Throwable {
//        RsfContext rsfContext = request.getRsfContext();
//        StringWriter sw = new StringWriter();
//        String[] args = request.getRequestArgs();
//        if (args != null && args.length > 0) {
//            String doArg = args[0];
//            if ("-h".equalsIgnoreCase(doArg)) {
//                sw.write(helpInfo());
//                return sw.toString();
//            }
//        }
//        //
//        Map<String, InterAddress> bindAddressSet = rsfContext.getSettings().getBindAddressSet();
//        List<String> arrays = new ArrayList<String>(bindAddressSet.keySet());
//        Collections.sort(arrays);
//        RsfSettings settings = rsfContext.getSettings();
//        int providerCount = 0;
//        int customerCount = 0;
//        List<String> serviceIDs = rsfContext.getServiceIDs();
//        for (String sid : serviceIDs) {
//            RsfBindInfo<Object> serviceInfo = rsfContext.getServiceInfo(sid);
//            if (serviceInfo.getServiceType() == RsfServiceType.Provider) {
//                providerCount++;
//            } else if (serviceInfo.getServiceType() == RsfServiceType.Consumer) {
//                customerCount++;
//            }
//        }
//        StringBuffer centerList = new StringBuffer("");
//        InterAddress[] centerServerSet = settings.getCenterServerSet();
//        for (InterAddress inter : centerServerSet) {
//            if (inter == null)
//                continue;
//            if (centerList.length() > 0) {
//                centerList.append(" , ");
//            }
//            centerList.append(inter.toHostSchema());
//        }
//        centerList.insert(0, '[');
//        centerList.append(']');
//        //
//        //
//        sw.write(">>\r\n");
//        sw.write(">>----- Server Info ------\r\n");
//        sw.write(">>        bindAddress : [\r\n");
//        for (String key : arrays) {
//            InterAddress interAddress = bindAddressSet.get(key);
//            sw.write(">>            bindAddress :(" + key + ")" + interAddress.toHostSchema() + "\r\n");
//        }
//        sw.write(">> ]\r\n");
//        sw.write(">>           isOnline :" + rsfContext.isOnline() + "\r\n");
//        sw.write(">>    automaticOnline :" + settings.isAutomaticOnline() + "\r\n");
//        sw.write(">>           appKeyID :" + settings.getAppKeyID() + "\r\n");
//        sw.write(">>       appKeySecret :" + (StringUtils.isBlank(settings.getAppKeySecret()) ? "null" : "******") + "\r\n");
//        sw.write(">>      service Count :" + serviceIDs.size() + "\r\n");
//        sw.write(">>     provider Count :" + providerCount + "\r\n");
//        sw.write(">>     customer Count :" + customerCount + "\r\n");
//        sw.write(">>\r\n");
//        sw.write(">>----- Console Info ------\r\n");
//        sw.write(">>        consolePort :" + settings.getConsolePort() + "\r\n");
//        sw.write(">>     consoleInBound :" + StringUtils.join(settings.getConsoleInBoundAddress(), ", ") + "\r\n");
//        sw.write(">>\r\n");
//        sw.write(">>----- Center Info ------\r\n");
//        sw.write(">>             enable :" + settings.isEnableCenter() + "\r\n");
//        sw.write(">>         rsfTimeout :" + settings.getCenterRsfTimeout() + "\r\n");
//        sw.write(">>      heartbeatTime :" + settings.getCenterHeartbeatTime() + "\r\n");
//        sw.write(">>         centerList :" + centerList.toString() + "\r\n");
//        sw.write(">>\r\n");
//        sw.write(">>----- Default Info ------\r\n");
//        sw.write(">>            timeout :" + settings.getDefaultTimeout() + "\r\n");
//        sw.write(">>              group :" + settings.getDefaultGroup() + "\r\n");
//        sw.write(">>            version :" + settings.getDefaultVersion() + "\r\n");
//        sw.write(">>      serializeType :" + settings.getDefaultSerializeType() + "\r\n");
//        sw.write(">>\r\n");
//        sw.write(">>----- Network Settings ------\r\n");
//        sw.write(">>      networkWorker :" + settings.getNetworkWorker() + "\r\n");
//        sw.write(">>    networkListener :" + settings.getNetworkListener() + "\r\n");
//        sw.write(">>     connectTimeout :" + settings.getConnectTimeout() + "\r\n");
//        sw.write(">>\r\n");
//        sw.write(">>----- RPC Settings ------\r\n");
//        sw.write(">>       queueMaxSize :" + settings.getQueueMaxSize() + "\r\n");
//        sw.write(">>   queueMinPoolSize :" + settings.getQueueMinPoolSize() + "\r\n");
//        sw.write(">>   queueMaxPoolSize :" + settings.getQueueMaxPoolSize() + "\r\n");
//        sw.write(">> queueKeepAliveTime :" + settings.getQueueKeepAliveTime() + "\r\n");
//        sw.write(">>     requestTimeout :" + settings.getRequestTimeout() + "\r\n");
//        sw.write(">>     maximumRequest :" + settings.getMaximumRequest() + "\r\n");
//        sw.write(">>    sendLimitPolicy :" + settings.getSendLimitPolicy().name() + "\r\n");
//        sw.write(">>\r\n");
//        sw.write(">>----- Address Policy------\r\n");
//        sw.write(">>       invalidWaitTime :" + settings.getInvalidWaitTime() + "\r\n");
//        sw.write(">>      refreshCacheTime :" + settings.getRefreshCacheTime() + "\r\n");
//        sw.write(">>        localDiskCache :" + settings.islocalDiskCache() + "\r\n");
//        sw.write(">> diskCacheTimeInterval :" + settings.getDiskCacheTimeInterval() + "\r\n");
//        return sw.toString();
//    }
//}