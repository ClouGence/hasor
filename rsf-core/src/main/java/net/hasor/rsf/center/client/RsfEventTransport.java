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
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.domain.Events;
import net.hasor.rsf.domain.ServiceDomain;
/**
 * 负责侦听RSF框架发出的事件，并将事件转发到RsfCenter。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfEventTransport implements EventListener<ServiceDomain<?>> {
    protected Logger          logger         = LoggerFactory.getLogger(getClass());
    private String            hostString     = null;
    private RsfCenterRegister centerRegister = null;
    public RsfEventTransport(RsfContext rsfContext) {
        this.hostString = rsfContext.bindAddress().getHostPort();
        this.centerRegister = rsfContext.getRsfClient().wrapper(RsfCenterRegister.class);
    }
    @Override
    public void onEvent(String event, ServiceDomain<?> eventData) throws Throwable {
        if (eventData == null) {
            return;
        }
        try {
            if (StringUtils.equals(Events.Rsf_ProviderService, event)) {
                //
                PublishInfo info = converTo(eventData);
                String registerID = this.centerRegister.publishService(hostString, info);
                eventData.setCenterID(registerID);
            } else if (StringUtils.equals(Events.Rsf_ConsumerService, event)) {
                //
                PublishInfo info = converTo(eventData);
                String registerID = this.centerRegister.receiveService(hostString, info);
                eventData.setCenterID(registerID);
            } else if (StringUtils.equals(Events.Rsf_DeleteService, event)) {
                //
                this.centerRegister.removeRegister(hostString, eventData.getCenterID());
            }
            logger.error("eventType = {} , process end.", event);
        } catch (Throwable e) {
            this.logger.error(e.getMessage(), e);
        }
    }
    private PublishInfo converTo(ServiceDomain<?> eventData) {
        PublishInfo info = new PublishInfo();
        info.setBindID(eventData.getBindID());
        info.setBindGroup(eventData.getBindGroup());
        info.setBindName(eventData.getBindName());
        info.setBindVersion(eventData.getBindVersion());
        info.setBindType(eventData.getBindType().getName());
        info.setClientTimeout(eventData.getClientTimeout());
        info.setSerializeType(eventData.getSerializeType());
        return info;
    }
}