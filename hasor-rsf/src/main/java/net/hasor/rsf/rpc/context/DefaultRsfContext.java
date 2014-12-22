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
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.adapter.AbstracAddressCenter;
import net.hasor.rsf.adapter.AbstractBindCenter;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.remoting.address.AddressCenter;
import net.hasor.rsf.remoting.binder.BindCenter;
import net.hasor.rsf.rpc.executes.ExecutesManager;
import net.hasor.rsf.rpc.executes.NameThreadFactory;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultRsfContext extends AbstractRsfContext {
    private AbstractBindCenter   bindCenter       = null;
    private AbstracAddressCenter addressCenter    = null;
    //
    private SerializeFactory     serializeFactory = null;
    private ExecutesManager      executesManager  = null;
    private EventLoopGroup       loopGroup        = null;
    private RsfSettings          rsfSettings      = null;
    //
    public DefaultRsfContext(Settings settings) throws IOException {
        this.rsfSettings = new RsfSettingsImpl(settings);
        this.bindCenter = new BindCenter(this);
        this.addressCenter = new AddressCenter();
        //
        this.serializeFactory = SerializeFactory.createFactory(this.rsfSettings);
        //
        int queueSize = this.rsfSettings.getQueueMaxSize();
        int minCorePoolSize = this.rsfSettings.getQueueMinPoolSize();
        int maxCorePoolSize = this.rsfSettings.getQueueMaxPoolSize();
        long keepAliveTime = this.rsfSettings.getQueueKeepAliveTime();
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        //
        int workerThread = this.rsfSettings.getNetworkWorker();
        this.loopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        //
    }
    //
    /**获取使用的{@link EventLoopGroup}*/
    public EventLoopGroup getLoopGroup() {
        return this.loopGroup;
    }
    /**获取配置*/
    public RsfSettings getSettings() {
        return this.rsfSettings;
    }
    /**获取{@link Executor}用于安排执行任务。*/
    public Executor getCallExecute(String serviceName) {
        return this.executesManager.getExecute(serviceName);
    }
    /**获取序列化管理器。*/
    public SerializeFactory getSerializeFactory() {
        return this.serializeFactory;
    }
    /**获取注册中心。*/
    public AbstractBindCenter getBindCenter() {
        return this.bindCenter;
    }
    /**获取注册中心。*/
    public AbstracAddressCenter getAddressCenter() {
        return this.addressCenter;
    }
    //
    /**获取客户端*/
    public RsfClient getRsfClient() {
        // TODO Auto-generated method stub
        return null;
    }
    /**获取元信息所描述的服务对象。*/
    public <T> T getBean(RsfBindInfo<T> metaData) {
        // TODO Auto-generated method stub
        return null;
    }
    /**获取服务上配置有效的过滤器*/
    public <T> Provider<RsfFilter>[] getFilters(RsfBindInfo<T> metaData) {
        // TODO Auto-generated method stub
        return null;
    }
}