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
package org.more.hypha.aop.assembler;
import org.more.core.error.InitializationException;
import org.more.core.log.ILog;
import org.more.core.log.LogFactory;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.aop.AopService;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.InitEvent;
/**
 * aop的初始化EventException
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class OnInit implements EventListener<InitEvent> {
    private static ILog log     = LogFactory.getLog(OnInit.class);
    private AopService  service = null;
    public OnInit(AopService service) {
        this.service = service;
    }
    public void onEvent(InitEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        if (this.service == null)
            throw new InitializationException("注册aop服务错误!");
        context.regeditService(AopService.class, this.service);
        //注册扩展点
        context.getExpandPointManager().regeditExpandPoint("moreAopSupport_1", new AopLoadClassPoint());
        context.getExpandPointManager().regeditExpandPoint("moreAopSupport_2", new AopCreateBeanPoint());
        log.info("hypha.aop init OK!");
    };
}