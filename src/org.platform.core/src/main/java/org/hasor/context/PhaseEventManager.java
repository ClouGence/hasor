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
package org.hasor.context;
/**
 * 阶段事件管理器
 * @version : 2013-5-6
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PhaseEventManager extends EventManager {
    /**抛出阶段性事件。该类事件抛出之后只有等待Hasor在执行相应阶段时才会处理对应的事件。<br/>
     * 该方法和{@link PhaseEventManager#addEventListener(String, AppEventListener)}方法不同。
     * pushPhaseEvent方法注册的时间监听器当收到一次阶段性事件之后会被自动删除。*/
    public void pushPhaseEvent(String eventType, AppEventListener eventListener);
    /**弹出阶段性事件。*/
    public void popPhaseEvent(String eventType, Object... objects);
}