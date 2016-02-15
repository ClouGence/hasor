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
package net.test.hasor.spring.event.tospring;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import net.hasor.plugins.spring.event.EventType;
/**
 * 接受来自Hasor发送的事件
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class ReceiveEventListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        String eventType = event.getClass().getSimpleName();
        if (event instanceof EventType) {
            eventType = ((EventType) event).getEventType();
        }
        System.out.println("!!!!!!!!!! \t\tthis Event form Hasor -> Type:" + eventType + ", Source: " + event.getSource());
    }
}