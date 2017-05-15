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
package net.hasor.registry.client;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.context.ContextStartListener;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.hasor.rsf.domain.RsfEvent.*;
/**
 * 负责侦听RSF框架发出的事件，并将事件转发到RsfCenter。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfEventTransport implements EventListener<Object>, ContextStartListener {
    protected Logger                logger        = LoggerFactory.getLogger(getClass());
    private   RegistryClientManager centerManager = null;
    //
    @Override
    public void doStart(AppContext appContext) {
        //
    }
    @Override
    public void doStartCompleted(AppContext appContext) {
        // .
        RsfContext rsfContext = appContext.getInstance(RsfContext.class);
        this.centerManager = new RegistryClientManager(rsfContext);
        this.centerManager.run(null);
        this.logger.info("start the registration service processed.");
    }
    @Override
    public void onEvent(String event, Object eventData) throws Throwable {
        if (eventData == null || centerManager == null) {
            return;/* 在没有正式启动之前，所有消息都丢弃。然后在 doStartCompleted 时候统一做一次收集 */
        }
        this.logger.info("rsfEventTransport -> eventType = {}.", event);
        if (Rsf_Online.equals(event)) {
            this.centerManager.online();
            return;
        }
        if (Rsf_Offline.equals(event)) {
            this.centerManager.offline();
            return;
        }
        //
        RsfBindInfo<?> domain = (RsfBindInfo<?>) eventData;
        try {
            if (Rsf_ProviderService.equals(event)) {
                this.centerManager.onlineService(domain);
            } else if (Rsf_ConsumerService.equals(event)) {
                this.centerManager.onlineService(domain);
            } else if (Rsf_DeleteService.equals(event)) {
                this.centerManager.offlineService(domain);
            }
            //
            this.logger.info("eventType = {} ,serviceID ={} , events have been processed.", event, domain.getBindID());
        } catch (Throwable e) {
            this.logger.error("eventType = {} ,serviceID ={} , process error -> {}", event, domain.getBindID(), e.getMessage(), e);
        }
    }
}