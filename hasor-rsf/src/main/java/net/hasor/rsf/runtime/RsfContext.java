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
package net.hasor.rsf.runtime;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.Executor;
import net.hasor.rsf.general.SendLimitPolicy;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * RSF 执行环境。
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfContext {
    /**根据服务名获取服务描述。*/
    public ServiceMetaData getService(String serviceName);
    /**获取{@link Executor}用于安排执行任务。*/
    public Executor getCallExecute(String serviceName);
    /**获取服务上配置有效的过滤器。*/
    public RsfFilter[] getRsfFilters(ServiceMetaData metaData);
    /**获取元信息所描述的服务对象。*/
    public Object getBean(ServiceMetaData metaData);
    /**获取元信息所描述的服务类型。*/
    public Class<?> getBeanType(ServiceMetaData metaData);
    //
    //
    //
    /**获取使用的{@link EventLoopGroup}*/
    public EventLoopGroup getLoopGroup();
    /**获取序列化管理器。*/
    public SerializeFactory getSerializeFactory();
    /**获取当发起请求的时候所使用的RSF协议版本。*/
    public byte getVersion();
    /**获取并发上线策略*/
    public SendLimitPolicy getSendLimitPolicy();
}