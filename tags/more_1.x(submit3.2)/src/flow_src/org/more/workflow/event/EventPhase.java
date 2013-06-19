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
import org.more.util.attribute.IAttribute;
/**
 * 表示一个事件的所处阶段，子类可以通过这个阶段对象在不用事件处理区间完成数据共享。
 * Date : 2010-5-24
 * @author 赵永春
 */
public abstract class EventPhase {
    //========================================================================================Field
    private final Class<? extends PhaseMark> markType; //阶段标记
    private Event                            event;
    //==================================================================================Constructor
    protected EventPhase(Class<? extends PhaseMark> markType) {
        this.markType = markType;
    };
    //==========================================================================================Job
    /**获取事件处理阶段的类型。*/
    public Class<? extends PhaseMark> getEventPhaseType() {
        return this.markType;
    };
    void setEvent(Event event) {
        this.event = event;
    };
    /**获取该阶段所处的事件。*/
    public Event getEvent() {
        return event;
    };
    /**获取该阶段所使用的IAttribute对象。*/
    public IAttribute getAttribute() {
        return this.event;
    };
};