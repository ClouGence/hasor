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
import java.io.Serializable;
import java.util.Date;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 事件对象workflow系统中的所有事件都必须实现该接口，其子类可以决定事件具体有哪些。
 * 提示：事件和事件阶段都可以携带属性，它们之间的属性不会互相影响。
 * Date : 2010-5-24
 * @author 赵永春
 */
public abstract class Event implements Serializable, IAttribute {
    //========================================================================================Field
    private static final long  serialVersionUID = 8851774714640209392L;
    /**  */
    private final String       eventID;                                //事件ID
    private String             eventMessage;                           //事件信息
    private final Object       target;                                 //事件源对象
    private final AttBase      attMap           = new AttBase();
    private final Date         atTime           = new Date();          //发生时间
    private final EventPhase[] eventPhase;
    //==================================================================================Constructor
    /**
     * 创建Event对象。
     * @param eventID 事件ID
     * @param target 事件源对象
     */
    public Event(String eventID, Object target) {
        this(eventID, target, null);
    };
    /**
     * 创建Event对象。
     * @param eventID 事件ID
     * @param target 事件源对象
     * @param eventMessage 事件信息
     */
    public Event(String eventID, Object target, String eventMessage) {
        this.eventID = eventID;
        this.target = target;
        this.eventMessage = eventMessage;
        EventPhase[] tempPhase = this.createEventPhase();
        if (tempPhase == null)
            this.eventPhase = new EventPhase[] { new ProcessEventPhase() };
        else
            this.eventPhase = tempPhase;
        for (EventPhase phase : this.eventPhase)
            phase.setEvent(this);
    };
    //=========================================================================================Type
    /**初始化阶段事件。*/
    protected class InitEventPhase extends EventPhase implements InitPhase {
        public InitEventPhase() {
            super(InitPhase.class);
        };
    };
    /**事件处理之前阶段对象。*/
    protected class BeforeEventPhase extends EventPhase implements BeforePhase {
        public BeforeEventPhase() {
            super(BeforePhase.class);
        };
    };
    /**事件处理时阶段对象。*/
    protected class ProcessEventPhase extends EventPhase implements ProcessPhase {
        public ProcessEventPhase() {
            super(ProcessPhase.class);
        };
    };
    /**事件处理之后阶段对象。*/
    protected class AfterEventPhase extends EventPhase implements AfterPhase {
        public AfterEventPhase() {
            super(AfterPhase.class);
        };
    };
    /**销毁阶段。*/
    protected class DestroyEventPhase extends EventPhase implements DestroyPhase {
        public DestroyEventPhase() {
            super(DestroyPhase.class);
        };
    };
    //==========================================================================================Job
    /**
     * 继承Event的子类需要决定子类型Event拥有哪些阶段。
     * 如果子类实现该方法的返回值是null则Event使用Event.ProcessEventPhase类型作为默认值
     */
    protected abstract EventPhase[] createEventPhase();
    /**获取当前事件所具备的事件阶段。*/
    public EventPhase[] getEventPhase() {
        return this.eventPhase;
    };
    /** 获取事件ID。 */
    public String getEventID() {
        return this.eventID;
    };
    /** 获取事件的描述信息。 */
    public String getEventMessage() {
        return this.eventMessage;
    };
    /** 获取事件发生的对象。 */
    public Object getTarget() {
        return this.target;
    };
    /** 获取事件发生时间。 */
    public Date getAtTime() {
        return (Date) this.atTime.clone();
    };
    public void clearAttribute() {
        this.attMap.clearAttribute();
    };
    @Override
    public boolean contains(String name) {
        return this.attMap.contains(name);
    };
    @Override
    public Object getAttribute(String name) {
        return this.attMap.getAttribute(name);
    };
    @Override
    public String[] getAttributeNames() {
        return this.attMap.getAttributeNames();
    };
    @Override
    public void removeAttribute(String name) {
        this.attMap.removeAttribute(name);
    };
    @Override
    public void setAttribute(String name, Object value) {
        this.attMap.setAttribute(name, value);
    };
};