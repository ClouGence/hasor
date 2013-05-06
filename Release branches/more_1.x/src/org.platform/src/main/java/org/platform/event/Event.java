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
package org.platform.event;
import org.more.util.StringUtil;
import org.platform.Assert;
/**
 * 事件对象。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class Event {
    private String eventType = null;
    protected Event(String eventType) {
        this.eventType = eventType;
    }
    public String getEventType() {
        return eventType;
    }
    @Override
    public int hashCode() {
        return eventType.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event) {
            String str1 = ((Event) obj).eventType;
            String str2 = this.eventType;
            return StringUtil.eqUnCaseSensitive(str1, str2);
        } else
            return this.eventType.equals(obj);
    }
    //
    public static Event getEvent(String eventType) {
        Assert.isNotNull(eventType);
        return new Event(eventType);
    }
};