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
package net.hasor.rsf.address;
import java.net.URI;
import java.net.URL;
import org.more.util.StringUtils;
/**
 * 某一个服务的地址
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressInfo {
    private String hostAddress = null; //地址
    private int    hostPort    = 8000; //端口
    private String formUnit    = null; //所属单元
    //
    AddressInfo(String formUnit, String hostAddress, int hostPort) {
        this.hostPort = hostPort;
        this.hostAddress = hostAddress;
    }
    //
    /** @return 地址*/
    public String getHostAddress() {
        return this.hostAddress;
    }
    /** @return 端口*/
    public int getHostPort() {
        return this.hostPort;
    }
    /** @return 所属单元*/
    public String getFormUnit() {
        return formUnit;
    }
    /**
     * 两个 Address 可以比较是否相等
     * @param obj 另一个对象
     * @return 返回结果。
     */
    public boolean equals(Object obj) {
        String diffHost = "";
        int diffProt = 0;
        if (obj instanceof AddressInfo) {
            diffHost = ((AddressInfo) obj).getHostAddress();
            diffProt = ((AddressInfo) obj).getHostPort();
        } else if (obj instanceof URL) {
            diffHost = ((URL) obj).getHost();
            diffProt = ((URL) obj).getPort();
        } else if (obj instanceof URI) {
            diffHost = ((URI) obj).getHost();
            diffProt = ((URI) obj).getPort();
        } else {
            return false;
        }
        //
        if (diffProt != this.hostPort) {
            return false;
        }
        return StringUtils.equals(diffHost, this.hostAddress);
    }
    public String toString() {
        return String.format("%s:%s", this.hostAddress, this.hostPort);
    }
}