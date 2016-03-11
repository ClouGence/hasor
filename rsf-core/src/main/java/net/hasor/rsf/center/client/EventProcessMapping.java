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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.RsfUpdater;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.domain.CenterEventBody;
import net.hasor.rsf.center.event.RsfCenterEvent;
/**
 * 注册中心事件响应实现
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
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
    private static final Map<RsfCenterEvent, EventProcess> eventProcessMap;
    static {
        eventProcessMap = new HashMap<RsfCenterEvent, EventProcess>();
        eventProcessMap.put(RsfCenterEvent.RsfCenter_AppendAddressEvent, new AppendAddressEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_RefreshAddressEvent, new RefreshAddressEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_RemoveAddressEvent, new RemoveAddressEvent());
        //
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateDefaultServiceRouteEvent, new UpdateDefaultServiceRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateServiceRouteEvent, new UpdateServiceRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateDefaultMethodRouteEvent, new UpdateDefaultMethodRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateMethodRouteEvent, new UpdateMethodRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateDefaultArgsRouteEvent, new UpdateDefaultArgsRouteEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateArgsRouteEvent, new UpdateArgsRouteEvent());
        //
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateDefaultFlowControlEvent, new UpdateDefaultFlowControlEvent());
        eventProcessMap.put(RsfCenterEvent.RsfCenter_UpdateFlowControlEvent, new UpdateFlowControlEvent());
    }
    //
    public static EventProcess findEventProcess(String eventType) {
        RsfCenterEvent type = RsfCenterEvent.getEventEnum(eventType);
        if (type == null) {
            return null;
        }
        return eventProcessMap.get(type);
    }
    //
    //
    //
    /**追加或重新激活地址。*/
    private static class AppendAddressEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String serviceID = centerEventBody.getServiceID();
            String eventBody = centerEventBody.getEventBody();
            //
            List<InterAddress> addressList = convertTo(eventBody);
            if (addressList != null && addressList.isEmpty() == false) {
                rsfUpdater.appendAddress(serviceID, addressList);
                logger.info("receiver Event from RsfCenter , eventType=AppendAddressEvent, serviceID = {} , addressSet = {}.", serviceID, eventBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=AppendAddressEvent, serviceID = {} , addressSet is empty.", serviceID);
            }
            return true;
        }
    }
    /**刷新服务地址本。*/
    private static class RefreshAddressEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String serviceID = centerEventBody.getServiceID();
            //
            rsfUpdater.refreshAddressCache(serviceID);
            logger.info("receiver Event from RsfCenter , eventType=RefreshAddressEvent, serviceID = {} , atTime = {}.", serviceID, nowData());
            return true;
        }
    }
    /**推送无效的地址，客户端对此地址进行删除操作。*/
    private static class RemoveAddressEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String serviceID = centerEventBody.getServiceID();
            String eventBody = centerEventBody.getEventBody();
            //
            List<InterAddress> addressList = convertTo(eventBody);
            if (addressList != null && addressList.isEmpty() == false) {
                rsfUpdater.removeAddress(serviceID, addressList);
                logger.info("receiver Event from RsfCenter , eventType=RemoveAddressEvent, serviceID = {} , addressSet = {}.", serviceID, eventBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=RemoveAddressEvent, serviceID = {} , addressSet is empty.", serviceID);
            }
            return true;
        }
    }
    /**推送默认服务级路由规则*/
    private static class UpdateDefaultServiceRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultServiceRouteEvent -> atTime = {}.", nowData());
                rsfUpdater.updateDefaultServiceRoute(scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultServiceRouteEvent -> scriptBody is empty , atTime = {}.", nowData());
            }
            return true;
        }
    }
    /**推送服务级路由规则*/
    private static class UpdateServiceRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String serviceID = centerEventBody.getServiceID();
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateServiceRouteEvent , serviceID = {} -> atTime = {}.", serviceID, nowData());
                rsfUpdater.updateServiceRoute(serviceID, scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateServiceRouteEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", serviceID, nowData());
            }
            return true;
        }
    }
    /**推送默认方法级路由规则*/
    private static class UpdateDefaultMethodRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultMethodRouteEvent -> atTime = {}.", nowData());
                rsfUpdater.updateDefaultMethodRoute(scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultMethodRouteEvent -> scriptBody is empty , atTime = {}.", nowData());
            }
            return true;
        }
    }
    /**推送方法级路由规则*/
    private static class UpdateMethodRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String serviceID = centerEventBody.getServiceID();
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateMethodRouteEvent , serviceID = {} -> atTime = {}.", serviceID, nowData());
                rsfUpdater.updateMethodRoute(serviceID, scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateMethodRouteEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", serviceID, nowData());
            }
            return true;
        }
    }
    /**推送默认参数级路由规则*/
    private static class UpdateDefaultArgsRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultArgsRouteEvent -> atTime = {}.", nowData());
                rsfUpdater.updateDefaultArgsRoute(scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultArgsRouteEvent -> scriptBody is empty , atTime = {}.", nowData());
            }
            return true;
        }
    }
    /**推送参数级路由规则*/
    private static class UpdateArgsRouteEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String serviceID = centerEventBody.getServiceID();
            String scriptBody = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(scriptBody)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateArgsRouteEvent , serviceID = {} -> atTime = {}.", serviceID, nowData());
                rsfUpdater.updateArgsRoute(serviceID, scriptBody);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateArgsRouteEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", serviceID, nowData());
            }
            return true;
        }
    }
    /**推送默认服务流控规则*/
    private static class UpdateDefaultFlowControlEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String flowControl = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(flowControl)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultFlowControlEvent -> atTime = {}.", nowData());
                rsfUpdater.updateDefaultFlowControl(flowControl);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateDefaultFlowControlEvent -> flowControl is empty , atTime = {}.", nowData());
            }
            return true;
        }
    }
    /**推送流控流控规则*/
    private static class UpdateFlowControlEvent implements EventProcess {
        @Override
        public boolean processEvent(RsfUpdater rsfUpdater, CenterEventBody centerEventBody) {
            String serviceID = centerEventBody.getServiceID();
            String flowControl = centerEventBody.getEventBody();
            //
            if (StringUtils.isNotBlank(flowControl)) {
                logger.info("receiver Event from RsfCenter , eventType=UpdateFlowControlEvent , serviceID = {} -> atTime = {}.", serviceID, nowData());
                rsfUpdater.updateFlowControl(serviceID, flowControl);
            } else {
                logger.info("receiver Event from RsfCenter , eventType=UpdateFlowControlEvent , serviceID = {} -> scriptBody is empty , atTime = {}.", serviceID, nowData());
            }
            return true;
        }
    }
}