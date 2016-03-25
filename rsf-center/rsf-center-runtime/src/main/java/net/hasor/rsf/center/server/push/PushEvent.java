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
import org.more.util.StringUtils;
/**
 * 
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class PushEvent implements Comparable<PushEvent> {
    private RsfCenterPushEventEnum pushEventType;//推送类型
    private String                 target;       //推送指令对特定RSF客户端的指向
    private String                 eventBody;    //内容体
    //
    PushEvent(RsfCenterPushEventEnum pushEventType) {
        this.pushEventType = pushEventType;
    }
    //
    public String getTarget() {
        return target;
    }
    public PushEvent setTarget(String target) {
        this.target = target;
        return this;
    }
    public String getEventBody() {
        return eventBody;
    }
    public PushEvent setEventBody(String eventBody) {
        this.eventBody = eventBody;
        return this;
    }
    public RsfCenterPushEventEnum getPushEventType() {
        return pushEventType;
    }
    //
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PushEvent) {
            PushEvent diffEvent = (PushEvent) obj;
            if (this.pushEventType == diffEvent.pushEventType) {
                if (StringUtils.equalsIgnoreCase(target, diffEvent.target)) {
                    return StringUtils.equals(eventBody, diffEvent.eventBody);
                }
            }
        }
        return false;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pushEventType == null) ? 0 : pushEventType.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((eventBody == null) ? 0 : eventBody.hashCode());
        return result;
    }
    @Override
    public String toString() {
        return "{PushEvent=" + this.pushEventType.name() + " ,target=" + this.target + " ,eventBody=" + this.eventBody + "}";
    }
    @Override
    public int compareTo(PushEvent o) {
        return Integer.valueOf(this.hashCode()).compareTo(o.hashCode());
    }
}