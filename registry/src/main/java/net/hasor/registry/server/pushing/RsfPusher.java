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
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.InterAddress;

import java.util.Collection;
import java.util.List;
/**
 * 推送服务触发器
 * @version : 2016年3月1日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class RsfPusher {
    @Inject
    private PushQueue pushQueue;
    //
    /** 推送服务路由脚本(服务级) */
    public boolean updateServiceRoute(String serviceID, String scriptBody, List<InterAddress> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateServiceRouteEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(scriptBody);
        return this.pushQueue.doPushEvent(eventData);
    }
    /** 推送服务路由脚本(方法级) */
    public boolean updateMethodRoute(String serviceID, String scriptBody, List<InterAddress> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateMethodRouteEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(scriptBody);
        return this.pushQueue.doPushEvent(eventData);
    }
    /** 推送服务路由脚本(参数级) */
    public boolean updateArgsRoute(String serviceID, String scriptBody, List<InterAddress> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateArgsRouteEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(scriptBody);
        return this.pushQueue.doPushEvent(eventData);
    }
    /** 推送服务流控规则 */
    public boolean updateFlowControl(String serviceID, String flowControl, List<InterAddress> targets) {
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.UpdateFlowControlEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        eventData.setEventBody(flowControl);
        return this.pushQueue.doPushEvent(eventData);
    }
    //
    /** 增量推送服务地址 */
    public boolean appendAddress(String serviceID, Collection<InterAddress> newHostSet, List<InterAddress> targets) {
        if (newHostSet == null || newHostSet.isEmpty()) {
            return false;
        }
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.AppendAddressEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        StringBuilder strBuilder = new StringBuilder("");
        for (InterAddress addr : newHostSet) {
            strBuilder.append(",");
            strBuilder.append(addr.toHostSchema());
        }
        //
        eventData.setEventBody(strBuilder.substring(1));
        return this.pushQueue.doPushEvent(eventData);
    }
    /** 全量推送服务地址 */
    public boolean refreshAddress(String serviceID, Collection<InterAddress> allHostSet, List<InterAddress> targets) {
        if (allHostSet == null || allHostSet.isEmpty()) {
            return false;
        }
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.RefreshAddressEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        StringBuilder strBuilder = new StringBuilder("");
        for (InterAddress addr : allHostSet) {
            strBuilder.append(",");
            strBuilder.append(addr.toHostSchema());
        }
        //
        eventData.setEventBody(strBuilder.substring(1));
        return this.pushQueue.doPushEvent(eventData);
    }
    /** 删除服务地址 */
    public boolean removeAddress(String serviceID, Collection<InterAddress> invalidAddressSet, List<InterAddress> targets) {
        if (invalidAddressSet == null || invalidAddressSet.isEmpty()) {
            return false;
        }
        RsfCenterEventEnum centerEventEnum = RsfCenterEventEnum.RemoveAddressEvent;
        PushEvent eventData = new PushEvent(serviceID, targets, centerEventEnum);
        StringBuilder strBuilder = new StringBuilder("");
        for (InterAddress addr : invalidAddressSet) {
            strBuilder.append(",");
            strBuilder.append(addr.toHostSchema());
        }
        //
        eventData.setEventBody(strBuilder.substring(1));
        return this.pushQueue.doPushEvent(eventData);
    }
}