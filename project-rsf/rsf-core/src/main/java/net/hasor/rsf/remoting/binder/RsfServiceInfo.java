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
package net.hasor.rsf.remoting.binder;
import java.lang.annotation.Annotation;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import org.more.util.StringUtils;
/**
 * @version : 2014年11月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfServiceInfo implements RsfService {
    private String serviceGroup   = null;
    private String serviceName    = null;
    private String serviceVersion = null;
    private int    clientTimeout  = 0;
    private String serializeType  = null;
    //
    public RsfServiceInfo(RsfContext rsfContext, Class<?> serviceType) {
        RsfSettings rsfSettings = rsfContext.getSettings();
        serviceGroup = rsfSettings.getDefaultGroup();
        serviceName = serviceType.getName();
        serviceVersion = rsfSettings.getDefaultVersion();
        //覆盖
        RsfService serviceInfo = serviceType.getAnnotation(RsfService.class);
        if (serviceInfo != null) {
            if (StringUtils.isBlank(serviceInfo.group()) == false)
                serviceGroup = serviceInfo.group();
            if (StringUtils.isBlank(serviceInfo.name()) == false)
                serviceName = serviceInfo.name();
            if (StringUtils.isBlank(serviceInfo.version()) == false)
                serviceVersion = serviceInfo.version();
        }
        //
        RsfBindInfo<?> bindInfo = rsfContext.getBindCenter().getService(serviceGroup, serviceName, serviceVersion);
        clientTimeout = bindInfo.getClientTimeout();
        serializeType = bindInfo.getSerializeType();
    }
    //
    @Override
    public Class<? extends Annotation> annotationType() {
        return RsfServiceInfo.class;
    }
    @Override
    public String name() {
        return this.serviceName;
    }
    @Override
    public String group() {
        return this.serviceGroup;
    }
    @Override
    public String version() {
        return this.serviceVersion;
    }
    @Override
    public int clientTimeout() {
        return this.clientTimeout;
    }
    @Override
    public String serializeType() {
        return this.serializeType;
    }
}