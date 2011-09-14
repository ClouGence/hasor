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
package org.more.services.remote.xml;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.StartingServicesEvent;
import org.more.services.remote.RemoteService;
/**
 * 当服务启动时，注册服务
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
class OnStartingServices implements EventListener<StartingServicesEvent> {
    private RemoteService service = null;
    public OnStartingServices(RemoteService service) {
        this.service = service;
    }
    public void onEvent(StartingServicesEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext app = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        app.regeditService(RemoteService.class, this.service);//注册服务
    }
}