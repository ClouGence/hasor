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
package net.test.hasor.core._08_event;
import net.hasor.core.AppContext;
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import org.junit.Test;
/**
 * 仅会被执行一次的事件
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class OnesAsyncEventTest {
    @Test
    public void onesAsyncEventTest() throws InterruptedException {
        System.out.println("--->>onesAsyncEventTest<<--");
        AppContext appContext = Hasor.createAppContext();
        EventContext ec = appContext.getEnvironment().getEventContext();
        //
        String EventName = "MyEvent";
        //1.添加事件监听器
        ec.pushListener(EventName, new MyListener());
        //2.引发异步事件，虽然引发了10次事件但只有一次会被执行。
        for (int i = 0; i < 10; i++) {
            System.out.println("fireAsyncEvent - " + i);
            ec.fireAsyncEvent(EventName, i);
        }
        //3.由于事件引发一次是同步事件，因此下面这条日志会在事件处理完毕之后喷出
        System.out.println("before Event do sth...");
        //
        Thread.sleep(5000);
    }
}