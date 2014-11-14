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
package net.hasor.rsf.runtime.context;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.Executor;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * RSF 服务器控制接口
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfContext {
    public EventLoopGroup getLoopGroup();
    /***/
    public ServiceMetaData getService(String serviceName);
    /**获取用于执行远程服务调用的 Executor。*/
    public Executor getCallExecute(String serviceName);
    //
    public SerializeFactory getSerializeFactory();
    //
    public Object getBean(ServiceMetaData metaData);
    public Class<?> getBeanType(ServiceMetaData metaData);
    // 
    public RsfFilter[] getRsfFilters(ServiceMetaData metaData);
    //
    public byte getProtocolVersion();
}