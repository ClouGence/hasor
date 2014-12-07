/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.runtime.register;
/**
 * 表示远程服务的地址和服务提供的版本信息。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressInfo {
    private String hostIP   = null;
    private int    hostPort = 8000;
    private String version  = null;
    //
    public String getID() {
        return hostIP + ":" + this.hostPort;
    }
    public String getHostIP() {
        return hostIP;
    }
    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }
    public int getHostPort() {
        return hostPort;
    }
    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
}