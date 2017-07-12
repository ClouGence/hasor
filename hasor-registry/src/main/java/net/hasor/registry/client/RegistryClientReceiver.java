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
package net.hasor.registry.client;
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import net.hasor.core.Inject;
import net.hasor.registry.RegistryConstants;
import net.hasor.registry.RsfCenterListener;
import net.hasor.registry.domain.client.CenterEventBody;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfUpdater;
import net.hasor.rsf.domain.RsfCenterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 注册中心数据接收器，负责更新注册中心推送过来的配置信息。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RegistryClientReceiver implements RsfCenterListener {
    protected Logger logger = LoggerFactory.getLogger(RegistryConstants.LoggerName_CenterReceiver);
    @Inject
    private              RsfContext   rsfContext;
    @Inject
    private              EventContext eventContext;
    private static final String[]     checkServiceEventArrays;

    static {
        checkServiceEventArrays = new String[] { //
                "AppendAddressEvent", //
                "RefreshAddressEvent",//
                "RemoveAddressEvent", //
                "UpdateServiceRouteEvent", //
                "UpdateMethodRouteEvent", //
                "UpdateArgsRouteEvent", //
                "UpdateFlowControlEvent" };
    }

    @Override
    public boolean onEvent(String eventType, CenterEventBody centerEventBody) throws Throwable {
        RsfUpdater rsfUpdater = Hasor.assertIsNotNull(this.rsfContext, " rsfContext is null.").getUpdater();
        EventProcess process = EventProcessMapping.findEventProcess(eventType);
        if (process == null) {
            throw new RsfCenterException(eventType + " eventType is undefined.");
        }
        //-有些事件需要检测服务-
        String serviceID = centerEventBody.getServiceID();
        for (String checkItem : checkServiceEventArrays) {
            if (checkItem.equals(eventType)) {
                if (this.rsfContext.getServiceInfo(serviceID) == null) {
                    throw new RsfCenterException(serviceID + " service is undefined.");
                }
            }
        }
        boolean result = process.processEvent(rsfUpdater, centerEventBody);
        logger.info("center({}) -> serviceID ={}, result ={}, body ={}.", //
                eventType, centerEventBody.getServiceID(), result, centerEventBody.getEventBody());
        return result;
    }
}