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
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.client.domain.ServiceID;

import java.util.Collection;
import java.util.List;

/**
 * 推送服务触发器
 * @version : 2016年3月1日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class RsfPusher {
    @Inject
    private PushQueue pushQueue;
    //

    /** 推送服务路由脚本(服务级) */
    public boolean updateServiceRoute(ServiceID serviceID, String scriptBody, List<String> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateServiceRouteEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(scriptBody);
        return this.pushQueue.doPushEvent(eventData);
    }

    /** 推送服务路由脚本(方法级) */
    public boolean updateMethodRoute(ServiceID serviceID, String scriptBody, List<String> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateMethodRouteEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(scriptBody);
        return this.pushQueue.doPushEvent(eventData);
    }

    /** 推送服务路由脚本(参数级) */
    public boolean updateArgsRoute(ServiceID serviceID, String scriptBody, List<String> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateArgsRouteEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(scriptBody);
        return this.pushQueue.doPushEvent(eventData);
    }

    /** 推送服务流控规则 */
    public boolean updateFlowControl(ServiceID serviceID, String flowControl, List<String> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateFlowControlEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(flowControl);
        return this.pushQueue.doPushEvent(eventData);
    }
    //

    /** 增量推送服务地址 */
    public boolean appendAddress(ServiceID serviceID, Collection<String> newHostSet, List<String> targets) {
        if (newHostSet == null || newHostSet.isEmpty()) {
            return false;
        }
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.AppendAddressEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        StringBuilder strBuilder = new StringBuilder("");
        for (String addr : newHostSet) {
            strBuilder.append(",");
            strBuilder.append(addr.trim());
        }
        //
        eventData.setEventBody(strBuilder.substring(1));
        return this.pushQueue.doPushEvent(eventData);
    }

    /** 全量推送服务地址 */
    public boolean refreshAddress(ServiceID serviceID, Collection<String> allHostSet, List<String> targets) {
        if (allHostSet == null || allHostSet.isEmpty()) {
            return false;
        }
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.RefreshAddressEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        StringBuilder strBuilder = new StringBuilder("");
        for (String addr : allHostSet) {
            strBuilder.append(",");
            strBuilder.append(addr.trim());
        }
        //
        eventData.setEventBody(strBuilder.substring(1));
        return this.pushQueue.doPushEvent(eventData);
    }

    /** 删除服务地址 */
    public boolean removeAddress(ServiceID serviceID, Collection<String> invalidAddressSet, List<String> targets) {
        if (invalidAddressSet == null || invalidAddressSet.isEmpty()) {
            return false;
        }
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.RemoveAddressEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        StringBuilder strBuilder = new StringBuilder("");
        for (String addr : invalidAddressSet) {
            strBuilder.append(",");
            strBuilder.append(addr.trim());
        }
        //
        eventData.setEventBody(strBuilder.substring(1));
        return this.pushQueue.doPushEvent(eventData);
    }
}