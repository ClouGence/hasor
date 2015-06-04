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
package net.hasor.rsf.center.domain.entity;
/**
 * 终端
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class TerminalDO {
    private String terminalID;
    private String terminalSecret;
    private String remoteIP;
    private int    remotePort;
    private String remoteUnit;
    private String remoteVersion;
    //
    public String getTerminalID() {
        return terminalID;
    }
    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }
    public String getTerminalSecret() {
        return terminalSecret;
    }
    public void setTerminalSecret(String terminalSecret) {
        this.terminalSecret = terminalSecret;
    }
    public String getRemoteIP() {
        return remoteIP;
    }
    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }
    public int getRemotePort() {
        return remotePort;
    }
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    public String getRemoteUnit() {
        return remoteUnit;
    }
    public void setRemoteUnit(String remoteUnit) {
        this.remoteUnit = remoteUnit;
    }
    public String getRemoteVersion() {
        return remoteVersion;
    }
    public void setRemoteVersion(String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }
}