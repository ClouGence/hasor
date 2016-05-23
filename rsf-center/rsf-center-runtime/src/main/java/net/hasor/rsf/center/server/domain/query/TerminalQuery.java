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
package net.hasor.rsf.center.server.domain.query;
import java.util.Date;
import org.more.bizcommon.Paginator;
import net.hasor.rsf.center.server.domain.entity.StatusEnum;
import net.hasor.rsf.center.server.domain.entity.TerminalTypeEnum;
/**
 * 
 * @version : 2016年5月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class TerminalQuery extends Paginator {
    private String           serviceID;
    private String           unit;     //所处单元
    private TerminalTypeEnum persona;  // 身份(C:订阅者、P:提供者)
    private StatusEnum       status;   //服务状态
    private Date             beatTime;
    //
    public String getServiceID() {
        return serviceID;
    }
    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public TerminalTypeEnum getPersona() {
        return persona;
    }
    public void setPersona(TerminalTypeEnum persona) {
        this.persona = persona;
    }
    public StatusEnum getStatus() {
        return status;
    }
    public void setStatus(StatusEnum status) {
        this.status = status;
    }
    public Date getBeatTime() {
        return beatTime;
    }
    public void setBeatTime(Date beatTime) {
        this.beatTime = beatTime;
    }
}