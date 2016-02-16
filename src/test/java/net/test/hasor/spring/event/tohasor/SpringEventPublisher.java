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
package net.test.hasor.spring.event.tohasor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import net.hasor.spring.event.AsyncHasorEvent;
import net.hasor.spring.event.SyncHasorEvent;
/**
 * 使用Spring的方式发送事件，并使用Hasor的方式接收事件。
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    //
    //
    /**发送事件到Hasor容器(同步事件)*/
    public void publishSyncEvent() {
        this.applicationEventPublisher.publishEvent(new SyncHasorEvent("SpringEventBean", this));
    }
    /**发送事件到Hasor容器(异步事件)*/
    public void publishAsyncEvent() {
        this.applicationEventPublisher.publishEvent(new AsyncHasorEvent("SpringEventBean", this));
    }
}