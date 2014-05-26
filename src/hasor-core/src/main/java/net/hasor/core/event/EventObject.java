/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
/**
 * 
 * @version : 2014-3-13
 * @author 赵永春(zyc@hasor.net)
 */
public class EventObject {
    private String            eventType = null;
    private boolean           sync      = true; //默认是同步的
    private Object[]          params    = null;
    private EventCallBackHook callBack  = null;
    //
    public EventObject(String eventType, boolean sync) {
        this.eventType = eventType;
        this.sync = sync;
    }
    //
    public String getEventType() {
        return eventType;
    }
    public Object[] getParams() {
        return params;
    }
    public boolean isSync() {
        return sync;
    }
    public void addParams(Object[] params) {
        this.params = params;
    }
    public void setCallBack(EventCallBackHook callBack) {
        this.callBack = callBack;
    }
    public EventCallBackHook getCallBack() {
        return this.callBack;
    }
}