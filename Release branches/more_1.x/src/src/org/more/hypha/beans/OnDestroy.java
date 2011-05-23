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
package org.more.hypha.beans;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.context.DestroyEvent;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 该事件的目的是清除创建Bean的引擎。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class OnDestroy implements EventListener<DestroyEvent> {
    private static ILog log = LogFactory.getLog(OnDestroy.class);
    public void onEvent(DestroyEvent event, Sequence sequence) {
        log.debug("hypha.beans On Destroy!");
    }
}