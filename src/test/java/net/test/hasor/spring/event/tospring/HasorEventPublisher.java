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
package net.test.hasor.spring.event.tospring;
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
/**
 * 使用Hasor的方式发送事件，并使用Spring的方式接收事件。
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorEventPublisher {
    @Inject
    private AppContext appContext;
    //
    //
    /**发送事件到Spring容器*/
    public void publishEvent() {
        this.appContext.getEnvironment().getEventContext().fireSyncEvent("MyEvent", this);
    }
}