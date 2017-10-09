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
package net.hasor.rsf.rpc.net;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 维护RSF同其它RSF的连接。
 * tips：主要数据结构为 hostPort 和 RsfChannel 的映射关系。另外还维护了一个 别名关系，通过别名关系实现双向通信上的连接复用问题。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class LinkPool {
    protected     Logger        logger = LoggerFactory.getLogger(getClass());
    private final AtomicBoolean inited = new AtomicBoolean(false);
    private final RsfEnvironment                                 environment;
    private final ConcurrentMap<String, BasicFuture<RsfChannel>> channelMap;
    //private final ConcurrentMap<String, String>                  channelAlias;
    //
    public LinkPool(RsfEnvironment environment) {
        this.environment = environment;
        this.channelMap = new ConcurrentHashMap<String, BasicFuture<RsfChannel>>();
        //this.channelAlias = new ConcurrentHashMap<String, String>();
    }
    //
    /** 初始化连接池。*/
    public void initPool() {
        if (this.inited.compareAndSet(false, true)) {
            this.logger.info("init LinkPool.");
        }
    }
    /** 销毁连接池。*/
    public void destroyPool() {
        if (this.inited.compareAndSet(true, false)) {
            this.logger.info("destroy LinkPool.");
            for (BasicFuture<RsfChannel> future : channelMap.values()) {
                if (future == null)
                    continue;
                if (!future.isDone()) {
                    future.failed(new IllegalStateException("the pool destroy."));
                } else {
                    try {
                        future.get().close();
                    } catch (Exception e) { /**/ }
                }
            }
        }
    }
    //
    //
    public synchronized BasicFuture<RsfChannel> preConnection(String hostPortKey) {
        if (!this.inited.get()) {
            throw new IllegalStateException("LinkPool not inited.");
        }
        //
        //创建一个Future，并开始计时，在规定时间内没有连接成功则反馈失败（目的防止其它线程在Future的get上被锁死）
        final BasicFuture<RsfChannel> channel = new BasicFuture<RsfChannel>();
        BasicFuture<RsfChannel> oldFuture = this.channelMap.putIfAbsent(hostPortKey, channel);
        if (oldFuture != null) {
            return oldFuture;
        }
        //
        int timeout = this.environment.getSettings().getConnectTimeout();
        this.environment.atTime(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (!channel.isDone()) {
                    channel.failed(new RsfException(ProtocolStatus.Timeout, "connection not ready within the given time."));
                }
            }
        }, timeout);
        return channel;
    }
    public void closeConnection(String hostPortKey) {
        BasicFuture<RsfChannel> future = this.findChannel(hostPortKey);
        if (future == null) {
            return;
        }
        this.channelMap.remove(hostPortKey);
        if (future.isDone()) {
            try {
                future.get().close();
            } catch (Exception e) { /**/ }
        }
    }
    public void mappingTo(RsfChannel rsfChannel, String hostPort) {
        //        String address = rsfChannel.getTarget().getHostPort();
        //        BasicFuture<RsfChannel> channel = this.findChannel(address);
        //        if (channel == null || !channel.isDone()) {
        //            return;
        //        }
        //        this.channelAlias.put(hostPort, address);
    }
    //
    /**
     * 查找连接
     * @param hostPortKey  liek this 127.0.0.1:2180
     */
    public BasicFuture<RsfChannel> findChannel(String hostPortKey) {
        return this.channelMap.get(hostPortKey);
    }
}