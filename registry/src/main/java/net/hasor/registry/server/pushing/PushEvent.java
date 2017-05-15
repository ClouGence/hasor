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
import net.hasor.rsf.InterAddress;

import java.util.ArrayList;
import java.util.List;
/**
 * 需要推送的事件对象
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class PushEvent {
    private String             serviceID;       //ServiceID
    private List<InterAddress> targetList;      //推送指令对特定RSF客户端的指向
    //
    private RsfCenterEventEnum pushEventType;   //推送的事件类型 @see RsfCenterEvent枚举
    private String             eventBody;       //内容体（真正推送的内容）
    //
    PushEvent(String serviceID, List<InterAddress> targets, RsfCenterEventEnum pushEventType) {
        this.serviceID = serviceID;
        this.pushEventType = pushEventType;
        if (targets != null) {
            this.targetList = targets;
        } else {
            this.targetList = new ArrayList<InterAddress>();
        }
    }
    //
    public String getServiceID() {
        return serviceID;
    }
    public List<InterAddress> getTarget() {
        return targetList;
    }
    public String getEventBody() {
        return eventBody;
    }
    public PushEvent setEventBody(String eventBody) {
        this.eventBody = eventBody;
        return this;
    }
    public RsfCenterEventEnum getPushEventType() {
        return pushEventType;
    }
    @Override
    public String toString() {
        return "{PushEvent=" + this.pushEventType.name() + " ,hashCode=" + this.hashCode() + "}";
    }
}