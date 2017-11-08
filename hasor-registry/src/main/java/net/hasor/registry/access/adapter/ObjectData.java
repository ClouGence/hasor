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
package net.hasor.registry.access.adapter;
/**
 * 服务数据存储检索
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ObjectData {
    private String dataPath;
    private String instanceID;
    private String serviceID;
    private String dataType;
    private String dataBody;
    private long   timestamp;
    //
    public ObjectData(String dataBody) {
        this.dataBody = dataBody;
    }
    //
    public String getDataPath() {
        return dataPath;
    }
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    public String getInstanceID() {
        return instanceID;
    }
    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }
    public String getServiceID() {
        return serviceID;
    }
    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getDataBody() {
        return dataBody;
    }
    public void setDataBody(String dataBody) {
        this.dataBody = dataBody;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}