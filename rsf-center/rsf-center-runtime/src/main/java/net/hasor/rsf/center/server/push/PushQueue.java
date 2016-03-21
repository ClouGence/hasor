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
package net.hasor.rsf.center.server.push;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.center.server.domain.RsfCenterEvent;
import net.hasor.rsf.center.server.manager.ConsumerServiceManager;
/**
 * 推送服务触发器
 * @version : 2016年3月1日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Event(RsfCenterEvent.ServicesPull_Event)
public class PushQueue implements EventListener<String>, Runnable {
    protected Logger                      logger = LoggerFactory.getLogger(getClass());
    private ConcurrentSkipListSet<String> dataSet;
    private ReadWriteLock                 lock;
    private Thread                        threadPushQueue;
    @Inject
    private RsfContext                    rsfContext;
    @Inject
    private RsfCenterCfg                  rsfCenterCfg;
    @Inject
    private ConsumerServiceManager        consumerServiceManager;
    //
    @Init
    public void init() {
        this.dataSet = new ConcurrentSkipListSet<String>();
        this.lock = new ReentrantReadWriteLock();
        this.threadPushQueue = new Thread(this);
        this.threadPushQueue.setDaemon(true);
        this.threadPushQueue.setName("Rsf-Center-PushQueue");
        this.threadPushQueue.start();
        logger.info("PushQueue Thread start.");
    }
    //
    // - 监听到服务注册订阅消息，将该服务加入到推送列表返回。该方法不做推送，推送交给推送线程去做。
    @Override
    public void onEvent(String event, String serviceID) throws Throwable {
        this.lock.readLock().lock();
        this.dataSet.add(serviceID);
        this.lock.readLock().unlock();
        //
        if (this.dataSet.size() > this.rsfCenterCfg.getPushQueueMaxSize()) {
            //推送线程定时运行一次，在两次运行期间如果等待推送的服务数达到了上限，那么也进行推送。
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
    //
    private synchronized void waitData() throws InterruptedException {
        final long waitTime = this.rsfCenterCfg.getPushSleepTime();
        if (this.dataSet.isEmpty() == false) {
            return;
        } else {
            for (;;) {
                this.wait(waitTime);
                if (this.dataSet.isEmpty() == false) {
                    return;
                }
            }
        }
    }
    public void run() {
        while (true) {
            try {
                this.waitData();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            //
            this.lock.writeLock().lock();
            String[] serviceIDs = this.dataSet.toArray(new String[this.dataSet.size()]);
            this.dataSet.clear();
            this.lock.writeLock().unlock();;
            //
            for (String serviceID : serviceIDs) {
                this.pushData(serviceID);
            }
        }
    }
    private void pushData(String serviceID) {
        //
        logger.info("PushService {} -> {}", serviceID);
        this.consumerServiceManager.newProviderToPush(serviceID);
    }
}