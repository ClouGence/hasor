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
import net.hasor.registry.client.domain.ServiceID;

import java.util.ArrayList;
import java.util.List;

/**
 * 需要推送的事件对象
 * @version : 2016年3月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class PushEvent {
    private String             group;           // Group
    private String             name;            // Name
    private String             version;         // Version
    private List<String>       targetList;      // 推送指令对特定RSF客户端的指向
    //
    private RsfCenterEventEnum pushEventType;   // 推送的事件类型 @see RsfCenterEvent枚举
    private String             eventBody;       // 内容体（真正推送的内容）

    //
    PushEvent(ServiceID serviceID, List<String> targets, RsfCenterEventEnum pushEventType) {
        this.group = serviceID.getBindGroup();
        this.name = serviceID.getBindName();
        this.version = serviceID.getBindVersion();
        this.pushEventType = pushEventType;
        this.targetList = (targets != null) ? targets : new ArrayList<String>();
    }

    //
    //
    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getTargetList() {
        return targetList;
    }

    public RsfCenterEventEnum getPushEventType() {
        return pushEventType;
    }

    public String getEventBody() {
        return eventBody;
    }

    public void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }

    //
    //
    @Override
    public String toString() {
        return "{PushEvent=" + this.pushEventType.name() + " ,hashCode=" + this.hashCode() + "}";
    }
}