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
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;

import java.io.StringWriter;
import java.util.*;

/**
 * RSF框架工作信息。
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class InfoRsfInstruct implements TelExecutor {
    @Inject
    private RsfContext rsfContext;

    @Override
    public String helpInfo() {
        return "show server info.\r\n"//
                + " - info -h   (show help info.)\r\n"//
                + " - info      (show server info.)";
    }

    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        StringWriter sw = new StringWriter();
        String[] args = telCommand.getCommandArgs();
        if (args != null && args.length > 0) {
            String doArg = args[0];
            if ("-h".equalsIgnoreCase(doArg)) {
                sw.write(helpInfo());
                return sw.toString();
            }
        }
        //
        Set<String> protocolSet = rsfContext.getSettings().getProtocos();
        Map<String, InterAddress> bindAddressSet = new HashMap<>();
        for (String protocol : protocolSet) {
            InterAddress interAddress = rsfContext.bindAddress(protocol);
            if (interAddress != null) {
                bindAddressSet.put(protocol, interAddress);
            }
        }
        List<String> arrays = new ArrayList<>(bindAddressSet.keySet());
        Collections.sort(arrays);
        RsfSettings settings = rsfContext.getSettings();
        int providerCount = 0;
        int customerCount = 0;
        List<String> serviceIDs = rsfContext.getServiceIDs();
        for (String sid : serviceIDs) {
            RsfBindInfo<Object> serviceInfo = rsfContext.getServiceInfo(sid);
            if (serviceInfo.getServiceType() == RsfServiceType.Provider) {
                providerCount++;
            } else if (serviceInfo.getServiceType() == RsfServiceType.Consumer) {
                customerCount++;
            }
        }
        //
        //
        sw.write(">>\r\n");
        sw.write(">>----- Server Info ------\r\n");
        sw.write(">>        bindAddress : [\r\n");
        for (String key : arrays) {
            InterAddress interAddress = bindAddressSet.get(key);
            sw.write(">>            bindAddress :(" + key + ")" + interAddress.toHostSchema() + "\r\n");
        }
        sw.write(">> ]\r\n");
        sw.write(">>           isOnline :" + rsfContext.isOnline() + "\r\n");
        sw.write(">>    automaticOnline :" + settings.isAutomaticOnline() + "\r\n");
        sw.write(">>      service Count :" + serviceIDs.size() + "\r\n");
        sw.write(">>     provider Count :" + providerCount + "\r\n");
        sw.write(">>     customer Count :" + customerCount + "\r\n");
        sw.write(">>\r\n");
        sw.write(">>----- Default Info ------\r\n");
        sw.write(">>            timeout :" + settings.getDefaultTimeout() + "\r\n");
        sw.write(">>              group :" + settings.getDefaultGroup() + "\r\n");
        sw.write(">>            version :" + settings.getDefaultVersion() + "\r\n");
        sw.write(">>      serializeType :" + settings.getDefaultSerializeType() + "\r\n");
        sw.write(">>\r\n");
        sw.write(">>----- Network Settings ------\r\n");
        sw.write(">>     connectTimeout :" + settings.getConnectTimeout() + "\r\n");
        sw.write(">>\r\n");
        sw.write(">>----- RPC Settings ------\r\n");
        sw.write(">>       queueMaxSize :" + settings.getQueueMaxSize() + "\r\n");
        sw.write(">>   queueMinPoolSize :" + settings.getQueueMinPoolSize() + "\r\n");
        sw.write(">>   queueMaxPoolSize :" + settings.getQueueMaxPoolSize() + "\r\n");
        sw.write(">> queueKeepAliveTime :" + settings.getQueueKeepAliveTime() + "\r\n");
        sw.write(">>     requestTimeout :" + settings.getRequestTimeout() + "\r\n");
        sw.write(">>     maximumRequest :" + settings.getMaximumRequest() + "\r\n");
        sw.write(">>    sendLimitPolicy :" + settings.getSendLimitPolicy().name() + "\r\n");
        sw.write(">>\r\n");
        sw.write(">>----- Address Policy------\r\n");
        sw.write(">>       invalidWaitTime :" + settings.getInvalidWaitTime() + "\r\n");
        sw.write(">>      refreshCacheTime :" + settings.getRefreshCacheTime() + "\r\n");
        sw.write(">>        localDiskCache :" + settings.islocalDiskCache() + "\r\n");
        sw.write(">> diskCacheTimeInterval :" + settings.getDiskCacheTimeInterval() + "\r\n");
        return sw.toString();
    }
}
