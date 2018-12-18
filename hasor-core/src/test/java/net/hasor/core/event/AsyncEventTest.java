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
import net.hasor.core.AppContext;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
/**
 * 仅会被执行一次的事件
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class AsyncEventTest {
    @Test
    public void asyncEventTest() throws InterruptedException {
        AppContext appContext = Hasor.createAppContext();
        EventContext ec = appContext.getEnvironment().getEventContext();
        //
        String EventName = "MyEvent";
        //1.添加事件监听器
        final CopyOnWriteArraySet<String> eventDataSet = new CopyOnWriteArraySet<String>();
        ec.addListener(EventName, new EventListener<Object>() {
            @Override
            public void onEvent(String event, Object eventData) throws Throwable {
                eventDataSet.add(event + eventData);
                Thread.sleep(110);
            }
        });
        //2.引发异步事件
        ArrayList<String> eventData = new ArrayList<String>();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            eventData.add(EventName + i);
            ec.fireAsyncEvent(EventName, i);
        }
        while (eventDataSet.size() != 50) {
            Thread.sleep(500);
            System.out.println("wait for Event Queue.");
        }
        long endTime = System.currentTimeMillis();
        //
        //3.check事件必须都执行到
        for (String key : eventData) {
            assert eventDataSet.contains(key);
        }
        assert (endTime - startTime) < (100 * 50);
    }
    @Test
    public void onesAsyncEventTest() throws InterruptedException {
        AppContext appContext = Hasor.createAppContext();
        EventContext ec = appContext.getEnvironment().getEventContext();
        //
        String EventName = "MyEvent";
        //1.添加事件监听器
        final CopyOnWriteArraySet<String> eventDataSet = new CopyOnWriteArraySet<String>();
        ec.pushListener(EventName, new EventListener<Object>() {
            @Override
            public void onEvent(String event, Object eventData) throws Throwable {
                eventDataSet.add(event + eventData);
                Thread.sleep(110);
            }
        });
        //2.引发异步事件
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            ec.fireAsyncEvent(EventName, i);
        }
        long endTime = System.currentTimeMillis();
        Thread.sleep(1000);
        //
        //3.check
        assert eventDataSet.size() == 1;
        assert (endTime - startTime) < 110;
    }
}