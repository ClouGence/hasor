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
package org.more.workflow.event;
/**
 * Event事件监听器，通过实现该接口可以监听目标对象发出的事件。并且在事件的指定阶段处理事件。
 * Date : 2010-5-16
 * @author 赵永春
 */
public interface EventListener {
    /**处理事件。*/
    public void onEvent(Event event);
};