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
package net.hasor.rsf.center.server.domain.entity;
import java.util.Date;
import net.hasor.rsf.address.InterAddress;
/**
 * 应用
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerDO {
    private long         id;          //id
    private InterAddress rsfUrl;      //center的服务地址
    private String       bindAddress; //center的服务地址
    private int          bindPort;    //center的服务地址
    private String       unit;        //所属单元
    private String       version;     //RSF版本
    private Date         beat;        //服务器心跳时间
    private StatusEnum   status;      //服务状态
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public InterAddress getRsfUrl() {
        return rsfUrl;
    }
    public void setRsfUrl(InterAddress rsfUrl) {
        this.rsfUrl = rsfUrl;
    }
    public String getBindAddress() {
        return bindAddress;
    }
    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }
    public int getBindPort() {
        return bindPort;
    }
    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public Date getBeat() {
        return beat;
    }
    public void setBeat(Date beat) {
        this.beat = beat;
    }
    public StatusEnum getStatus() {
        return status;
    }
    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}