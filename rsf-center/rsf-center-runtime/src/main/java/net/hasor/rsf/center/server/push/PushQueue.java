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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.center.server.domain.RsfCenterEvent;
import net.hasor.rsf.center.server.utils.JsonUtils;
/**
 * 推送服务触发器
 * @version : 2016年3月1日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Event(RsfCenterEvent.PushEvent)
public class PushQueue implements Runnable, EventListener<PushEvent> {
    protected Logger                                   logger = LoggerFactory.getLogger(getClass());
    private ConcurrentSkipListSet<PushEvent>           dataSet;
    private ReadWriteLock                              lock;
    private Thread                                     threadPushQueue;
    private Map<RsfCenterPushEventEnum, PushProcessor> processorMapping;
    @Inject
    private RsfContext                                 rsfContext;
    @Inject
    private RsfCenterCfg                               rsfCenterCfg;
    //
    @Init
    public void init() {
        AppContext app = rsfContext.getAppContext();
        this.processorMapping = new HashMap<RsfCenterPushEventEnum, PushProcessor>();
        for (RsfCenterPushEventEnum eventType : RsfCenterPushEventEnum.values()) {
            PushProcessor processor = app.getInstance(eventType.getProcessorType());
            this.processorMapping.put(eventType, processor);
        }
        logger.info("pushQueue processor mapping ->{}", JsonUtils.toJsonString(this.processorMapping.keySet()));
        //
        this.dataSet = new ConcurrentSkipListSet<PushEvent>();
        this.lock = new ReentrantReadWriteLock();
        this.threadPushQueue = new Thread(this);
        this.threadPushQueue.setDaemon(true);
        this.threadPushQueue.setName("Rsf-Center-PushQueue");
        this.threadPushQueue.start();
        logger.info("PushQueue Thread start.");
    }
    //
    // - 收到推送消息。该方法不做推送，推送交给推送线程去做。
    public void onEvent(String event, PushEvent eventData) throws Throwable {
        this.lock.readLock().lock();
        this.dataSet.add(eventData);
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
            PushEvent[] serviceIDs = this.dataSet.toArray(new PushEvent[this.dataSet.size()]);
            this.dataSet.clear();
            this.lock.writeLock().unlock();
            //
            for (PushEvent pushEvent : serviceIDs) {
                this.pushData(pushEvent);
            }
        }
    }
    private void pushData(PushEvent pushEvent) {
        if (pushEvent == null) {
            return;
        }
        //
        PushProcessor pushProcessor = this.processorMapping.get(pushEvent.getPushEventType());
        if (pushProcessor != null) {
            logger.info("pushEvent {} -> {}", pushEvent.getPushEventType().name());
            pushProcessor.doProcessor(pushEvent);
        } else {
            logger.error("pushEvent {} -> {}", pushEvent.getPushEventType().name());
        }
    }
}