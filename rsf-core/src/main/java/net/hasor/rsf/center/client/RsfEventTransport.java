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
package net.hasor.rsf.center.client;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.EventListener;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.RsfEvent;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 负责侦听RSF框架发出的事件，并将事件转发到RsfCenter。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfEventTransport implements EventListener<Object> {
    protected Logger             logger    = LoggerFactory.getLogger(RsfConstants.RsfCenter_Logger);
    private RsfCenterBeatManager beatTimer = null;
    public RsfEventTransport(RsfContext rsfContext) {
        this.beatTimer = new RsfCenterBeatManager(rsfContext);
    }
    //
    @Override
    public void onEvent(String event, Object eventData) throws Throwable {
        if (eventData == null) {
            return;
        }
        if (StringUtils.equals(RsfEvent.Rsf_Started, event)) {
            this.beatTimer.run(null);//启动的时候调用一次，目的是进行服务注册
            this.logger.info("eventType = {} , start the registration service processed.", event);
            return;
        }
        //
        ServiceDomain<?> domain = (ServiceDomain<?>) eventData;
        try {
            if (StringUtils.equals(RsfEvent.Rsf_ProviderService, event)) {
                this.beatTimer.newService(domain, RsfEvent.Rsf_ProviderService);
                //
            } else if (StringUtils.equals(RsfEvent.Rsf_ConsumerService, event)) {
                this.beatTimer.newService(domain, RsfEvent.Rsf_ConsumerService);
                //
            } else if (StringUtils.equals(RsfEvent.Rsf_DeleteService, event)) {
                this.beatTimer.deleteService(domain);
                //
            } else if (StringUtils.equals(RsfEvent.Rsf_Online, event)) {
                this.beatTimer.online();
                //
            } else if (StringUtils.equals(RsfEvent.Rsf_Offline, event)) {
                this.beatTimer.offline();
                //
            }
            //
            this.logger.info("eventType = {} ,serviceID ={} , events have been processed.", event, domain.getBindID());
        } catch (Throwable e) {
            this.logger.error("eventType = {} ,serviceID ={} , process error -> {}", event, domain.getBindID(), e.getMessage(), e);
        }
    }
}