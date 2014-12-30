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
package net.hasor.rsf.adapter;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.Executor;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 服务上下文，负责提供 RSF 运行环境的支持。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext {
    /**获取{@link Executor}用于安排执行任务。*/
    public abstract Executor getCallExecute(String serviceName);
    /**获取序列化管理器。*/
    public abstract SerializeFactory getSerializeFactory();
    /**获取服务上配置有效的过滤器*/
    public abstract <T> Provider<RsfFilter>[] getFilters(RsfBindInfo<T> metaData);
    /**获取Netty事件处理工具*/
    public abstract EventLoopGroup getLoopGroup();
    //
    /**获取地址管理中心*/
    public abstract AbstracAddressCenter getAddressCenter();
    /**获取服务注册中心*/
    public abstract AbstractBindCenter getBindCenter();
    /**获取连接管理器*/
    public abstract ConnectionManager getConnectionManager();
}