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
import org.more.workflow.context.FlashSession;
import org.more.workflow.event.Event;
import org.more.workflow.event.EventPhase;
/**
 * 将运行状态闪存时。
 * Date : 2010-5-21
 * @author 赵永春
 */
public class SaveStateEvent extends Event {
    /**  */
    private static final long serialVersionUID = -4046949021700949565L;
    private FlashSession      flashSession     = null;
    /**将运行状态闪存时。*/
    public SaveStateEvent(Object targetMode, FlashSession flashSession) {
        super("SaveStateEvent", targetMode);
        this.flashSession = flashSession;
    };
    @Override
    protected EventPhase[] createEventPhase() {
        return null;
    };
    public FlashSession getFlashSession() {
        return flashSession;
    };
};