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
package net.hasor.rsf.center.domain.form.apis;
import net.hasor.plugins.restful.api.ReqParam;
/**
 * @version : 2015年6月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class OnLineForm {
    @ReqParam("Terminal_HostName")
    private String hostName;
    @ReqParam("Terminal_HostPort")
    private int    hostPort;
    @ReqParam("Terminal_HostUnit")
    private String hostUnit;
    @ReqParam("Terminal_Version")
    private String version;
    //
    public String getHostName() {
        return hostName;
    }
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    public int getHostPort() {
        return hostPort;
    }
    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }
    public String getHostUnit() {
        return hostUnit;
    }
    public void setHostUnit(String hostUnit) {
        this.hostUnit = hostUnit;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
}