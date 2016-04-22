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
package net.hasor.rsf.center.client;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfUpdater;
import net.hasor.rsf.center.RsfCenterListener;
import net.hasor.rsf.center.domain.CenterEventBody;
import net.hasor.rsf.domain.RsfCenterException;
/**
 * 注册中心数据接收器，负责更新注册中心推送过来的配置信息。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterDataReceiver implements RsfCenterListener {
    protected Logger              logger = LoggerFactory.getLogger(getClass());
    private final RsfContext      rsfContext;
    private final EventContext    eventContext;
    private static final String[] checkServiceEventArrays;
    static {
        checkServiceEventArrays = new String[] { //
                "AppendAddressEvent", //
                "RemoveAddressEvent", //
                "UpdateServiceRouteEvent", //
                "UpdateMethodRouteEvent", //
                "UpdateArgsRouteEvent", //
                "UpdateFlowControlEvent" };
    }
    //
    //
    public RsfCenterDataReceiver(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.eventContext = rsfContext.getAppContext().getEnvironment().getEventContext();
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
        //-发送CenterUpdate_Event事件-
        boolean result = process.processEvent(rsfUpdater, centerEventBody);
        if (result == true) {
            if (StringUtils.isBlank(serviceID) == false) {
                this.eventContext.fireSyncEvent(RsfCenterClientManager.CenterUpdate_Event, centerEventBody);//同步更新服务的CenterMarkData
            }
        }
        return result;
    }
}