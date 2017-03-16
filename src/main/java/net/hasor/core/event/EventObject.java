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
package net.hasor.core.event;
import net.hasor.core.EventCallBackHook;
import net.hasor.core.FireType;
/**
 * 用于封装事件对象。
 * @version : 2014-3-13
 * @author 赵永春(zyc@hasor.net)
 */
public class EventObject<T> {
    private String               eventType = null;
    private FireType             fireType  = null;
    private T                    eventData = null;
    private EventCallBackHook<T> callBack  = null;
    //
    public EventObject(final String eventType, final FireType fireType) {
        this.eventType = eventType;
        this.fireType = fireType;
    }
    /**获得事件类型。*/
    public String getEventType() {
        return this.eventType;
    }
    public FireType getFireType() {
        return this.fireType;
    }
    public void setFireType(FireType fireType) {
        this.fireType = fireType;
    }
    public void setCallBack(final EventCallBackHook<T> callBack) {
        this.callBack = callBack;
    }
    public EventCallBackHook<T> getCallBack() {
        return this.callBack;
    }
    public T getEventData() {
        return eventData;
    }
    public void setEventData(T eventData) {
        this.eventData = eventData;
    }
}