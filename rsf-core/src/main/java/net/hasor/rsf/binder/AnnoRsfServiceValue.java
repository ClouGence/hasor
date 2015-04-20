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
package net.hasor.rsf.binder;
import java.lang.annotation.Annotation;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.RsfSettings;
import org.more.util.StringUtils;
/**
 * @version : 2014年11月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class AnnoRsfServiceValue implements RsfService {
    private String serviceGroup   = null;
    private String serviceName    = null;
    private String serviceVersion = null;
    private int    clientTimeout  = 0;
    private String serializeType  = null;
    //
    public AnnoRsfServiceValue(RsfSettings rsfSettings, Class<?> serviceType) {
        //1.真实值
        RsfService serviceInfo = serviceType.getAnnotation(RsfService.class);
        if (serviceInfo != null) {
            if (StringUtils.isBlank(serviceInfo.group()) == false) {
                this.serviceGroup = serviceInfo.group();
            }
            if (StringUtils.isBlank(serviceInfo.name()) == false) {
                this.serviceName = serviceInfo.name();
            }
            if (StringUtils.isBlank(serviceInfo.version()) == false) {
                this.serviceVersion = serviceInfo.version();
            }
            if (StringUtils.isBlank(serviceInfo.serializeType()) == false) {
                this.serializeType = serviceInfo.serializeType();
            }
            if (serviceInfo.clientTimeout() > 0) {
                this.clientTimeout = serviceInfo.clientTimeout();
            }
        }
        //2.默认值
        if (StringUtils.isBlank(this.serviceGroup)) {
            this.serviceGroup = rsfSettings.getDefaultGroup();
        }
        if (StringUtils.isBlank(this.serviceName)) {
            this.serviceName = serviceType.getName();
        }
        if (StringUtils.isBlank(serviceVersion)) {
            this.serviceVersion = rsfSettings.getDefaultVersion();
        }
        if (StringUtils.isBlank(this.serializeType)) {
            this.serializeType = rsfSettings.getDefaultSerializeType();
        }
        if (this.clientTimeout < 1) {
            this.clientTimeout = rsfSettings.getDefaultTimeout();
        }
    }
    //
    @Override
    public Class<? extends Annotation> annotationType() {
        return AnnoRsfServiceValue.class;
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