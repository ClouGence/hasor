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
package org.more.hypha.commons;
import org.more.core.event.EventManager;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.context.AbstractDefineResource;
/**
* 该类管理事件分发的基类，该类是{@link EventManager}接口的实现类。
* @version 2010-10-10
* @author 赵永春 (zyc@byshell.org)
*/
public class AbstractEventManager extends org.more.core.event.AbstractEventManager {
    private static final Log       log            = LogFactory.getLog(AbstractEventManager.class);
    private AbstractDefineResource defineResource = null;
    /*------------------------------------------------------------------------------*/
    public void init(AbstractDefineResource defineResource) {
        if (defineResource != null)
            log.info("init EventManager, AbstractDefineResource = {%0}", defineResource);
        else
            log.warning("init EventManager, AbstractDefineResource is null.");
        this.defineResource = defineResource;
    }
    /**获取{@link AbstractDefineResource}。*/
    protected AbstractDefineResource getDefineResource() {
        return this.defineResource;
    }
}