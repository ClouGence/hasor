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
package net.hasor.rsf.center.server.push;
import net.hasor.rsf.center.event.RsfCenterEvent;
import net.hasor.rsf.center.server.push.processor.AppendAddressProcessor;
import net.hasor.rsf.center.server.push.processor.RefreshAddressProcessor;
import net.hasor.rsf.center.server.push.processor.RemoveAddressProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateArgsRouteProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateDefaultArgsProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateDefaultFlowControlProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateDefaultMethodRouteProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateDefaultServiceRouteProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateFlowControlProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateMethodRouteProcessor;
import net.hasor.rsf.center.server.push.processor.UpdateServiceRouteProcessor;
/**
 * 注册中心下推送的事件类型
 * 
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public enum RsfCenterPushEventEnum {
    /**
     * 追加或重新激活地址
     * 说明：每当新的提供者上线时，都会通过该事件推送给消费者端。*/
    AppendAddressEvent(RsfCenterEvent.RsfCenter_AppendAddressEvent, AppendAddressProcessor.class), //
    /**
     * 使用新的地址本替换已有的地址本。
     * 说明：废弃服务已有的地址本，使用全新的地址本加以替换。*/
    RefreshAddressEvent(RsfCenterEvent.RsfCenter_RefreshAddressEvent, RefreshAddressProcessor.class), //
    /**
     * 推送无效的地址
     * 说明：当有服务下线时，注册中心会通过该事件将无效的地址推送给消费者。*/
    RemoveAddressEvent(RsfCenterEvent.RsfCenter_RemoveAddressEvent, RemoveAddressProcessor.class), //
    //
    /**
     * 推送默认服务级路由规则
     * 说明：推送服务的路由规则给RSF客户端，该路由策略会影响到RSF客户端上所有订阅的服务。*/
    UpdateDefaultServiceRouteEvent(RsfCenterEvent.RsfCenter_UpdateDefaultServiceRouteEvent, UpdateDefaultServiceRouteProcessor.class), //
    /**
     * 推送服务级路由规则
     * 说明：推送服务的路由规则给RSF客户端，该路由策略只会影响到特定的服务。*/
    UpdateServiceRouteEvent(RsfCenterEvent.RsfCenter_UpdateServiceRouteEvent, UpdateServiceRouteProcessor.class), //
    /**
     * 推送默认方法级路由规则。方法级路由是指：服务的路由策略可以为不同的方法做特殊配置
     * 说明：推送服务的路由规则给RSF客户端，该路由策略会影响到RSF客户端上所有订阅的服务。*/
    UpdateDefaultMethodRouteEvent(RsfCenterEvent.RsfCenter_UpdateDefaultMethodRouteEvent, UpdateDefaultMethodRouteProcessor.class), //
    /**
     * 推送方法级路由规则。方法级路由是指：服务的路由策略可以为不同的方法做特殊配置
     * 说明：推送服务的路由规则给RSF客户端，该路由策略只会影响到特定的服务。*/
    UpdateMethodRouteEvent(RsfCenterEvent.RsfCenter_UpdateMethodRouteEvent, UpdateMethodRouteProcessor.class), //
    /**
     * 推送默认参数级路由规则。参数级路由是指：服务的路由策略可以细分到服务方法的参数上，例如根据userID散列不用的服务调用地址
     * 说明：推送服务的路由规则给RSF客户端，该路由策略会影响到RSF客户端上所有订阅的服务。*/
    UpdateDefaultArgsRouteEvent(RsfCenterEvent.RsfCenter_UpdateDefaultArgsRouteEvent, UpdateDefaultArgsProcessor.class), //
    /**
     * 推送参数级路由规则。参数级路由是指：服务的路由策略可以细分到服务方法的参数上，例如根据userID散列不用的服务调用地址
     * 说明：推送服务的路由规则给RSF客户端，该路由策略只会影响到特定的服务。*/
    UpdateArgsRouteEvent(RsfCenterEvent.RsfCenter_UpdateArgsRouteEvent, UpdateArgsRouteProcessor.class), //
    //
    /**
     * 推送默认服务流控规则，流控规则包含了：限流、选址、单元化
     * 说明：推送服务的路由规则给RSF客户端，该路由策略会影响到RSF客户端上所有订阅的服务。*/
    UpdateDefaultFlowControlEvent(RsfCenterEvent.RsfCenter_UpdateDefaultFlowControlEvent, UpdateDefaultFlowControlProcessor.class), //
    /**
     * 推送流控流控规则，流控规则包含了：限流、选址、单元化
     * 说明：推送服务的路由规则给RSF客户端，该路由策略只会影响到特定的服务。*/
    UpdateFlowControlEvent(RsfCenterEvent.RsfCenter_UpdateFlowControlEvent, UpdateFlowControlProcessor.class);
    //
    //---------------------------------------------------------------------------------------------
    //
    //
    private RsfCenterEvent                 eventType;
    private Class<? extends PushProcessor> processorType;
    RsfCenterPushEventEnum(RsfCenterEvent eventType, Class<? extends PushProcessor> processorType) {
        this.eventType = eventType;
        this.processorType = processorType;
    }
    public Class<? extends PushProcessor> getProcessorType() {
        return processorType;
    }
    @Override
    public String toString() {
        return this.name();
    }
    public RsfCenterEvent getEventType() {
        return eventType;
    }
    public PushEvent newEvent(String serviceID) {
        return new PushEvent(serviceID, this);
    }
}