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
import java.util.concurrent.Executor;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.binder.RsfBindCenter;
import net.hasor.rsf.manager.ExecutesManager;
import net.hasor.rsf.rpc.client.RsfRequestManager;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.NameThreadFactory;
import org.more.logger.LoggerHelper;
/**
 * 服务上下文，负责提供 RSF 运行环境的支持。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext {
    private RsfSettings      rsfSettings;
    private AddressPool      addressPool;
    private RsfBindCenter    bindCenter;
    private SerializeFactory serializeFactory;
    private ExecutesManager  executesManager;
    private EventLoopGroup   loopGroup;
    //
    //
    //
    protected void initContext(RsfSettings rsfSettings) {
        LoggerHelper.logConfig("rsfContext init.");
        this.rsfSettings = rsfSettings;
        //
        this.bindCenter = new RsfBindCenter(this);
        this.addressPool = new AddressPool(rsfSettings.getUnitName(), bindCenter, rsfSettings);
        this.serializeFactory = SerializeFactory.createFactory(this.rsfSettings);
        //
        int queueSize = this.rsfSettings.getQueueMaxSize();
        int minCorePoolSize = this.rsfSettings.getQueueMinPoolSize();
        int maxCorePoolSize = this.rsfSettings.getQueueMaxPoolSize();
        long keepAliveTime = this.rsfSettings.getQueueKeepAliveTime();
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        //
        int workerThread = this.rsfSettings.getNetworkWorker();
        LoggerHelper.logConfig("nioEventLoopGroup, workerThread = " + workerThread);
        this.loopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
    }
    /**序列化反序列化使用的类加载器*/
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    /**获取配置*/
    public RsfSettings getSettings() {
        return this.rsfSettings;
    }
    /** @return 获取服务注册中心*/
    public RsfBindCenter getBindCenter() {
        return this.bindCenter;
    }
    public AddressPool getAddressPool() {
        return this.addressPool;
    }
    /** @return 获取序列化管理器。*/
    public SerializeFactory getSerializeFactory() {
        return this.serializeFactory;
    }
    /**
     * 获取{@link Executor}用于安排执行任务。
     * @param serviceName 服务名
     * @return 返回Executor
     */
    public Executor getCallExecute(String serviceName) {
        return this.executesManager.getExecute(serviceName);
    }
    /** @return 获取Netty事件处理工具*/
    public EventLoopGroup getLoopGroup() {
        return this.loopGroup;
    }
    /**停止工作*/
    public void shutdown() {
        this.loopGroup.shutdownGracefully();
    }
    /**获取客户端*/
    public RsfClient getRsfClient() {
        return new RsfClientFacade(this);
    }
    /**
     * 获取元信息所描述的服务对象
     * @param bindInfo 元信息所描述对象
     * @return 服务对象
     */
    public <T> T getBean(RsfBindInfo<T> bindInfo) {
        Provider<T> provider = getProvider(bindInfo);
        return (provider != null) ? provider.get() : null;
    }
    @Override
    public <T> Provider<T> getProvider(RsfBindInfo<T> bindInfo) {
        return this.getBindCenter().getProvider(bindInfo);
    }
    /** @return 获取请求管理中心*/
    public RsfRequestManager getRequestManager() {
        return null;s
    }
}