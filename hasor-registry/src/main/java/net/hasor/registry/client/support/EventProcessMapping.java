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
package net.hasor.registry.client.support;
import net.hasor.registry.client.RsfCenterEvent;
import net.hasor.registry.client.domain.CenterEventBody;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfUpdater;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 注册中心事件响应实现
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
class EventProcessMapping {
    protected static Logger logger = LoggerFactory.getLogger(EventProcessMapping.class);
    private static String nowData() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
    }
    private static List<InterAddress> convertTo(String addressSetBody) {
        String[] addressSet = StringUtils.isBlank(addressSetBody) ? null : addressSetBody.split(",");
        if (addressSet == null || addressSet.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<InterAddress> addressList = new ArrayList<InterAddress>();
        for (String address : addressSet) {
            try {
                addressList.add(new InterAddress(address));
            } catch (Throwable e) {
                logger.error("address {} Invalid format -> {}.", address, e.getMessage());
            }
        }
        return addressList;
    }
    //
    private static final Map<String, EventProcess> eventProcessMap;

    static {
        eventProcessMap = new HashMap<String, EventProcess>();
        eventProcessMap.put(RsfCenterEvent.RsfCenter_AppendAddressEvent, new AppendAddressEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_RefreshAddressEvent, new RefreshAddressEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_RemoveAddressEvent, new RemoveAddressEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateServiceRouteEvent, new UpdateServiceRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateMethodRouteEvent, new UpdateMethodRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateArgsRouteEvent, new UpdateArgsRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateFlowControlEvent, new UpdateFlowControlEvent());
    }

    //
    public static EventProcess findEventProcess(String eventType) {
        if (StringUtils.isBlank(eventType)) {
            return null;
        }
        return eventProcessMap.get(eventType);
    }
    //
    //
    //
    /**追加或重新激活地址。*/
    private static class AppendAddressEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, String rsfBindID, CenterEventBody centerEventBody) {
            String eventBody = centerEventBody.getEventBody();
            //
            List<InterAddress> addressList = convertTo(eventBody);
            if (addressList != null && !addressList.isEmpty()) {
                rsfUpdater.appendAddress(rsfBindID, addressList);
                logger.info("receiver Event from RsfCenter , eventType=AppendAddressEvent, serviceID = {} , addressSet = {}.", rsfBindID, eventBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=AppendAddressEvent, serviceID = {} , addressSet is empty.", rsfBindID);
            }
            return true;
        }
    }
    /**使用新的地址本替换已有的地址本。*/
    private static class RefreshAddressEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, String rsfBindID, CenterEventBody centerEventBody) {
            String eventBody = centerEventBody.getEventBody();
            //
            List<InterAddress> addressList = convertTo(eventBody);
            if (addressList != null && !addressList.isEmpty()) {
                rsfUpdater.refreshAddress(rsfBindID, addressList);
                logger.info("receiver Event from RsfCenter , eventType=RefreshAddressEvent, serviceID = {} , atTime = {}.", rsfBindID, nowData());
            } else {
                logger.info("receiver Event from RsfCenter , eventType=RefreshAddressEvent, serviceID = {} , addressSet is empty.", rsfBindID);
            }
            return true;
        }
    }
    /**推送无效的地址，客户端对此地址进行删除操作。*/
    private static class RemoveAddressEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, String rsfBindID, CenterEventBody centerEventBody) {
            String eventBody = centerEventBody.getEventBody();
            //
            List<InterAddress> addressList = convertTo(eventBody);
            if (addressList != null && !addressList.isEmpty()) {
                rsfUpdater.removeAddress(rsfBindID, addressList);
                logger.info("receiver Event from RsfCenter , eventType=RemoveAddressEvent, serviceID = {} , addressSet = {}.", rsfBindID, eventBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=RemoveAddressEvent, serviceID = {} , addressSet is empty.", rsfBindID);
            }
            return true;
        }
    }
    /**推送服务级路由规则*/
    private static class UpdateServiceRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, String rsfBindID, CenterEventBody centerEventBody) {
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateServiceRouteEvent , serviceID = {} -> atTime = {}.", rsfBindID, nowData());
                rsfUpdater.updateServiceRoute(rsfBindID, scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateServiceRouteEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", rsfBindID, nowData());
            }
            return true;
        }
    }
    /**推送方法级路由规则*/
    private static class UpdateMethodRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, String rsfBindID, CenterEventBody centerEventBody) {
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateMethodRouteEvent , serviceID = {} -> atTime = {}.", rsfBindID, nowData());
                rsfUpdater.updateMethodRoute(rsfBindID, scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateMethodRouteEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", rsfBindID, nowData());
            }
            return true;
        }
    }
    /**推送参数级路由规则*/
    private static class UpdateArgsRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, String rsfBindID, CenterEventBody centerEventBody) {
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateArgsRouteEvent , serviceID = {} -> atTime = {}.", rsfBindID, nowData());
                rsfUpdater.updateArgsRoute(rsfBindID, scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateArgsRouteEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", rsfBindID, nowData());
            }
            return true;
        }
    }
    /**推送流控流控规则*/
    private static class UpdateFlowControlEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, String rsfBindID, CenterEventBody centerEventBody) {
            String flowControl = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(flowControl)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateFlowControlEvent , serviceID = {} -> atTime = {}.", rsfBindID, nowData());
                rsfUpdater.updateFlowControl(rsfBindID, flowControl);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateFlowControlEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", rsfBindID, nowData());
            }
            return true;
        }
    }
}