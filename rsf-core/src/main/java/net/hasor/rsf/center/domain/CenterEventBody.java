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
package net.hasor.rsf.center.domain;
import java.io.Serializable;
/**
 * 
 * @version : 2016年3月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class CenterEventBody implements Serializable {
    private static final long serialVersionUID = 1617451556801258822L;
    private String            eventType;
    private String            serviceID;                              //相关服务ID
    private String            snapshotInfo;                           //服务中心上的快照
    private String            eventBody;                              //事件内容
    //
    public String getEventType() {
        return this.eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public String getServiceID() {
        return this.serviceID;
    }
    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    public String getSnapshotInfo() {
        return this.snapshotInfo;
    }
    public void setSnapshotInfo(String snapshotInfo) {
        this.snapshotInfo = snapshotInfo;
    }
    public String getEventBody() {
        return this.eventBody;
    }
    public void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }
}