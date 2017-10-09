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
package net.hasor.registry.server.pushing;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.registry.RsfCenterListener;
import net.hasor.registry.domain.client.CenterEventBody;
import net.hasor.registry.server.domain.LogUtils;
import net.hasor.registry.trace.TraceUtil;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.provider.InstanceAddressProvider;
import net.hasor.rsf.rpc.caller.RsfServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 执行处理器，该类的作用是将事件推送到指定的客户端中去。
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class PushProcessor {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfContext                     rsfContext;
    private ThreadLocal<RsfCenterListener> rsfClientListener;
    //
    @Init
    public void init() {
        this.rsfClientListener = new ThreadLocal<RsfCenterListener>() {
            protected RsfCenterListener initialValue() {
                return rsfContext.getRsfClient().wrapper(RsfCenterListener.class);
            }
        };
    }
    //
    public final List<InterAddress> doProcessor(PushEvent event) {
        if (event == null) {
            return Collections.emptyList();
        }
        if (event.getTarget() == null || event.getTarget().isEmpty()) {
            logger.error(LogUtils.create("ERROR_300_00003")//
                    .addLog("traceID", TraceUtil.getTraceID())//
                    .addLog("serviceID", event.getServiceID())//
                    .addLog("pushEventType", event.getPushEventType().name())//
                    .toJson());
            return Collections.emptyList();
            //
        } else {
            ArrayList<InterAddress> failedAddress = new ArrayList<InterAddress>();
            for (InterAddress target : event.getTarget()) {
                boolean res = this.doProcessor(target, event);
                if (!res) {
                    failedAddress.add(target);
                }
            }
            return failedAddress;
        }
    }
    //
    /**
     * 向客户端推送数据,3次重试
     * @param rsfAddress 目标客户端
     * @param event 数据
     */
    private boolean doProcessor(InterAddress rsfAddress, PushEvent event) {
        CenterEventBody eventBody = new CenterEventBody();
        eventBody.setEventType(event.getPushEventType().forCenterEvent());
        eventBody.setServiceID(event.getServiceID());
        eventBody.setEventBody(event.getEventBody());
        boolean result = false;
        //
        result = sendEvent(rsfAddress, eventBody, 1);           // 第一次尝试
        if (!result) {
            result = sendEvent(rsfAddress, eventBody, 2);       // 第二次尝试
            if (!result) {
                result = sendEvent(rsfAddress, eventBody, 3);   // 第三次尝试
            }
        }
        //
        return result;
    }
    /** 数据推送 */
    private boolean sendEvent(InterAddress rsfAddress, CenterEventBody eventBody, int times) {
        //
        logger.info(LogUtils.create("INFO_200_00001")//
                .addLog("traceID", TraceUtil.getTraceID())//
                .addLog("serviceID", eventBody.getServiceID())//
                .addLog("rsfAddress", rsfAddress)//
                .addLog("eventType", eventBody.getEventType())//
                .addLog("times", times)//
                .toJson());
        //
        try {
            RsfCenterListener listener = this.rsfClientListener.get();
            ((RsfServiceWrapper) listener).setTarget(new InstanceAddressProvider(rsfAddress));
            return listener.onEvent(eventBody.getEventType(), eventBody);
        } catch (Throwable e) {
            logger.error(LogUtils.create("ERROR_300_00002")//
                    .addLog("traceID", TraceUtil.getTraceID())//
                    .logException(e)//
                    .addLog("rsfAddress", rsfAddress.toHostSchema())//
                    .addLog("serviceID", eventBody.getServiceID())//
                    .addLog("eventType", eventBody.getEventType())//
                    .addLog("eventBody", eventBody.getEventBody())//
                    .toJson());
            return false;
        }
    }
}