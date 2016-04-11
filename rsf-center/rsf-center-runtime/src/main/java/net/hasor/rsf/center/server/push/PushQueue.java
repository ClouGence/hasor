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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
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
import net.hasor.rsf.center.server.push.share.SharePushManager;
import net.hasor.rsf.center.server.utils.JsonUtils;
/**
 * 推送服务触发器
 * @version : 2016年3月1日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Event(RsfCenterEvent.PushEvent)
public class PushQueue implements Runnable, EventListener<PushEvent> {
    protected Logger                               logger = LoggerFactory.getLogger(getClass());
    private LinkedBlockingQueue<PushEvent>         dataQueue;
    private ArrayList<Thread>                      threadPushQueue;
    private Map<RsfCenterEventEnum, PushProcessor> processorMapping;
    @Inject
    private RsfContext                             rsfContext;
    @Inject
    private RsfCenterCfg                           rsfCenterCfg;
    @Inject
    private SharePushManager                       sharePushManager;
    //
    @Init
    public void init() {
        AppContext app = this.rsfContext.getAppContext();
        this.processorMapping = new HashMap<RsfCenterEventEnum, PushProcessor>();
        for (RsfCenterEventEnum eventType : RsfCenterEventEnum.values()) {
            PushProcessor processor = app.getInstance(eventType.getProcessorType());
            this.processorMapping.put(eventType, processor);
        }
        logger.info("pushQueue processor mapping ->{}", JsonUtils.toJsonString(this.processorMapping.keySet()));
        //
        this.dataQueue = new LinkedBlockingQueue<PushEvent>();
        this.threadPushQueue = new ArrayList<Thread>();
        for (int i = 1; i <= 3; i++) {
            Thread pushQueue = this.createPushThread(i);
            pushQueue.start();
            this.threadPushQueue.add(pushQueue);
        }
        logger.info("PushQueue Thread start.");
    }
    /**创建推送线程*/
    protected Thread createPushThread(int index) {
        Thread threadPushQueue = new Thread(this);
        threadPushQueue.setDaemon(true);
        threadPushQueue.setName("Rsf-Center-PushQueue-" + index);
        return threadPushQueue;
    }
    //
    // - 收到推送消息。该方法不做推送，推送交给推送线程去做。
    public void onEvent(String event, PushEvent eventData) throws Throwable {
        if (this.dataQueue.size() > this.rsfCenterCfg.getPushQueueMaxSize()) {
            try {
                Thread.sleep(this.rsfCenterCfg.getPushSleepTime());
            } catch (Exception e) {
                logger.error("pushQueue - " + e.getMessage(), e);
            }
            if (this.dataQueue.size() > this.rsfCenterCfg.getPushQueueMaxSize()) {
                this.sharePushManager.shareEvent(eventData); //需要转发到进群其它机器中处理
                return;
            }
        }
        //
        this.dataQueue.offer(eventData);//资源不在紧张，加入到队列中。
    }
    //
    public void run() {
        while (true) {
            try {
                doPushQueue();
            } catch (Throwable e) {
                logger.error("doPushQueue - " + e.getMessage(), e);
            }
        }
    }
    private void doPushQueue() throws InterruptedException {
        PushEvent pushEvent = null;
        while ((pushEvent = this.dataQueue.take()) != null) {
            PushProcessor pushProcessor = this.processorMapping.get(pushEvent.getPushEventType());
            if (pushProcessor != null) {
                logger.info("pushEvent {} -> {}", pushEvent.getPushEventType().name(), pushEvent);
                pushProcessor.doProcessor(pushEvent);
            } else {
                logger.error("pushEvent pushProcessor is null. {} -> {}", pushEvent.getPushEventType().name(), pushEvent);
            }
        }
    }
}