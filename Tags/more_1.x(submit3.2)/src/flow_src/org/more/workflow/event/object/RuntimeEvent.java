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
package org.more.workflow.event.object;
import org.more.workflow.event.Event;
import org.more.workflow.event.EventPhase;
/**
 * 该事件中各个阶段对应了Runtime中各个阶段.
 * Date : 2010-6-19
 * @author 赵永春
 */
public class RuntimeEvent extends Event {
    /**  */
    private static final long serialVersionUID = -7413555001709971458L;
    /**将运行状态从闪存状态恢复时。*/
    public RuntimeEvent(Object targetMode) {
        super("RuntimeEvent", targetMode);
    };
    @Override
    protected EventPhase[] createEventPhase() {
        return new EventPhase[] { new Event.InitEventPhase(), new Event.BeforeEventPhase(), new Event.ProcessEventPhase(), new Event.AfterEventPhase() };
    };
};