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
package net.test.simple.core._08_event;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.test.simple.core._08_event.listener.MyListener;
import org.junit.Test;
/**
 * 该例子演示了事件链。及由事件引发的事件。
 *   例子使用的是异步事件的方式进行演示。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class EventLinkTest {
    @Test
    public void syncEventTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>syncEventTest<<--");
        AppContext appContext = Hasor.createAppContext();
        //
        final String EventName = "MyEvent";//事件链的终端
        final String SeedEvent = "SeedEvent";//种子事件
        //1.添加事件监听器F
        appContext.addListener(EventName, new MyListener());
        appContext.addListener(SeedEvent, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                AppContext app = (AppContext) params[0];
                System.out.println("before MyEvent.");
                app.fireAsyncEvent(EventName, 1);
                app.fireAsyncEvent(EventName, 2);
            }
        });
        //2.引发种子事件
        appContext.fireAsyncEvent(SeedEvent, appContext);
        //3.由于是同步事件，因此下面这条日志会在事件处理完毕之后喷出
        System.out.println("before All Event.");
        Thread.sleep(1000);
    }
}