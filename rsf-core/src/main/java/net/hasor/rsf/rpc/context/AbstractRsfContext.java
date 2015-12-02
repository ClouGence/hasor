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
package net.hasor.rsf.rpc.context;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.hasor.core.EventContext;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.binder.RsfBindCenter;
import net.hasor.rsf.rpc.client.RsfClientChannelManager;
import net.hasor.rsf.rpc.client.RsfClientRequestManager;
import net.hasor.rsf.rpc.event.Events;
import net.hasor.rsf.rpc.manager.ExecutesManager;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.NameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 服务上下文，负责提供 RSF 运行环境的支持。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext {
    protected Logger                logger = LoggerFactory.getLogger(getClass());
    private RsfEnvironment          rsfEnvironment;
    private AddressPool             addressPool;
    private RsfBindCenter           bindCenter;
    private SerializeFactory        serializeFactory;
    private ExecutesManager         executesManager;
    private EventLoopGroup          workLoopGroup;
    private RsfClientRequestManager requestManager;
    private RsfClientChannelManager channelManager;
    //
    protected void initContext(Object context, RsfSettings rsfSettings) throws IOException {
        logger.info("rsfContext init.");
        this.rsfEnvironment = new DefaultRsfEnvironment(context, rsfSettings);
        //
        this.bindCenter = new RsfBindCenter(this);
        this.addressPool = new AddressPool(this.rsfEnvironment);
        this.serializeFactory = SerializeFactory.createFactory(rsfSettings);
        //
        int queueSize = rsfSettings.getQueueMaxSize();
        int minCorePoolSize = rsfSettings.getQueueMinPoolSize();
        int maxCorePoolSize = rsfSettings.getQueueMaxPoolSize();
        long keepAliveTime = rsfSettings.getQueueKeepAliveTime();
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        //
        int workerThread = rsfSettings.getNetworkWorker();
        logger.info("nioEventLoopGroup, workerThread = " + workerThread);
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        //
        this.requestManager = new RsfClientRequestManager(this);
        this.channelManager = new RsfClientChannelManager(this);
        //
    }
    /**序列化反序列化使用的类加载器*/
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    /**获取配置*/
    @Override
    public RsfSettings getSettings() {
        return this.rsfEnvironment.getSettings();
    }
    /** @return 获取服务注册中心*/
    @Override
    public RsfBindCenter getBindCenter() {
        return this.bindCenter;
    }
    /** @return 获取地址管理中心*/
    public AddressPool getAddressPool() {
        return this.addressPool;
    }
    /** @return 获取请求管理中心*/
    public RsfClientRequestManager getRequestManager() {
        return this.requestManager;
    }
    /** @return 获取网络连接管理中心*/
    public RsfClientChannelManager getChannelManager() {
        return this.channelManager;
    }
    /** @return 获取序列化管理器。*/
    public SerializeFactory getSerializeFactory() {
        return this.serializeFactory;
    }
    /** @return 获取事件管理器。*/
    public EventContext getEventContext() {
        return this.rsfEnvironment.getEventContext();
    }
    /**
     * 获取{@link Executor}用于安排执行任务。
     * @param serviceName 服务名
     * @return 返回Executor
     */
    public Executor getCallExecute(byte[] serviceUniqueName) {
        return this.executesManager.getExecute(serviceUniqueName);
    }
    /** @return 获取Netty事件处理工具*/
    public EventLoopGroup getWorkLoopGroup() {
        return this.workLoopGroup;
    }
    /**停止工作*/
    @Override
    public void shutdown() {
        this.getEventContext().fireSyncEvent(Events.Shutdown, this);
        this.workLoopGroup.shutdownGracefully();
    }
    /**获取客户端*/
    @Override
    public RsfClient getRsfClient() {
        return this.requestManager.getClientWrappe();
    }
    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     * @return 服务对象
     */
    @Override
    public <T> T getBean(RsfBindInfo<T> bindInfo) {
        Provider<T> provider = getProvider(bindInfo);
        return (provider != null) ? provider.get() : null;
    }
    @Override
    public <T> Provider<T> getProvider(RsfBindInfo<T> bindInfo) {
        return this.getBindCenter().getProvider(bindInfo);
    }
}