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
package org.hasor.context.event;
import org.hasor.context.AppEventListener;
import org.hasor.context.PhaseEventManager;
import org.hasor.context.Settings;
/**
 * 阶段事件管理器的实现类
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public class PhaseStandardEventManager extends StandardEventManager implements PhaseEventManager {
    public PhaseStandardEventManager(Settings settings) {
        super(settings);
    }
    @Override
    public void pushPhaseEvent(String eventType, AppEventListener eventListener) {
        // TODO Auto-generated method stub
    }
    @Override
    public void popPhaseEvent(String eventType, Object... objects) {
        // TODO Auto-generated method stub
    }
}