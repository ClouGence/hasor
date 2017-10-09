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
package net.hasor.registry.server.pushing;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.server.domain.LogUtils;
import net.hasor.registry.server.manager.ServerSettings;
import net.hasor.registry.trace.TraceUtil;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 推送服务触发器
 * @version : 2016年3月1日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class PushQueue implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private LinkedBlockingQueue<PushEvent>         dataQueue;
    private ArrayList<Thread>                      threadPushQueue;
    private Map<RsfCenterEventEnum, PushProcessor> processorMapping;
    @Inject
    private RsfContext                             rsfContext;
    @Inject
    private ServerSettings                         rsfCenterCfg;
    //
    @Init
    public void init() {
        AppContext app = this.rsfContext.getAppContext();
        this.processorMapping = new HashMap<RsfCenterEventEnum, PushProcessor>();
        for (RsfCenterEventEnum eventType : RsfCenterEventEnum.values()) {
            PushProcessor processor = app.getInstance(eventType.getProcessorType());
            this.processorMapping.put(eventType, processor);
            logger.info("pushQueue processor mapping {} -> {}", eventType.forCenterEvent(), eventType.getProcessorType());
        }
        //
        this.dataQueue = new LinkedBlockingQueue<PushEvent>();
        this.threadPushQueue = new ArrayList<Thread>();
        int threadSize = rsfCenterCfg.getThreadSize();
        for (int i = 1; i <= threadSize; i++) {
            Thread pushQueue = new Thread(this);
            pushQueue.setDaemon(true);
            pushQueue.setName("Rsf-Center-PushQueue-" + i);
            pushQueue.setContextClassLoader(this.rsfContext.getClassLoader());
            pushQueue.start();
            this.threadPushQueue.add(pushQueue);
        }
        logger.info("PushQueue Thread start.");
    }
    public void run() {
        while (true) {
            try {
                PushEvent pushEvent = null;
                while ((pushEvent = this.dataQueue.take()) != null) {
                    doPush(pushEvent);
                }
            } catch (Throwable e) {
                logger.error(LogUtils.create("ERROR_300_00004")//
                        .addLog("traceID", TraceUtil.getTraceID())//
                        .logException(e).toJson());
            }
        }
    }
    //
    // - 立刻执行消息推送,返回推送失败的地址列表。
    private List<InterAddress> doPush(PushEvent pushEvent) {
        PushProcessor pushProcessor = this.processorMapping.get(pushEvent.getPushEventType());
        if (pushProcessor != null) {
            return pushProcessor.doProcessor(pushEvent);
        } else {
            logger.error(LogUtils.create("ERROR_300_00005")//
                    .addLog("traceID", TraceUtil.getTraceID())//
                    .addLog("pushEventType", pushEvent.getPushEventType().name())//
                    .toJson());
        }
        return pushEvent.getTarget();
    }
    // - 将消息推送交给推送线程,执行异步推送。
    public boolean doPushEvent(PushEvent eventData) {
        if (this.dataQueue.size() > this.rsfCenterCfg.getQueueMaxSize()) {
            try {
                Thread.sleep(this.rsfCenterCfg.getSleepTime());
            } catch (Exception e) {
                logger.error(LogUtils.create("ERROR_300_00004")//
                        .addLog("traceID", TraceUtil.getTraceID())//
                        .logException(e).toJson());
            }
            if (this.dataQueue.size() > this.rsfCenterCfg.getQueueMaxSize()) {
                return false;//资源还是紧张,返回 失败
            }
        }
        //
        this.dataQueue.offer(eventData);//资源不在紧张，加入到队列中。
        return true;
    }
}