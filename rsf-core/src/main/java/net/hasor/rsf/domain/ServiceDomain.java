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
package net.hasor.rsf.domain;
import net.hasor.core.info.MetaDataAdapter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
/**
 * 服务的描述信息，包括了服务的发布和订阅信息。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceDomain<T> extends MetaDataAdapter implements RsfBindInfo<T> {
    private String   bindName      = null;     //服务名
    private String   bindGroup     = "default"; //服务分组
    private String   bindVersion   = "1.0.0";  //服务版本
    private Class<T> bindType      = null;     //服务类型
    private int      clientTimeout = 6000;     //调用超时（毫秒）
    private String   serializeType = null;     //传输序列化类型
    //
    public ServiceDomain(Class<T> bindType) {
        this.bindType = bindType;
    }
    public String getBindID() {
        return RsfRuntimeUtils.bindID(this.bindGroup, this.bindName, this.bindVersion);
    }
    /**获取发布服务的名称。*/
    public String getBindName() {
        return this.bindName;
    }
    /**设置发布服务的名称。*/
    public void setBindName(String bindName) {
        this.bindName = bindName;
    }
    /**获取发布服务的分组名称（默认是：default）。*/
    public String getBindGroup() {
        return this.bindGroup;
    }
    /**设置发布服务的分组名称（默认是：default）。*/
    public void setBindGroup(String bindGroup) {
        this.bindGroup = bindGroup;
    }
    /**获取发布服务的版本号。*/
    public String getBindVersion() {
        return this.bindVersion;
    }
    /**设置发布服务的版本号。*/
    public void setBindVersion(String bindVersion) {
        this.bindVersion = bindVersion;
    }
    /**服务类型*/
    public Class<T> getBindType() {
        return this.bindType;
    }
    /**获取客户端调用服务超时时间。*/
    public int getClientTimeout() {
        return this.clientTimeout;
    }
    /**设置客户端调用服务超时时间。*/
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    /**获取客户端使用的对象序列化格式。*/
    public String getSerializeType() {
        return this.serializeType;
    }
    /**设置客户端使用的对象序列化格式。*/
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    //
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}