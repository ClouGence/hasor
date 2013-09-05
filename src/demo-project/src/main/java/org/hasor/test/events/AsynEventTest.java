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
package org.hasor.test.events;
import java.io.IOException;
import net.hasor.Hasor;
import net.hasor.context.AppContext;
import net.hasor.context.HasorEventListener;
import org.hasor.test.AbstractTestContext;
import org.junit.Test;
/**
 * 异步事件
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AsynEventTest extends AbstractTestContext {
    //
    protected void initContext(AppContext appContext) {
        /*注册事件监听器*/
        appContext.getEventManager().addEventListener(EventType.Type_B, new Test_EventListener(500));
    }
    @Test
    public void phaseEvent() throws IOException {
        for (int i = 0; i < 10; i++)
            /*发送同步事件*/
            this.getAppContext().getEventManager().doAsynEventIgnoreThrow(EventType.Type_B, i);
        System.out.println("after Event do sth...");
        /*由于test-config.xml中配置了5个事件线程池，因此异步事件将以5个一组执行。*/
        System.in.read();
    }
    /**事件监听器A*/
    private static class Test_EventListener implements HasorEventListener {
        private int sleep = 0;
        public Test_EventListener(int sleep) {
            this.sleep = sleep;
        }
        public void onEvent(String event, Object[] params) {
            try {
                Thread.sleep(this.sleep);
            } catch (InterruptedException e) {}
            System.out.println("Test_EventListener：onEvent :" + event + " \t" + Hasor.logString(params));
        }
    };
}
//
//
//
//
/**事件监听器*/
class ThrowEvent_EventListener implements HasorEventListener {
    private int sleep = 0;
    public ThrowEvent_EventListener(int sleep) {
        this.sleep = sleep;
    }
    public void onEvent(String event, Object[] params) {
        System.out.println("onEvent :" + event + " \t" + Hasor.logString(params));
        try {
            Thread.sleep(this.sleep);
        } catch (InterruptedException e) {}
    }
};
/**负责事件发送，美100毫秒发送一次同步事件*/
class ThrowEvent extends Thread {
    private String     eventName  = null;
    private AppContext appContext = null;
    public ThrowEvent(String eventName, AppContext appContext) {
        this.eventName = eventName;
        this.appContext = appContext;
    }
    public void run() {
        int i = 0;
        while (true)
            appContext.getEventManager().doSyncEventIgnoreThrow(this.eventName, i++);
    }
};