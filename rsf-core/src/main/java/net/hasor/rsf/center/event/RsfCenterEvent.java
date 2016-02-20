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
package net.hasor.rsf.center.event;
import org.more.util.StringUtils;
/**
 * 更新地址本
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public enum RsfCenterEvent {
    /**追加或重新激活地址。*/
    RsfCenter_AppendAddressEvent("AppendAddressEvent"), //
    /**刷新服务地址本。*/
    RsfCenter_RefreshAddressEvent("RefreshAddressEvent"), //
    /**推送无效的地址，客户端对此地址进行删除操作。*/
    RsfCenter_RemoveAddressEvent("RemoveAddressEvent"), //
    //
    /**推送默认服务级路由规则*/
    RsfCenter_UpdateDefaultServiceRouteEvent("UpdateServiceRouteEvent"), //
    /**推送服务级路由规则*/
    RsfCenter_UpdateServiceRouteEvent("UpdateServiceRouteEvent"), //
    /**推送默认方法级路由规则*/
    RsfCenter_UpdateDefaultMethodRouteEvent("UpdateMethodRouteEvent"), //
    /**推送方法级路由规则*/
    RsfCenter_UpdateMethodRouteEvent("UpdateMethodRouteEvent"), //
    /**推送默认参数级路由规则*/
    RsfCenter_UpdateDefaultArgsRouteEvent("UpdateArgsRouteEvent "), //
    /**推送参数级路由规则*/
    RsfCenter_UpdateArgsRouteEvent("UpdateArgsRouteEvent "), //
    //
    /**推送默认服务流控规则*/
    RsfCenter_UpdateDefaultFlowControlEvent("UpdateDefaultFlowControlEvent"), //
    /**推送流控流控规则*/
    RsfCenter_UpdateFlowControlEvent("UpdateFlowControlEvent"),//
    ;
    private String eventType;
    RsfCenterEvent(String eventType) {
        this.eventType = eventType;
    }
    @Override
    public String toString() {
        return "RsfCenterEvent[" + eventType + "]";
    }
    public static RsfCenterEvent getEventEnum(String eventType) {
        if (eventType != null) {
            for (RsfCenterEvent t : RsfCenterEvent.values()) {
                if (StringUtils.equalsBlankIgnoreCase(eventType, t.eventType)) {
                    return t;
                }
            }
        }
        return null;
    }
    public String getEventType() {
        return eventType;
    }
}