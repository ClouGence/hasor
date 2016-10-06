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
package net.hasor.plugins.spring.event;
import org.springframework.context.ApplicationEvent;
/**
 * Spring中的Hasor事件
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringHasorEvent extends ApplicationEvent implements EventType {
    private static final long serialVersionUID = 4739385371879985426L;
    private String eventType;
    //
    public SpringHasorEvent(Object source) {
        this(null, source);
    }
    public SpringHasorEvent(String eventType, Object source) {
        super(source);
        this.eventType = eventType;
    }
    @Override
    public String getEventType() {
        if (this.eventType == null) {
            return getClass().getSimpleName();
        }
        return this.eventType;
    }
}