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
 * 事件对象，用于标记一个特定时间发生的事情，处理该事件时将会分为处理前，处理中，处理后。
 * Date : 2010-5-16
 * @author 赵永春
 */
public abstract class Event implements Serializable, IAttribute {
    //========================================================================================Field
    private static final long serialVersionUID = 8851774714640209392L;
    /**  */
    private final String      eventID;                                //事件ID
    private String            eventMessage;                           //事件信息
    private final Object      target;                                 //事件源对象
    private final Date        atTime           = new Date();          //发生时间
    private final AttBase     attMap           = new AttBase();       //用于保存事件的属性对象。
    //==================================================================================Constructor
    /**
     * 创建Event对象。
     * @param eventID 事件ID
     * @param target 事件源对象
     */
    public Event(String eventID, Object target) {
        this.eventID = eventID;
        this.target = target;
        this.eventMessage = null;
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
    };
    //==========================================================================================Job
    /** 获取事件ID。 */
    public String getEventID() {
        return eventID;
    };
    /** 获取事件的描述信息。 */
    public String getEventMessage() {
        return eventMessage;
    };
    /** 获取事件发生的对象。 */
    public Object getTarget() {
        return target;
    };
    /** 获取事件发生时间。 */
    public Date getAtTime() {
        return (Date) atTime.clone();
    }
    //==========================================================================================Job
    @Override
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