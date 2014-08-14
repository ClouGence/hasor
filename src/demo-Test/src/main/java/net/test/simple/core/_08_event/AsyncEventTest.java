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
import net.hasor.core.Hasor;
import net.test.simple.core._08_event.listener.MyListener;
import org.junit.Test;
/**
 * 异步事件演示
 * @version : 2013-8-11F
 * @author 赵永春 (zyc@hasor.net)
 */
public class AsyncEventTest {
    @Test
    public void asyncEventTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>asyncEventTest<<--");
        AppContext appContext = Hasor.createAppContext();
        //
        String EventName = "MyEvent";
        //1.添加事件监听器
        appContext.addListener(EventName, new MyListener());
        //2.引发异步事件
        for (int i = 0; i < 10; i++)
            appContext.fireAsyncEvent(EventName, i);
        //3.由于是异步事件，因此下面这条日志会在事件喷出Log之前打印
        System.out.println("before Event do sth...");
        //
        Thread.sleep(5000);
    }
}