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
package net.hasor.registry.server.pusher;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.registry.client.RsfCenterListener;
import net.hasor.registry.server.domain.LogUtils;
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
 * @author 赵永春 (zyc@hasor.net)
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
    public final List<String> doProcessor(PushEvent event) {
        if (event == null || event.getTargetList() == null) {
            return Collections.emptyList();
        }
        if (event.getTargetList() == null || event.getTargetList().isEmpty()) {
            logger.error(LogUtils.create("ERROR_300_00003")//
                    .addLog("group", event.getGroup())//
                    .addLog("name", event.getName())//
                    .addLog("version", event.getVersion())//
                    .addLog("pushEventType", event.getPushEventType().name())//
                    .toJson());
            return Collections.emptyList();
            //
        } else {
            ArrayList<String> failedAddress = new ArrayList<String>();
            for (String target : event.getTargetList()) {
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
    private boolean doProcessor(String rsfAddress, PushEvent event) {
        boolean result = false;
        result = sendEvent(rsfAddress, event, 1);           // 第一次尝试
        if (!result) {
            result = sendEvent(rsfAddress, event, 2);       // 第二次尝试
            if (!result) {
                result = sendEvent(rsfAddress, event, 3);   // 第三次尝试
            }
        }
        //
        return result;
    }
    /** 数据推送 */
    private boolean sendEvent(String rsfAddress, PushEvent event, int times) {
        String eventType = event.getPushEventType().forCenterEvent();
        String eventBody = event.getEventBody();    // 事件内容
        //
        logger.info(LogUtils.create("INFO_200_00001")//
                .addLog("rsfAddress", rsfAddress)//
                .addLog("eventType", eventType)//
                .addLog("times", times)//
                .toJson());
        //
        try {
            RsfCenterListener listener = this.rsfClientListener.get();
            InterAddress interAddress = new InterAddress(rsfAddress);
            ((RsfServiceWrapper) listener).setTarget(new InstanceAddressProvider(interAddress));
            return listener.onEvent(event.getGroup(), event.getName(), event.getVersion(), eventType, eventBody);
        } catch (Throwable e) {
            logger.error(LogUtils.create("ERROR_300_00002")//
                    .logException(e)//
                    .addLog("rsfAddress", rsfAddress)//
                    .addLog("eventType", eventType)//
                    .toJson());
            return false;
        }
    }
}