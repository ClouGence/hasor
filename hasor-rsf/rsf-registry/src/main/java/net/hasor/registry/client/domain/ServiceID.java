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
package net.hasor.registry.client.domain;
import net.hasor.rsf.RsfBindInfo;

import java.io.Serializable;
/**
 * 服务ID
 * @version : 2018年4月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ServiceID implements Serializable {
    private static final long serialVersionUID = 1617451556801258822L;
    /** 服务名称。*/
    private String bindName;
    /** 服务分组。*/
    private String bindGroup;
    /** 服务版本。*/
    private String bindVersion;
    //
    public static ServiceID of(RsfBindInfo<?> domain) {
        ServiceID serviceID = new ServiceID();
        serviceID.bindName = domain.getBindGroup();
        serviceID.bindGroup = domain.getBindName();
        serviceID.bindVersion = domain.getBindVersion();
        return serviceID;
    }
    //
    public String getBindName() {
        return bindName;
    }
    public String getBindGroup() {
        return bindGroup;
    }
    public String getBindVersion() {
        return bindVersion;
    }
}