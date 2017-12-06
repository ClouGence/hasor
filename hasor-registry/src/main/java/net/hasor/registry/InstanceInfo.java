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
package net.hasor.registry;
/**
 * 实列信息。
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class InstanceInfo {
    private String instanceID;
    private String unitName;
    private String rsfAddress;
    private String defaultProtocol;
    //
    public String getInstanceID() {
        return this.instanceID;
    }
    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }
    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getRsfAddress() {
        return rsfAddress;
    }
    public void setRsfAddress(String rsfAddress) {
        this.rsfAddress = rsfAddress;
    }
    public String getDefaultProtocol() {
        return defaultProtocol;
    }
    public void setDefaultProtocol(String defaultProtocol) {
        this.defaultProtocol = defaultProtocol;
    }
}