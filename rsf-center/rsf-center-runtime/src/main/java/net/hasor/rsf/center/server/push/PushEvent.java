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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class PushEvent implements Comparable<PushEvent> {
    private RsfCenterPushEvent pushEventType;//推送类型
    private String             snapshotInfo; //snapshotInfo
    private String             serviceID;    //ServiceID
    private List<String>       targetList;   //推送指令对特定RSF客户端的指向
    private String             eventBody;    //内容体
    //
    PushEvent(String serviceID, RsfCenterPushEvent pushEventType) {
        this.serviceID = serviceID;
        this.pushEventType = pushEventType;
        this.targetList = new ArrayList<String>();
    }
    //
    public String getServiceID() {
        return serviceID;
    }
    public PushEvent setSnapshotInfo(String snapshotInfo) {
        this.snapshotInfo = snapshotInfo;
        return this;
    }
    public String getSnapshotInfo() {
        return this.snapshotInfo;
    }
    public List<String> getTarget() {
        return targetList;
    }
    public PushEvent addTarget(List<String> targetList) {
        if (targetList != null) {
            this.targetList.addAll(targetList);
        }
        return this;
    }
    public PushEvent addTarget(String target) {
        if (target != null) {
            this.targetList.add(target);
        }
        return this;
    }
    public String getEventBody() {
        return eventBody;
    }
    public PushEvent setEventBody(String eventBody) {
        this.eventBody = eventBody;
        return this;
    }
    public RsfCenterPushEvent getPushEventType() {
        return pushEventType;
    }
    //
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PushEvent) {
            PushEvent diffEvent = (PushEvent) obj;
            return this.hashCode() == diffEvent.hashCode();
        }
        return false;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pushEventType == null) ? 0 : pushEventType.hashCode());
        result = prime * result + Integer.valueOf(this.targetList.size()).hashCode();
        if (this.targetList != null && this.targetList.isEmpty() == false) {
            List<String> shortArray = new ArrayList<String>(this.targetList);
            Collections.sort(shortArray);
            for (String addr : this.targetList) {
                result = prime * result + ((addr == null) ? 0 : addr.hashCode());
            }
        }
        result = prime * result + ((eventBody == null) ? 0 : eventBody.hashCode());
        return result;
    }
    @Override
    public String toString() {
        return "{PushEvent=" + this.pushEventType.name() + " ,hashCode=" + this.hashCode() + "}";
    }
    @Override
    public int compareTo(PushEvent o) {
        return Integer.valueOf(this.hashCode()).compareTo(o.hashCode());
    }
}